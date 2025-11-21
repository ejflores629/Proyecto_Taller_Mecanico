package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.controller.ApiService;
import hn.uth.proyecto_tallermecanico.controller.RetrofitClient;
import hn.uth.proyecto_tallermecanico.model.ORDSCollectionResponse;
import hn.uth.proyecto_tallermecanico.model.DetalleOrden;
import jakarta.enterprise.context.ApplicationScoped;
import retrofit2.Response;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class DetalleOrdenRepositoryImpl implements DetalleOrdenRepository {

    private final ApiService apiService = RetrofitClient.getInstance().getApiService();

    @Override
    public List<DetalleOrden> findByOrden(String numeroOrden) {
        try {
            // offset 0, limit 1000 para traer todos los detalles de esta orden
            Response<ORDSCollectionResponse<DetalleOrden>> response = apiService.getDetallesPorOrden(numeroOrden, 0, 1000).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getItems();
            }
        } catch (IOException e) {
            System.err.println("Error cargando mano de obra: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public void create(DetalleOrden detalle) {
        try {
            Response<Void> response = apiService.crearDetalleOrden(detalle).execute();
            if (!response.isSuccessful()) throw new RuntimeException("Error API: " + response.code());
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión", e);
        }
    }

    @Override
    public void delete(Long idDetalle) {
        try {
            Response<Void> response = apiService.eliminarDetalleOrden(idDetalle).execute();
            if (!response.isSuccessful()) throw new RuntimeException("Error API: " + response.code());
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión", e);
        }
    }
}