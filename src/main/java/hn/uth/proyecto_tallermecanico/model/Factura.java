package hn.uth.proyecto_tallermecanico.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Factura implements Serializable {

    private static final long serialVersionUID = 1L;

    private String numero_factura;
    private String numero_orden;
    private String doc_cliente;
    private Date fecha_emision;
    private BigDecimal subtotal;
    private BigDecimal impuesto;
    private BigDecimal total;
    private String metodo_pago;
    private String estado;
    private String activo;

    // Campos extra de la vista ORDS
    private String cliente;
    private String placa;

    // Constructor por defecto
    public Factura() {
    }

    // Constructor con campos de la tabla
    public Factura(String numero_factura, String numero_orden, String doc_cliente, Date fecha_emision, BigDecimal subtotal, BigDecimal impuesto, BigDecimal total, String metodo_pago, String estado, String activo) {
        this.numero_factura = numero_factura;
        this.numero_orden = numero_orden;
        this.doc_cliente = doc_cliente;
        this.fecha_emision = fecha_emision;
        this.subtotal = subtotal;
        this.impuesto = impuesto;
        this.total = total;
        this.metodo_pago = metodo_pago;
        this.estado = estado;
        this.activo = activo;
    }

    // Constructor completo (incluye campos de la vista)
    public Factura(String numero_factura, String numero_orden, String doc_cliente, Date fecha_emision, BigDecimal subtotal, BigDecimal impuesto, BigDecimal total, String metodo_pago, String estado, String activo, String cliente, String placa) {
        this(numero_factura, numero_orden, doc_cliente, fecha_emision, subtotal, impuesto, total, metodo_pago, estado, activo);
        this.cliente = cliente;
        this.placa = placa;
    }

    // Getters y Setters
    public String getNumero_factura() {
        return numero_factura;
    }

    public void setNumero_factura(String numero_factura) {
        this.numero_factura = numero_factura;
    }

    public String getNumero_orden() {
        return numero_orden;
    }

    public void setNumero_orden(String numero_orden) {
        this.numero_orden = numero_orden;
    }

    public String getDoc_cliente() {
        return doc_cliente;
    }

    public void setDoc_cliente(String doc_cliente) {
        this.doc_cliente = doc_cliente;
    }

    public Date getFecha_emision() {
        return fecha_emision;
    }

    public void setFecha_emision(Date fecha_emision) {
        this.fecha_emision = fecha_emision;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(BigDecimal impuesto) {
        this.impuesto = impuesto;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getMetodo_pago() {
        return metodo_pago;
    }

    public void setMetodo_pago(String metodo_pago) {
        this.metodo_pago = metodo_pago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
}