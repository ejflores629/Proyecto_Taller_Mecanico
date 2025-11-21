package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.model.Usuario;
import java.util.List;

public interface UsuarioRepository {

    List<Usuario> findAll();

    List<Usuario> findRange(int offset, int limit);

    long count();

    Usuario findById(String docIdentidad);

    void create(Usuario usuario);

    void update(Usuario usuario);

    void delete(String docIdentidad);
}