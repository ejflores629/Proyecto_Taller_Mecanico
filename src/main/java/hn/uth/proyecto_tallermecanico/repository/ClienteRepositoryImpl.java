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
            Response<ORDSCollectionResponse<Cliente>> response = apiService.getClientes(offset, limit).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getItems();
            }
        } catch (IOException e) {
            System.err.println("Error conexión clientes: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public long count() {
        try {
            Response<CountResponse> response = apiService.getTotalClientes().execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getCount();
            }
        } catch (IOException e) {
            System.err.println("Error conteo clientes: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public Cliente findById(String docIdentidad) {
        if (docIdentidad == null || docIdentidad.trim().isEmpty()) return null;
        try {
            Response<ORDSCollectionResponse<Cliente>> response = apiService.getCliente(docIdentidad).execute();
            if (response.isSuccessful() && response.body() != null) {
                List<Cliente> lista = response.body().getItems();
                // Verificamos si la lista tiene elementos
                if (lista != null && !lista.isEmpty()) {
                    return lista.get(0);
                }
            }
        } catch (IOException e) {
            System.err.println("Error buscar cliente: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void create(Cliente cliente) {
        // Validación de existencia activa
        Cliente existente = findById(cliente.getDoc_identidad());
        if (existente != null) {
            throw new RuntimeException("⚠️ Ya existe un cliente ACTIVO con el DNI " + cliente.getDoc_identidad());
        }
        try {
            Response<Void> response = apiService.crearCliente(cliente).execute();
            checkResponse(response, "Error al registrar cliente");
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Cliente cliente) {
        try {
            Response<Void> response = apiService.actualizarCliente(cliente.getDoc_identidad(), cliente).execute();
            checkResponse(response, "Error al actualizar cliente");
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String docIdentidad) {
        try {
            Response<Void> response = apiService.eliminarCliente(docIdentidad).execute();
            checkResponse(response, "Error al eliminar cliente");
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    private void checkResponse(Response<?> response, String errorMessage) throws IOException {
        if (!response.isSuccessful()) {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
            System.err.println("❌ API Error (" + response.code() + "): " + errorBody);
            throw new RuntimeException(errorMessage + " (Código " + response.code() + ")");
        }
    }
}