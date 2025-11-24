package hn.uth.proyecto_tallermecanico.repository;

import hn.uth.proyecto_tallermecanico.model.Vehiculo;
import java.util.List;

public interface VehiculoRepository {

    List<Vehiculo> findAll();

    List<Vehiculo> findRange(int offset, int limit);

    long count();

    Vehiculo findById(String placa);

    void create(Vehiculo vehiculo);

    void update(Vehiculo vehiculo);

    void delete(String placa);
}