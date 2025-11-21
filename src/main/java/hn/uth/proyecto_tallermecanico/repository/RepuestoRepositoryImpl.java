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
            System.err.println("Error al obtener repuestos: " + e.getMessage());
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
            System.err.println("Error al contar repuestos: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public Repuesto findById(String sku) {
        try {
            Response<Repuesto> response = apiService.getRepuesto(sku).execute();
            if (response.isSuccessful()) {
                return response.body();
            }
        } catch (IOException e) {
            System.err.println("Error al buscar repuesto: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void save(Repuesto repuesto) {
        try {
            Response<Void> response;
            // Si 'activo' no es nulo, asumimos actualización
            if (repuesto.getActivo() != null && !repuesto.getActivo().isEmpty()) {
                response = apiService.actualizarRepuesto(repuesto.getCodigo_sku(), repuesto).execute();
            } else {
                response = apiService.crearRepuesto(repuesto).execute();
            }

            if (!response.isSuccessful()) {
                throw new RuntimeException("Error API Repuestos: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String sku) {
        try {
            Response<Void> response = apiService.eliminarRepuesto(sku).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error API al eliminar: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión al eliminar: " + e.getMessage(), e);
        }
    }
}