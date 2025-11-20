package hn.uth.proyecto_tallermecanico.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrdenRepuesto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String numero_orden;
    private String codigo_sku;
    private Integer cantidad_utilizada;
    private BigDecimal precio_venta_momento;

    // Campos extra de la vista ORDS
    private String repuesto;
    private BigDecimal subtotal;

    // Constructor por defecto
    public OrdenRepuesto() {
    }

    // Constructor con campos de la tabla
    public OrdenRepuesto(String numero_orden, String codigo_sku, Integer cantidad_utilizada, BigDecimal precio_venta_momento) {
        this.numero_orden = numero_orden;
        this.codigo_sku = codigo_sku;
        this.cantidad_utilizada = cantidad_utilizada;
        this.precio_venta_momento = precio_venta_momento;
    }

    // Constructor completo (incluye campos de la vista)
    public OrdenRepuesto(String numero_orden, String codigo_sku, Integer cantidad_utilizada, BigDecimal precio_venta_momento, String repuesto, BigDecimal subtotal) {
        this(numero_orden, codigo_sku, cantidad_utilizada, precio_venta_momento);
        this.repuesto = repuesto;
        this.subtotal = subtotal;
    }

    // Getters y Setters
    public String getNumero_orden() {
        return numero_orden;
    }

    public void setNumero_orden(String numero_orden) {
        this.numero_orden = numero_orden;
    }

    public String getCodigo_sku() {
        return codigo_sku;
    }

    public void setCodigo_sku(String codigo_sku) {
        this.codigo_sku = codigo_sku;
    }

    public Integer getCantidad_utilizada() {
        return cantidad_utilizada;
    }

    public void setCantidad_utilizada(Integer cantidad_utilizada) {
        this.cantidad_utilizada = cantidad_utilizada;
    }

    public BigDecimal getPrecio_venta_momento() {
        return precio_venta_momento;
    }

    public void setPrecio_venta_momento(BigDecimal precio_venta_momento) {
        this.precio_venta_momento = precio_venta_momento;
    }

    public String getRepuesto() {
        return repuesto;
    }

    public void setRepuesto(String repuesto) {
        this.repuesto = repuesto;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}