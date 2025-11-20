package hn.uth.proyecto_tallermecanico.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("p_doc_identidad")
    private String doc_identidad;
    private String nombre_completo;
    private String clave;
    private String rol;
    private String activo;

    // Constructor por defecto
    public Usuario() {
    }

    // Constructor con todos los campos
    public Usuario(String doc_identidad, String nombre_completo, String clave, String rol, String activo) {
        this.doc_identidad = doc_identidad;
        this.nombre_completo = nombre_completo;
        this.clave = clave;
        this.rol = rol;
        this.activo = activo;
    }

    // Getters y Setters
    public String getDoc_identidad() {
        return doc_identidad;
    }

    public void setDoc_identidad(String doc_identidad) {
        this.doc_identidad = doc_identidad;
    }

    public String getNombre_completo() {
        return nombre_completo;
    }

    public void setNombre_completo(String nombre_completo) {
        this.nombre_completo = nombre_completo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }
}