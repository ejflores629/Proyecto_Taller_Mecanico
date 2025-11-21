package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.controller.ApiService;
import hn.uth.proyecto_tallermecanico.controller.RetrofitClient;
import hn.uth.proyecto_tallermecanico.model.CountResponse;
import hn.uth.proyecto_tallermecanico.model.ORDSCollectionResponse;
import hn.uth.proyecto_tallermecanico.model.Repuesto;
import jakarta.enterprise.context.ApplicationScoped;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class RepuestoRepositoryImpl implements RepuestoRepository {

    private final ApiService apiService = RetrofitClient.getInstance().getApiService();

    @Override
    public List<Repuesto> findAll() {
        return findRange(0, 1000);
    }

    @Override
    public List<Repuesto> findRange(int offset, int limit) {
        try {
            Response<ORDSCollectionResponse<Repuesto>> response = apiService.getRepuestos(offset, limit).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getItems();
            }
        } catch (IOException e) {
            System.err.println("Error conexión repuestos: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public long count() {
        try {
            Response<CountResponse> response = apiService.getTotalRepuestos().execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getCount();
            }
        } catch (IOException e) {
            System.err.println("Error conteo repuestos: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public Repuesto findById(String sku) {
        if (sku == null || sku.trim().isEmpty()) return null;

        try {
            // CORRECCIÓN: Recibimos la "Caja" (Colección)
            Response<ORDSCollectionResponse<Repuesto>> response = apiService.getRepuesto(sku).execute();

            if (response.isSuccessful() && response.body() != null) {
                List<Repuesto> lista = response.body().getItems();
                // Validamos si la caja trae algo adentro
                if (lista != null && !lista.isEmpty()) {
                    return lista.get(0);
                }
            }
        } catch (IOException e) {
            System.err.println("Error buscar repuesto: " + e.getMessage());
        }
        // Si la lista está vacía o falla, retornamos null (No existe)
        return null;
    }

    @Override
    public void create(Repuesto repuesto) {
        // VALIDACIÓN DE NEGOCIO: Evitar duplicados activos
        Repuesto existente = findById(repuesto.getCodigo_sku());

        if (existente != null) {
            throw new RuntimeException("⚠️ Ya existe un repuesto ACTIVO con el SKU " + repuesto.getCodigo_sku());
        }

        try {
            Response<Void> response = apiService.crearRepuesto(repuesto).execute();
            checkResponse(response, "Error al registrar repuesto");
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Repuesto repuesto) {
        try {
            Response<Void> response = apiService.actualizarRepuesto(repuesto.getCodigo_sku(), repuesto).execute();
            checkResponse(response, "Error al actualizar repuesto");
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String sku) {
        try {
            Response<Void> response = apiService.eliminarRepuesto(sku).execute();
            checkResponse(response, "Error al eliminar repuesto");
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    private void checkResponse(Response<?> response, String errorMessage) throws IOException {
        if (!response.isSuccessful()) {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
            if(errorBody.contains("<!DOCTYPE html>")) {
                errorBody = "Error HTML del servidor (Posible 500 o restricción de BD)";
            }
            System.err.println("❌ API Error (" + response.code() + "): " + errorBody);
            throw new RuntimeException(errorMessage + " (Código " + response.code() + ")");
        }
    }
}