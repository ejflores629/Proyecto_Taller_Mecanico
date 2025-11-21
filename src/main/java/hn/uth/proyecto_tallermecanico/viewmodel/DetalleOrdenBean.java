package hn.uth.proyecto_tallermecanico.viewmodel;

import hn.uth.proyecto_tallermecanico.model.*;
import hn.uth.proyecto_tallermecanico.repository.*;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Named("detalleBean")
@ViewScoped
public class DetalleOrdenBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject private OrdenRepository ordenRepository;
    @Inject private OrdenRepuestoRepository ordenRepuestoRepository;
    @Inject private DetalleOrdenRepository detalleOrdenRepository;
    @Inject private RepuestoRepository repuestoRepository;
    @Inject private UsuarioRepository usuarioRepository;

    // Parámetro de URL
    @Getter @Setter
    private String numeroOrdenParam;

    // Datos Principales
    @Getter
    private Orden ordenActual;
    @Getter
    private List<OrdenRepuesto> listaRepuestosAgregados;
    @Getter
    private List<DetalleOrden> listaManoObraAgregada;

    // Datos para Formularios (Combos)
    @Getter
    private List<Repuesto> inventarioRepuestos;
    @Getter
    private List<Usuario> listaTecnicos;

    // Objetos Nuevos para agregar
    @Getter @Setter
    private OrdenRepuesto nuevoRepuesto;
    @Getter @Setter
    private DetalleOrden nuevaManoObra;

    @PostConstruct
    public void init() {
        this.nuevoRepuesto = new OrdenRepuesto();
        this.nuevaManoObra = new DetalleOrden();
    }

    public void cargarDatos() {
        if (numeroOrdenParam != null && !numeroOrdenParam.isEmpty()) {
            // 1. Cargar Header de la Orden
            this.ordenActual = ordenRepository.findById(numeroOrdenParam);

            if (this.ordenActual != null) {
                // 2. Cargar Listas Detalladas
                this.listaRepuestosAgregados = ordenRepuestoRepository.findByOrden(numeroOrdenParam);
                this.listaManoObraAgregada = detalleOrdenRepository.findByOrden(numeroOrdenParam);

                // 3. Cargar Catálogos para los Combos
                this.inventarioRepuestos = repuestoRepository.findAll();
                this.listaTecnicos = usuarioRepository.findAll(); // Idealmente filtrar por rol TECNICO
            }
        }
    }

    // --- ACCIONES REPUESTOS ---
    public void agregarRepuesto() {
        try {
            this.nuevoRepuesto.setNumero_orden(ordenActual.getNumero_orden());
            ordenRepuestoRepository.addRepuesto(ordenActual.getNumero_orden(), nuevoRepuesto);
            addMessage(FacesMessage.SEVERITY_INFO, "Agregado", "Repuesto añadido a la orden.");

            this.nuevoRepuesto = new OrdenRepuesto(); // Limpiar
            cargarDatos(); // Recargar para actualizar Costo Total
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void eliminarRepuesto(String sku) {
        try {
            ordenRepuestoRepository.removeRepuesto(ordenActual.getNumero_orden(), sku);
            addMessage(FacesMessage.SEVERITY_WARN, "Eliminado", "Repuesto quitado de la orden.");
            cargarDatos();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    // --- ACCIONES MANO DE OBRA ---
    public void agregarManoObra() {
        try {
            this.nuevaManoObra.setNumero_orden(ordenActual.getNumero_orden());
            detalleOrdenRepository.create(nuevaManoObra);
            addMessage(FacesMessage.SEVERITY_INFO, "Agregado", "Tarea asignada correctamente.");

            this.nuevaManoObra = new DetalleOrden(); // Limpiar
            cargarDatos(); // Recargar para actualizar Costo Total
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void eliminarManoObra(Long id) {
        try {
            detalleOrdenRepository.delete(id);
            addMessage(FacesMessage.SEVERITY_WARN, "Eliminado", "Tarea eliminada.");
            cargarDatos();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}