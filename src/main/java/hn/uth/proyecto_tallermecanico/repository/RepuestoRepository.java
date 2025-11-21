package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.model.Repuesto;
import java.util.List;

public interface RepuestoRepository {

    List<Repuesto> findAll();

    List<Repuesto> findRange(int offset, int limit);

    long count();

    Repuesto findById(String sku);

    void save(Repuesto repuesto);

    void delete(String sku);
}