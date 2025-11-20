package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.controller.ApiService;
import hn.uth.proyecto_tallermecanico.controller.RetrofitClient;
import hn.uth.proyecto_tallermecanico.model.Cliente;
import hn.uth.proyecto_tallermecanico.model.ORDSCollectionResponse;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import retrofit2.Response;

@ApplicationScoped
public class ClienteRepositoryImpl implements ClienteRepository {

    // Se obtiene el cliente Retrofit (Singleton)
    private final ApiService apiService = RetrofitClient.getInstance().getApiService();

    @Override
    public List<Cliente> findAll() {
        try {
            Response<ORDSCollectionResponse<Cliente>> response = apiService.getClientes().execute();

            if (response.isSuccessful() && response.body() != null) {
                // Se extrae la lista de clientes del campo "items"
                return response.body().getItems();
            }
        } catch (IOException e) {
            System.err.println("Error de I/O al buscar clientes: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public Cliente findById(String docIdentidad) {
        try {
            Response<Cliente> response = apiService.getCliente(docIdentidad).execute();
            if (response.isSuccessful()) {
                return response.body();
            }
        } catch (IOException e) {
            System.err.println("Error de I/O al buscar cliente: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void save(Cliente cliente) {
        try {
            Response<Void> response;

            // Lógica para determinar si es PUT o POST: si ya tiene el flag 'activo' se asume PUT/Edición.
            if (cliente.getActivo() != null) {
                response = apiService.actualizarCliente(cliente.getDoc_identidad(), cliente).execute();
            } else {
                response = apiService.crearCliente(cliente).execute();
            }

            if (!response.isSuccessful()) {
                throw new RuntimeException("API Error: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Connection Error: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String docIdentidad) {
        try {
            Response<Void> response = apiService.eliminarCliente(docIdentidad).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("API Error: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Connection Error: " + e.getMessage(), e);
        }
    }
}