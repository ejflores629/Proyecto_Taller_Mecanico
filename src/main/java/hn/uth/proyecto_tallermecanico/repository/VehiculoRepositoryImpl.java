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
            System.err.println("Error obtener vehículos: " + e.getMessage());
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
            System.err.println("Error contar vehículos: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public Vehiculo findById(String placa) {
        if (placa == null || placa.trim().isEmpty()) return null;
        try {
            // CAMBIO: Recibimos colección
            Response<ORDSCollectionResponse<Vehiculo>> response = apiService.getVehiculo(placa).execute();
            if (response.isSuccessful() && response.body() != null) {
                List<Vehiculo> lista = response.body().getItems();
                if (lista != null && !lista.isEmpty()) {
                    return lista.get(0);
                }
            }
        } catch (IOException e) {
            System.err.println("Error buscar vehículo: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void save(Vehiculo vehiculo) {
        // IMPORTANTE: Aquí mantuve 'save' genérico en el ejemplo anterior,
        // pero deberíamos separarlo si quieres la misma validación estricta.
        // Por simplicidad y consistencia con lo que ya tenías, aplicamos la lógica interna:

        if (vehiculo.getActivo() != null && !vehiculo.getActivo().isEmpty()) {
            // UPDATE
            try {
                Response<Void> response = apiService.actualizarVehiculo(vehiculo.getPlaca(), vehiculo).execute();
                checkResponse(response, "Error actualizando vehículo");
            } catch(IOException e) { throw new RuntimeException(e); }
        } else {
            // CREATE - Validamos existencia
            Vehiculo existente = findById(vehiculo.getPlaca());
            if (existente != null) {
                throw new RuntimeException("⚠️ Ya existe un vehículo ACTIVO con la placa " + vehiculo.getPlaca());
            }
            try {
                Response<Void> response = apiService.crearVehiculo(vehiculo).execute();
                checkResponse(response, "Error creando vehículo");
            } catch(IOException e) { throw new RuntimeException(e); }
        }
    }

    @Override
    public void delete(String placa) {
        try {
            Response<Void> response = apiService.eliminarVehiculo(placa).execute();
            checkResponse(response, "Error eliminando vehículo");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkResponse(Response<?> response, String msg) throws IOException {
        if (!response.isSuccessful()) {
            throw new RuntimeException(msg + " Code: " + response.code());
        }
    }
}