package hn.uth.proyecto_tallermecanico.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class DashboardStats implements Serializable {
    // Para el endpoint /resumen
    private int ordenes_activas;
    private BigDecimal ingresos_mes;
    private int bajo_stock;
    private int total_clientes;

    // Para los gráficos (Lista de esto)
    private String label;
    private BigDecimal value;

    // Getters y Setters
    public int getOrdenes_activas() { return ordenes_activas; }
    public void setOrdenes_activas(int ordenes_activas) { this.ordenes_activas = ordenes_activas; }
    public BigDecimal getIngresos_mes() { return ingresos_mes; }
    public void setIngresos_mes(BigDecimal ingresos_mes) { this.ingresos_mes = ingresos_mes; }
    public int getBajo_stock() { return bajo_stock; }
    public void setBajo_stock(int bajo_stock) { this.bajo_stock = bajo_stock; }
    public int getTotal_clientes() { return total_clientes; }
    public void setTotal_clientes(int total_clientes) { this.total_clientes = total_clientes; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
}