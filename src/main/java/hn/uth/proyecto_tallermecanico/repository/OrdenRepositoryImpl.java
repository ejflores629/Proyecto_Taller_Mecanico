package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.controller.ApiService;
import hn.uth.proyecto_tallermecanico.controller.RetrofitClient;
import hn.uth.proyecto_tallermecanico.model.CountResponse;
import hn.uth.proyecto_tallermecanico.model.ORDSCollectionResponse;
import hn.uth.proyecto_tallermecanico.model.Orden;
import jakarta.enterprise.context.ApplicationScoped;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class OrdenRepositoryImpl implements OrdenRepository {

    private final ApiService apiService = RetrofitClient.getInstance().getApiService();

    @Override
    public List<Orden> findAll() {
        return findRange(0, 1000);
    }

    @Override
    public List<Orden> findRange(int offset, int limit) {
        try {
            Response<ORDSCollectionResponse<Orden>> response = apiService.getOrdenes(offset, limit).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getItems();
            }
        } catch (IOException e) {
            System.err.println("Error listando órdenes: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public long count() {
        try {
            Response<CountResponse> response = apiService.getTotalOrdenes().execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getCount();
            }
        } catch (IOException e) {
            System.err.println("Error contando órdenes: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public Orden findById(String numeroOrden) {
        if (numeroOrden == null || numeroOrden.trim().isEmpty()) return null;
        try {
            // CORRECCIÓN: Recibimos la colección para verificar si está vacía
            Response<ORDSCollectionResponse<Orden>> response = apiService.getOrden(numeroOrden).execute();
            if (response.isSuccessful() && response.body() != null) {
                List<Orden> lista = response.body().getItems();
                if (lista != null && !lista.isEmpty()) {
                    return lista.get(0);
                }
            }
        } catch (IOException e) {
            System.err.println("Error buscando orden: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void create(Orden orden) {
        // Validación: No duplicar número de orden
        Orden existente = findById(orden.getNumero_orden());
        if (existente != null) {
            throw new RuntimeException("⚠️ Ya existe una orden ACTIVA con el número " + orden.getNumero_orden());
        }

        try {
            Response<Void> response = apiService.crearOrden(orden).execute();
            checkResponse(response, "Error al crear la orden");
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Orden orden) {
        try {
            Response<Void> response = apiService.actualizarOrden(orden.getNumero_orden(), orden).execute();
            checkResponse(response, "Error al actualizar la orden");
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String numeroOrden) {
        try {
            Response<Void> response = apiService.eliminarOrden(numeroOrden).execute();
            checkResponse(response, "Error al anular la orden");
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    private void checkResponse(Response<?> response, String msg) throws IOException {
        if (!response.isSuccessful()) {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
            if(errorBody.contains("<!DOCTYPE html>")) errorBody = "Error del Servidor (HTML)";
            System.err.println("❌ API Error (" + response.code() + "): " + errorBody);
            throw new RuntimeException(msg + " (" + response.code() + ")");
        }
    }
}