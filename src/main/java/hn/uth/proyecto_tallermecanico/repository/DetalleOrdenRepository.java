package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.model.DetalleOrden;
import java.util.List;

public interface DetalleOrdenRepository {
    List<DetalleOrden> findByOrden(String numeroOrden);
    void create(DetalleOrden detalle);
    void delete(Long idDetalle);
}