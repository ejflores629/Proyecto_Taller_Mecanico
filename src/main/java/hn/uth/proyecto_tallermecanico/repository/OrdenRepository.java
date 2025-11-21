package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.model.Orden;
import java.util.List;

public interface OrdenRepository {

    List<Orden> findAll();

    List<Orden> findRange(int offset, int limit);

    long count();

    Orden findById(String numeroOrden);

    void create(Orden orden);

    void update(Orden orden);

    void delete(String numeroOrden);
}