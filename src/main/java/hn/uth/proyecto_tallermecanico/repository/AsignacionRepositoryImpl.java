package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.controller.ApiService;
import hn.uth.proyecto_tallermecanico.controller.RetrofitClient;
import hn.uth.proyecto_tallermecanico.model.Asignacion;
import hn.uth.proyecto_tallermecanico.model.ORDSCollectionResponse;
import jakarta.enterprise.context.ApplicationScoped;
import retrofit2.Response;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class AsignacionRepositoryImpl implements AsignacionRepository {

    private final ApiService apiService = RetrofitClient.getInstance().getApiService();

    @Override
    public List<Asignacion> findByOrden(String numeroOrden) {
        try {
            Response<ORDSCollectionResponse<Asignacion>> response = apiService.getAsignacionesPorOrden(numeroOrden, 0, 100).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getItems();
            }
        } catch (IOException e) {
            System.err.println("Error buscando asignaciones: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public void create(Asignacion asignacion) {
        try {
            Response<Void> response = apiService.crearAsignacion(asignacion).execute();
            if (!response.isSuccessful()) throw new RuntimeException("Error API: " + response.code());
        } catch (IOException e) {
            throw new RuntimeException("Error conexión", e);
        }
    }

    @Override
    public void delete(Long idAsignacion) {
        try {
            Response<Void> response = apiService.eliminarAsignacion(idAsignacion).execute();
            if (!response.isSuccessful()) throw new RuntimeException("Error API: " + response.code());
        } catch (IOException e) {
            throw new RuntimeException("Error conexión", e);
        }
    }
}