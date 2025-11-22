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
    @Inject private AsignacionRepository asignacionRepository;

    @Getter @Setter
    private String numeroOrdenParam;

    @Getter private Orden ordenActual;
    @Getter private List<OrdenRepuesto> listaRepuestosAgregados;
    @Getter private List<DetalleOrden> listaManoObraAgregada;
    @Getter private List<Asignacion> listaAsignaciones;

    @Getter private List<Repuesto> inventarioRepuestos;
    @Getter private List<Usuario> listaTecnicos;

    @Getter @Setter
    private OrdenRepuesto nuevoRepuesto;
    @Getter @Setter
    private DetalleOrden nuevaManoObra;
    @Getter @Setter
    private Asignacion nuevaAsignacion;

    private String idTecnicoPrincipal = null;

    @PostConstruct
    public void init() {
        this.nuevoRepuesto = new OrdenRepuesto();
        this.nuevaManoObra = new DetalleOrden();
        this.nuevaAsignacion = new Asignacion();
    }

    public void cargarDatos() {
        if (numeroOrdenParam != null && !numeroOrdenParam.isEmpty()) {
            this.ordenActual = ordenRepository.findById(numeroOrdenParam);

            if (this.ordenActual != null) {
                this.listaRepuestosAgregados = ordenRepuestoRepository.findByOrden(numeroOrdenParam);
                this.listaManoObraAgregada = detalleOrdenRepository.findByOrden(numeroOrdenParam);
                this.listaAsignaciones = asignacionRepository.findByOrden(numeroOrdenParam);

                // 1. Detectar Técnico Principal para pre-selección
                if (!this.listaAsignaciones.isEmpty()) {
                    this.idTecnicoPrincipal = this.listaAsignaciones.get(0).getDoc_tecnico();
                    this.nuevaManoObra.setDoc_tecnico(this.idTecnicoPrincipal);
                }

                // 2. LÓGICA CORREGIDA: Pre-cargar la descripción de la tarea con el problema reportado
                // Esto ayuda al mecánico a no tener que reescribir "Revisión de frenos"
                if (this.nuevaManoObra.getDescripcion_tarea() == null || this.nuevaManoObra.getDescripcion_tarea().isEmpty()) {
                    this.nuevaManoObra.setDescripcion_tarea(this.ordenActual.getDescripcion());
                }

                this.inventarioRepuestos = repuestoRepository.findAll();
                this.listaTecnicos = usuarioRepository.findAll();
            }
        }
    }

    // --- ACCIÓN FINALIZAR (UX) ---
    public String finalizarOrden() {
        try {
            if (this.ordenActual == null) return null;
            if (this.ordenActual.getCosto_total().doubleValue() <= 0) {
                addMessage(FacesMessage.SEVERITY_WARN, "Atención", "La orden tiene costo cero. Agregue servicios antes de terminar.");
                return null;
            }
            this.ordenActual.setEstado("TERMINADA");
            ordenRepository.update(this.ordenActual);

            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            addMessage(FacesMessage.SEVERITY_INFO, "¡Trabajo Terminado!", "Orden lista para facturación.");
            return "orden?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
            return null;
        }
    }

    // --- ASIGNACIÓN ---
    public void asignarTecnicoPrincipal() {
        try {
            // VALIDACIÓN: No duplicar técnico
            boolean yaExiste = listaAsignaciones.stream()
                    .anyMatch(a -> a.getDoc_tecnico().equals(this.nuevaAsignacion.getDoc_tecnico()));

            if (yaExiste) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Duplicado", "Este técnico ya está asignado como responsable.");
                return;
            }

            this.nuevaAsignacion.setNumero_orden(ordenActual.getNumero_orden());
            asignacionRepository.create(this.nuevaAsignacion);

            verificarInicioTrabajo();
            addMessage(FacesMessage.SEVERITY_INFO, "Asignado", "Responsable vinculado.");
            this.nuevaAsignacion = new Asignacion();
            cargarDatos();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void eliminarAsignacion(Long id) {
        try {
            asignacionRepository.delete(id);
            addMessage(FacesMessage.SEVERITY_WARN, "Eliminado", "Asignación removida.");
            this.idTecnicoPrincipal = null;
            cargarDatos();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    // --- REPUESTOS ---
    public void agregarRepuesto() {
        try {
            this.nuevoRepuesto.setNumero_orden(ordenActual.getNumero_orden());
            ordenRepuestoRepository.addRepuesto(ordenActual.getNumero_orden(), nuevoRepuesto);
            verificarInicioTrabajo();
            addMessage(FacesMessage.SEVERITY_INFO, "Agregado", "Repuesto añadido.");
            this.nuevoRepuesto = new OrdenRepuesto();
            cargarDatos();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void eliminarRepuesto(String sku) {
        try {
            ordenRepuestoRepository.removeRepuesto(ordenActual.getNumero_orden(), sku);
            addMessage(FacesMessage.SEVERITY_WARN, "Eliminado", "Repuesto quitado.");
            cargarDatos();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    // --- MANO DE OBRA ---
    public void agregarManoObra() {
        try {
            this.nuevaManoObra.setNumero_orden(ordenActual.getNumero_orden());
            detalleOrdenRepository.create(nuevaManoObra);
            verificarInicioTrabajo();
            addMessage(FacesMessage.SEVERITY_INFO, "Agregado", "Tarea asignada.");

            this.nuevaManoObra = new DetalleOrden();
            // Restaurar selección inteligente
            if (this.idTecnicoPrincipal != null) {
                this.nuevaManoObra.setDoc_tecnico(this.idTecnicoPrincipal);
            }
            // Restaurar descripción por si agrega otra parecida (opcional, o dejar en blanco)
            // this.nuevaManoObra.setDescripcion_tarea(ordenActual.getDescripcion());

            cargarDatos();
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

    private void verificarInicioTrabajo() {
        if (this.ordenActual != null && "PENDIENTE".equals(this.ordenActual.getEstado())) {
            this.ordenActual.setEstado("EN PROCESO");
            try {
                ordenRepository.update(this.ordenActual);
                addMessage(FacesMessage.SEVERITY_INFO, "Estado", "Orden cambió a EN PROCESO.");
            } catch (Exception e) {
                System.err.println("Error update estado: " + e.getMessage());
            }
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}