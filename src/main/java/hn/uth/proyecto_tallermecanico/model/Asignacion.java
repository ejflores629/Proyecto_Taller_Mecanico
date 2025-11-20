package hn.uth.proyecto_tallermecanico.model;

import java.io.Serializable;
import java.util.Date;

public class Asignacion implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id_asignacion;
    private String doc_tecnico;
    private String numero_orden;
    private Date fecha_asignacion;

    // Campos extra de la vista ORDS
    private String tecnico;
    private Long id_nuevo; // Para capturar el ID de retorno en POST

    // Constructor por defecto
    public Asignacion() {
    }

    // Constructor con campos de la tabla
    public Asignacion(String doc_tecnico, String numero_orden, Date fecha_asignacion) {
        this.doc_tecnico = doc_tecnico;
        this.numero_orden = numero_orden;
        this.fecha_asignacion = fecha_asignacion;
    }

    // Constructor completo
    public Asignacion(Long id_asignacion, String doc_tecnico, String numero_orden, Date fecha_asignacion, String tecnico) {
        this(doc_tecnico, numero_orden, fecha_asignacion);
        this.id_asignacion = id_asignacion;
        this.tecnico = tecnico;
    }

    // Getters y Setters
    public Long getId_asignacion() {
        return id_asignacion;
    }

    public void setId_asignacion(Long id_asignacion) {
        this.id_asignacion = id_asignacion;
    }

    public String getDoc_tecnico() {
        return doc_tecnico;
    }

    public void setDoc_tecnico(String doc_tecnico) {
        this.doc_tecnico = doc_tecnico;
    }

    public String getNumero_orden() {
        return numero_orden;
    }

    public void setNumero_orden(String numero_orden) {
        this.numero_orden = numero_orden;
    }

    public Date getFecha_asignacion() {
        return fecha_asignacion;
    }

    public void setFecha_asignacion(Date fecha_asignacion) {
        this.fecha_asignacion = fecha_asignacion;
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