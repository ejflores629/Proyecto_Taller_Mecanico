package hn.uth.proyecto_tallermecanico.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

public class Repuesto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codigo_sku;
    private String nombre;
    private BigDecimal precio_unitario;
    private Integer stock_actual;
    private String proveedor;
    private String activo;

    // Constructor por defecto
    public Repuesto() {
    }

    // Constructor con todos los campos
    public Repuesto(String codigo_sku, String nombre, BigDecimal precio_unitario, Integer stock_actual, String proveedor, String activo) {
        this.codigo_sku = codigo_sku;
        this.nombre = nombre;
        this.precio_unitario = precio_unitario;
        this.stock_actual = stock_actual;
        this.proveedor = proveedor;
        this.activo = activo;
    }

    // Getters y Setters
    public String getCodigo_sku() {
        return codigo_sku;
    }

    public void setCodigo_sku(String codigo_sku) {
        this.codigo_sku = codigo_sku;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio_unitario() {
        return precio_unitario;
    }

    public void setPrecio_unitario(BigDecimal precio_unitario) {
        this.precio_unitario = precio_unitario;
    }

    public Integer getStock_actual() {
        return stock_actual;
    }

    public void setStock_actual(Integer stock_actual) {
        this.stock_actual = stock_actual;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }
}