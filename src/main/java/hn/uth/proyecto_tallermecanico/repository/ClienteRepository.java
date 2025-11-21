package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.model.Cliente;
import java.util.List;

public interface ClienteRepository {

    List<Cliente> findAll();

    List<Cliente> findRange(int offset, int limit);

    long count();

    Cliente findById(String docIdentidad);

    void save(Cliente cliente); // Puede ser crear o actualizar

    void delete(String docIdentidad);
}