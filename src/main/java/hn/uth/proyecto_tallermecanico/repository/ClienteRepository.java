package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.model.Cliente;
import java.util.List;

public interface ClienteRepository {

    List<Cliente> findAll();

    List<Cliente> findRange(int offset, int limit);

    long count();

    Cliente findById(String docIdentidad);

    void create(Cliente cliente);

    void update(Cliente cliente);

    void delete(String docIdentidad);
}