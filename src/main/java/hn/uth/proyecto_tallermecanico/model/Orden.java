package hn.uth.proyecto_tallermecanico.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Orden implements Serializable {

    private static final long serialVersionUID = 1L;

    private String numero_orden;
    private String placa_vehiculo;
    private Date fecha_entrada;
    private Date fecha_salida;
    private String descripcion;
    private String observaciones;
    private String estado;
    private BigDecimal costo_total;
    private String activo;

    // Campos extra de la vista ORDS
    private String vehiculo; // marca || ' ' || modelo
    private String cliente; // nombre

    // Constructor por defecto
    public Orden() {
    }

    // Constructor con campos de la tabla
    public Orden(String numero_orden, String placa_vehiculo, Date fecha_entrada, Date fecha_salida, String descripcion, String observaciones, String estado, BigDecimal costo_total, String activo) {
        this.numero_orden = numero_orden;
        this.placa_vehiculo = placa_vehiculo;
        this.fecha_entrada = fecha_entrada;
        this.fecha_salida = fecha_salida;
        this.descripcion = descripcion;
        this.observaciones = observaciones;
        this.estado = estado;
        this.costo_total = costo_total;
        this.activo = activo;
    }

    // Constructor completo (incluye campos de la vista)
    public Orden(String numero_orden, String placa_vehiculo, Date fecha_entrada, Date fecha_salida, String descripcion, String observaciones, String estado, BigDecimal costo_total, String activo, String vehiculo, String cliente) {
        this(numero_orden, placa_vehiculo, fecha_entrada, fecha_salida, descripcion, observaciones, estado, costo_total, activo);
        this.vehiculo = vehiculo;
        this.cliente = cliente;
    }

    // Getters y Setters
    public String getNumero_orden() {
        return numero_orden;
    }

    public void setNumero_orden(String numero_orden) {
        this.numero_orden = numero_orden;
    }

    public String getPlaca_vehiculo() {
        return placa_vehiculo;
    }

    public void setPlaca_vehiculo(String placa_vehiculo) {
        this.placa_vehiculo = placa_vehiculo;
    }

    public Date getFecha_entrada() {
        return fecha_entrada;
    }

    public void setFecha_entrada(Date fecha_entrada) {
        this.fecha_entrada = fecha_entrada;
    }

    public Date getFecha_salida() {
        return fecha_salida;
    }

    public void setFecha_salida(Date fecha_salida) {
        this.fecha_salida = fecha_salida;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getCosto_total() {
        return costo_total;
    }

    public void setCosto_total(BigDecimal costo_total) {
        this.costo_total = costo_total;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(String vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }
}