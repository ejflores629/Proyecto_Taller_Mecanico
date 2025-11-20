package hn.uth.proyecto_tallermecanico.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Vehiculo implements Serializable {

    private static final long serialVersionUID = 1L;


    private String placa;
    private String doc_cliente;
    private String marca;
    private String modelo;
    private String color;
    private Integer anio;
    private String activo;

    // Campo extra de la vista ORDS
    private String nombre_cliente;

    // Constructor por defecto
    public Vehiculo() {
    }

    // Constructor con campos de la tabla
    public Vehiculo(String placa, String doc_cliente, String marca, String modelo, String color, Integer anio, String activo) {
        this.placa = placa;
        this.doc_cliente = doc_cliente;
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
        this.anio = anio;
        this.activo = activo;
    }

    // Constructor completo
    public Vehiculo(String placa, String doc_cliente, String marca, String modelo, String color, Integer anio, String activo, String nombre_cliente) {
        this(placa, doc_cliente, marca, modelo, color, anio, activo);
        this.nombre_cliente = nombre_cliente;
    }

    // Getters y Setters
    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getDoc_cliente() {
        return doc_cliente;
    }

    public void setDoc_cliente(String doc_cliente) {
        this.doc_cliente = doc_cliente;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public String getNombre_cliente() {
        return nombre_cliente;
    }

    public void setNombre_cliente(String nombre_cliente) {
        this.nombre_cliente = nombre_cliente;
    }
}