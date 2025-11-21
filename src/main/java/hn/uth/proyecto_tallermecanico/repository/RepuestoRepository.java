package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.model.Repuesto;
import java.util.List;

public interface RepuestoRepository {

    List<Repuesto> findAll();

    List<Repuesto> findRange(int offset, int limit);

    long count();

    Repuesto findById(String sku);

    void create(Repuesto repuesto);

    void update(Repuesto repuesto);

    void delete(String sku);
}