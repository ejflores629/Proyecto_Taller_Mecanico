package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.controller.ApiService;
import hn.uth.proyecto_tallermecanico.controller.RetrofitClient;
import hn.uth.proyecto_tallermecanico.model.Cliente;
import hn.uth.proyecto_tallermecanico.model.CountResponse;
import hn.uth.proyecto_tallermecanico.model.ORDSCollectionResponse;
import jakarta.enterprise.context.ApplicationScoped;
import retrofit2.Response;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class ClienteRepositoryImpl implements ClienteRepository {

    private final ApiService apiService = RetrofitClient.getInstance().getApiService();

    @Override
    public List<Cliente> findAll() {
        return findRange(0, 1000);
    }

    @Override
    public List<Cliente> findRange(int offset, int limit) {
        try {
            //  paginación
            Response<ORDSCollectionResponse<Cliente>> response = apiService.getClientes(offset, limit).execute();

            if (response.isSuccessful() && response.body() != null) {
                return response.body().getItems();
            }
        } catch (IOException e) {
            System.err.println("Error al obtener rango de clientes: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public long count() {
        try {
            // Llamada al endpoint de conteo total
            Response<CountResponse> response = apiService.getTotalClientes().execute();

            if (response.isSuccessful() && response.body() != null) {
                return response.body().getCount();
            }
        } catch (IOException e) {
            System.err.println("Error al obtener conteo de clientes: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public Cliente findById(String docIdentidad) {
        try {
            Response<Cliente> response = apiService.getCliente(docIdentidad).execute();
            if (response.isSuccessful()) {
                return response.body();
            }
        } catch (IOException e) {
            System.err.println("Error al buscar cliente: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void save(Cliente cliente) {
        try {
            Response<Void> response;
            // Si tiene activo='S' o 'N', asumimos que ya existía y es un Update,
            // Pero tu API manejaba Upsert en POST.
            // Para estar seguros, usamos la lógica del Bean: si viene del modal de edición (update) o nuevo (create).
            // Aquí simplemente llamamos al endpoint correspondiente.

            if (cliente.getActivo() != null && !cliente.getActivo().isEmpty()) {
                // PUT
                response = apiService.actualizarCliente(cliente.getDoc_identidad(), cliente).execute();
            } else {
                // POST (Upsert inteligente en BD)
                response = apiService.crearCliente(cliente).execute();
            }

            if (!response.isSuccessful()) {
                throw new RuntimeException("Error API: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String docIdentidad) {
        try {
            Response<Void> response = apiService.eliminarCliente(docIdentidad).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error API: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión al eliminar: " + e.getMessage(), e);
        }
    }
}