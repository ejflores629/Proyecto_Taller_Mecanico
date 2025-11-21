package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.controller.ApiService;
import hn.uth.proyecto_tallermecanico.controller.RetrofitClient;
import hn.uth.proyecto_tallermecanico.model.ORDSCollectionResponse;
import hn.uth.proyecto_tallermecanico.model.OrdenRepuesto;
import jakarta.enterprise.context.ApplicationScoped;
import retrofit2.Response;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class OrdenRepuestoRepositoryImpl implements OrdenRepuestoRepository {

    private final ApiService apiService = RetrofitClient.getInstance().getApiService();

    @Override
    public List<OrdenRepuesto> findByOrden(String numeroOrden) {
        try {
            Response<ORDSCollectionResponse<OrdenRepuesto>> response = apiService.getRepuestosDeOrden(numeroOrden).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getItems();
            }
        } catch (IOException e) {
            System.err.println("Error cargando repuestos de orden: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public void addRepuesto(String numeroOrden, OrdenRepuesto item) {
        try {
            Response<Void> response = apiService.agregarRepuestoAOrden(numeroOrden, item.getCodigo_sku(), item).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error API: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión", e);
        }
    }

    @Override
    public void removeRepuesto(String numeroOrden, String sku) {
        try {
            Response<Void> response = apiService.quitarRepuestoDeOrden(numeroOrden, sku).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error API: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión", e);
        }
    }
}