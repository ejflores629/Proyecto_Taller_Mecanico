package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.controller.ApiService;
import hn.uth.proyecto_tallermecanico.controller.RetrofitClient;
import hn.uth.proyecto_tallermecanico.model.CountResponse;
import hn.uth.proyecto_tallermecanico.model.ORDSCollectionResponse;
import hn.uth.proyecto_tallermecanico.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final ApiService apiService = RetrofitClient.getInstance().getApiService();

    @Override
    public List<Usuario> findAll() {
        return findRange(0, 1000);
    }

    @Override
    public List<Usuario> findRange(int offset, int limit) {
        try {
            Response<ORDSCollectionResponse<Usuario>> response = apiService.getUsuarios(offset, limit).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getItems();
            }
        } catch (IOException e) {
            System.err.println("Error conexión usuarios: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public long count() {
        try {
            Response<CountResponse> response = apiService.getTotalUsuarios().execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getCount();
            }
        } catch (IOException e) {
            System.err.println("Error conteo usuarios: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public Usuario findById(String docIdentidad) {
        // Protección: DNI nulo o vacío no debe buscar
        if (docIdentidad == null || docIdentidad.trim().isEmpty()) {
            return null;
        }

        try {
            // CAMBIO: Ahora recibimos la colección (la caja), no el usuario directo
            Response<ORDSCollectionResponse<Usuario>> response = apiService.getUsuario(docIdentidad).execute();

            if (response.isSuccessful() && response.body() != null) {
                // La "caja" puede estar vacía (items = []) o tener datos.
                List<Usuario> lista = response.body().getItems();
                if (lista != null && !lista.isEmpty()) {
                    // ¡Si la lista NO está vacía, entonces SÍ existe el usuario!
                    return lista.get(0);
                }
            }
        } catch (IOException e) {
            System.err.println("Error buscar usuario: " + e.getMessage());
        }
        // Si la lista está vacía o hubo error, retornamos null (Usuario NO existe)
        return null;
    }

    @Override
    public void create(Usuario usuario) {
        Usuario existente = findById(usuario.getDoc_identidad());

        if (existente != null) {
            throw new RuntimeException("⚠️ Ya existe un usuario ACTIVO con el DNI " + usuario.getDoc_identidad() + " (" + existente.getNombre_completo() + ")");
        }

        try {
            Response<Void> response = apiService.crearUsuario(usuario).execute();
            checkResponse(response, "Error al crear usuario");
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Usuario usuario) {
        try {
            Response<Void> response = apiService.actualizarUsuario(usuario.getDoc_identidad(), usuario).execute();
            checkResponse(response, "Error al actualizar usuario");
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String docIdentidad) {
        try {
            Response<Void> response = apiService.eliminarUsuario(docIdentidad).execute();
            checkResponse(response, "Error al eliminar usuario");
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