package hn.uth.proyecto_tallermecanico.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class DetalleOrden implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id_detalle;
    private String numero_orden;
    private String doc_tecnico;
    private String descripcion_tarea;
    private BigDecimal costo_mano_obra;
    private BigDecimal tiempo_estimado_hrs;

    // Campos extra de la vista ORDS
    private String tecnico;
    private Long id_nuevo; // Para capturar el ID de retorno en POST

    // Constructor por defecto
    public DetalleOrden() {
    }

    // Constructor con campos de la tabla (sin ID, ya que es generado)
    public DetalleOrden(String numero_orden, String doc_tecnico, String descripcion_tarea, BigDecimal costo_mano_obra, BigDecimal tiempo_estimado_hrs) {
        this.numero_orden = numero_orden;
        this.doc_tecnico = doc_tecnico;
        this.descripcion_tarea = descripcion_tarea;
        this.costo_mano_obra = costo_mano_obra;
        this.tiempo_estimado_hrs = tiempo_estimado_hrs;
    }

    // Constructor completo
    public DetalleOrden(Long id_detalle, String numero_orden, String doc_tecnico, String descripcion_tarea, BigDecimal costo_mano_obra, BigDecimal tiempo_estimado_hrs, String tecnico) {
        this(numero_orden, doc_tecnico, descripcion_tarea, costo_mano_obra, tiempo_estimado_hrs);
        this.id_detalle = id_detalle;
        this.tecnico = tecnico;
    }

    // Getters y Setters
    public Long getId_detalle() {
        return id_detalle;
    }

    public void setId_detalle(Long id_detalle) {
        this.id_detalle = id_detalle;
    }

    public String getNumero_orden() {
        return numero_orden;
    }

    public void setNumero_orden(String numero_orden) {
        this.numero_orden = numero_orden;
    }

    public String getDoc_tecnico() {
        return doc_tecnico;
    }

    public void setDoc_tecnico(String doc_tecnico) {
        this.doc_tecnico = doc_tecnico;
    }

    public String getDescripcion_tarea() {
        return descripcion_tarea;
    }

    public void setDescripcion_tarea(String descripcion_tarea) {
        this.descripcion_tarea = descripcion_tarea;
    }

    public BigDecimal getCosto_mano_obra() {
        return costo_mano_obra;
    }

    public void setCosto_mano_obra(BigDecimal costo_mano_obra) {
        this.costo_mano_obra = costo_mano_obra;
    }

    public BigDecimal getTiempo_estimado_hrs() {
        return tiempo_estimado_hrs;
    }

    public void setTiempo_estimado_hrs(BigDecimal tiempo_estimado_hrs) {
        this.tiempo_estimado_hrs = tiempo_estimado_hrs;
    }

    public String getTecnico() {
        return tecnico;
    }

    public void setTecnico(String tecnico) {
        this.tecnico = tecnico;
    }

    public Long getId_nuevo() {
        return id_nuevo;
    }

    public void setId_nuevo(Long id_nuevo) {
        this.id_nuevo = id_nuevo;
    }
}