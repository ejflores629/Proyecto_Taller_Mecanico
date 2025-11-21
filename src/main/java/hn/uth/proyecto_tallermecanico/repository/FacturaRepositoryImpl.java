package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.controller.ApiService;
import hn.uth.proyecto_tallermecanico.controller.RetrofitClient;
import hn.uth.proyecto_tallermecanico.model.CountResponse;
import hn.uth.proyecto_tallermecanico.model.Factura;
import hn.uth.proyecto_tallermecanico.model.ORDSCollectionResponse;
import jakarta.enterprise.context.ApplicationScoped;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class FacturaRepositoryImpl implements FacturaRepository {

    private final ApiService apiService = RetrofitClient.getInstance().getApiService();

    @Override
    public List<Factura> findAll() {
        return findRange(0, 1000);
    }

    @Override
    public List<Factura> findRange(int offset, int limit) {
        try {
            Response<ORDSCollectionResponse<Factura>> response = apiService.getFacturas(offset, limit).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getItems();
            }
        } catch (IOException e) {
            System.err.println("Error listando facturas: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public long count() {
        try {
            Response<CountResponse> response = apiService.getTotalFacturas().execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getCount();
            }
        } catch (IOException e) {
            System.err.println("Error contando facturas: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public Factura findById(String numeroFactura) {
        if (numeroFactura == null || numeroFactura.trim().isEmpty()) return null;
        try {
            // Abrimos la caja de la respuesta
            Response<ORDSCollectionResponse<Factura>> response = apiService.getFactura(numeroFactura).execute();
            if (response.isSuccessful() && response.body() != null) {
                List<Factura> lista = response.body().getItems();
                if (lista != null && !lista.isEmpty()) {
                    return lista.get(0);
                }
            }
        } catch (IOException e) {
            System.err.println("Error buscando factura: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void create(Factura factura) {
        // Validación de duplicados
        Factura existente = findById(factura.getNumero_factura());
        if (existente != null) {
            throw new RuntimeException("⚠️ Ya existe una factura con el número " + factura.getNumero_factura());
        }

        try {
            Response<Void> response = apiService.crearFactura(factura).execute();
            checkResponse(response, "Error al emitir factura");
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    @Override
    public void anular(String numeroFactura) {
        try {
            Response<Void> response = apiService.anularFactura(numeroFactura).execute();
            checkResponse(response, "Error al anular factura");
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    private void checkResponse(Response<?> response, String msg) throws IOException {
        if (!response.isSuccessful()) {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
            if (errorBody.contains("<!DOCTYPE html>")) errorBody = "Error del Servidor (Posible conflicto de datos)";
            System.err.println("❌ API Error (" + response.code() + "): " + errorBody);
            throw new RuntimeException(msg + " (" + response.code() + ")");
        }
    }
}