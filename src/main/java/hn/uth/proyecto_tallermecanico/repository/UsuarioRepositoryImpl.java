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
            System.err.println("Error al obtener usuarios: " + e.getMessage());
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
            System.err.println("Error al contar usuarios: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public Usuario findById(String docIdentidad) {
        try {
            Response<Usuario> response = apiService.getUsuario(docIdentidad).execute();
            if (response.isSuccessful()) {
                return response.body();
            }
        } catch (IOException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void save(Usuario usuario) {
        try {
            Response<Void> response;
            // Misma lógica que Cliente: Si tiene 'activo', asumimos update
            if (usuario.getActivo() != null && !usuario.getActivo().isEmpty()) {
                response = apiService.actualizarUsuario(usuario.getDoc_identidad(), usuario).execute();
            } else {
                response = apiService.crearUsuario(usuario).execute();
            }

            if (!response.isSuccessful()) {
                throw new RuntimeException("Error API Usuarios: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String docIdentidad) {
        try {
            Response<Void> response = apiService.eliminarUsuario(docIdentidad).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error API al eliminar: " + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error de conexión al eliminar: " + e.getMessage(), e);
        }
    }
}