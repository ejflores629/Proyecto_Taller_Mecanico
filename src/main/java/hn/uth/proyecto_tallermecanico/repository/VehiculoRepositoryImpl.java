package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.controller.ApiService;
import hn.uth.proyecto_tallermecanico.controller.RetrofitClient;
import hn.uth.proyecto_tallermecanico.model.CountResponse;
import hn.uth.proyecto_tallermecanico.model.ORDSCollectionResponse;
import hn.uth.proyecto_tallermecanico.model.Vehiculo;
import jakarta.enterprise.context.ApplicationScoped;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class VehiculoRepositoryImpl implements VehiculoRepository {

    private final ApiService apiService = RetrofitClient.getInstance().getApiService();

    @Override
    public List<Vehiculo> findAll() {
        return findRange(0, 1000);
    }

    @Override
    public List<Vehiculo> findRange(int offset, int limit) {
        try {
            Response<ORDSCollectionResponse<Vehiculo>> response = apiService.getVehiculos(offset, limit).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getItems();
            }
        } catch (IOException e) {
            System.err.println("Error al obtener vehículos: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public long count() {
        try {
            Response<CountResponse> response = apiService.getTotalVehiculos().execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getCount();
            }
        } catch (IOException e) {
            System.err.println("Error al contar vehículos: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public Vehiculo findById(String placa) {
        try {
            Response<Vehiculo> response = apiService.getVehiculo(placa).execute();
            if (response.isSuccessful()) {
                return response.body();
            }
        } catch (IOException e) {
            System.err.println("Error al buscar vehículo: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void save(Vehiculo vehiculo) {
        try {
            Response<Void> response;
            // Si tiene 'activo' poblado, asumimos que es una actualización
            if (vehiculo.getActivo() != null && !vehiculo.getActivo().isEmpty()) {
                response = apiService.actualizarVehiculo(vehiculo.getPlaca(), vehiculo).execute();
            } else {
                response = apiService.crearVehiculo(vehiculo).execute();
            }

            if (!response.isSuccessful()) {
                throw new RuntimeException("Error API Vehículos: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String placa) {
        try {
            Response<Void> response = apiService.eliminarVehiculo(placa).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error API al eliminar: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión al eliminar: " + e.getMessage(), e);
        }
    }
}