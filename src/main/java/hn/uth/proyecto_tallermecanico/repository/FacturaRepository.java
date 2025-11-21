package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.model.Factura;
import java.util.List;

public interface FacturaRepository {

    List<Factura> findAll();

    List<Factura> findRange(int offset, int limit);

    long count();

    Factura findById(String numeroFactura);

    void create(Factura factura);

    void anular(String numeroFactura);
}