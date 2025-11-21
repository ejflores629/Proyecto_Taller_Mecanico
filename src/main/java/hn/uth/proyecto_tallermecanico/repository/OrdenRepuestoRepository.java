package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.model.OrdenRepuesto;
import java.util.List;

public interface OrdenRepuestoRepository {
    List<OrdenRepuesto> findByOrden(String numeroOrden);
    void addRepuesto(String numeroOrden, OrdenRepuesto item);
    void removeRepuesto(String numeroOrden, String sku);
}