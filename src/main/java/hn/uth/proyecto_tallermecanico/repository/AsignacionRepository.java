package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.model.Asignacion;
import java.util.List;

public interface AsignacionRepository {
    List<Asignacion> findByOrden(String numeroOrden);
    void create(Asignacion asignacion);
    void delete(Long idAsignacion);
}