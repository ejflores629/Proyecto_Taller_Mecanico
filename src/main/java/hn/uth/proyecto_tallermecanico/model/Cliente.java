package hn.uth.proyecto_tallermecanico.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    private String doc_identidad;
    private String nombre;
    private String telefono;
    private String correo;
    private String direccion;
    private String activo;

    // Constructor por defecto
    public Cliente() {
    }

    // Constructor con todos los campos
    public Cliente(String doc_identidad, String nombre, String telefono, String correo, String direccion, String activo) {
        this.doc_identidad = doc_identidad;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.activo = activo;
    }

    // Getters y Setters
    public String getDoc_identidad() {
        return doc_identidad;
    }

    public void setDoc_identidad(String doc_identidad) {
        this.doc_identidad = doc_identidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }
}