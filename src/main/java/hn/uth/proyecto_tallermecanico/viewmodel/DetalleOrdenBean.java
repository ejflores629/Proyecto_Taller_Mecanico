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
    @Inject private AsignacionRepository asignacionRepository; // NUEVO

    @Getter @Setter
    private String numeroOrdenParam;

    @Getter private Orden ordenActual;
    @Getter private List<OrdenRepuesto> listaRepuestosAgregados;
    @Getter private List<DetalleOrden> listaManoObraAgregada;
    @Getter private List<Asignacion> listaAsignaciones; // Lista de responsables

    // Combos
    @Getter private List<Repuesto> inventarioRepuestos;
    @Getter private List<Usuario> listaTecnicos;

    @Getter @Setter
    private OrdenRepuesto nuevoRepuesto;
    @Getter @Setter
    private DetalleOrden nuevaManoObra;
    @Getter @Setter
    private Asignacion nuevaAsignacion; // Para asignar técnico principal

    // Variable para recordar al técnico principal y mejorar UX
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
                // Cargas normales
                this.listaRepuestosAgregados = ordenRepuestoRepository.findByOrden(numeroOrdenParam);
                this.listaManoObraAgregada = detalleOrdenRepository.findByOrden(numeroOrdenParam);

                // NUEVO: Cargar Asignaciones (Responsables)
                this.listaAsignaciones = asignacionRepository.findByOrden(numeroOrdenParam);

                // LÓGICA UX: Detectar si hay un técnico principal (tomamos el primero)
                if (!this.listaAsignaciones.isEmpty()) {
                    this.idTecnicoPrincipal = this.listaAsignaciones.get(0).getDoc_tecnico();
                    // Pre-llenar el formulario de mano de obra
                    this.nuevaManoObra.setDoc_tecnico(this.idTecnicoPrincipal);
                }

                this.inventarioRepuestos = repuestoRepository.findAll();
                this.listaTecnicos = usuarioRepository.findAll();
            }
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

    // --- ACCIONES ASIGNACIÓN (Técnico Principal) ---
    public void asignarTecnicoPrincipal() {
        try {
            this.nuevaAsignacion.setNumero_orden(ordenActual.getNumero_orden());
            asignacionRepository.create(this.nuevaAsignacion);

            verificarInicioTrabajo();
            addMessage(FacesMessage.SEVERITY_INFO, "Asignado", "Técnico responsable vinculado.");
            this.nuevaAsignacion = new Asignacion();
            cargarDatos(); // Esto refrescará el ID Principal
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void eliminarAsignacion(Long id) {
        try {
            asignacionRepository.delete(id);
            addMessage(FacesMessage.SEVERITY_WARN, "Eliminado", "Asignación removida.");
            this.idTecnicoPrincipal = null; // Resetear memoria
            cargarDatos();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    // --- ACCIONES REPUESTOS ---
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

    // --- ACCIONES MANO DE OBRA ---
    public void agregarManoObra() {
        try {
            this.nuevaManoObra.setNumero_orden(ordenActual.getNumero_orden());
            detalleOrdenRepository.create(nuevaManoObra);
            verificarInicioTrabajo();
            addMessage(FacesMessage.SEVERITY_INFO, "Agregado", "Tarea asignada.");

            this.nuevaManoObra = new DetalleOrden();

            // UX: Si ya teníamos un técnico principal, lo volvemos a poner por defecto
            if (this.idTecnicoPrincipal != null) {
                this.nuevaManoObra.setDoc_tecnico(this.idTecnicoPrincipal);
            }

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

    public String finalizarOrden() {
        try {
            if (this.ordenActual == null) return null;

            // 1. Validar que tenga al menos un costo (Regla de Negocio)
            if (this.ordenActual.getCosto_total().doubleValue() <= 0) {
                addMessage(FacesMessage.SEVERITY_WARN, "Atención", "La orden tiene costo cero. Agregue servicios antes de terminar.");
                return null; // Se queda en la misma página
            }

            // 2. Actualizar Estado
            this.ordenActual.setEstado("TERMINADA");
            ordenRepository.update(this.ordenActual);

            // 3. Mensaje Flash (Para que se vea en la siguiente pantalla)
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            addMessage(FacesMessage.SEVERITY_INFO, "¡Trabajo Terminado!", "La orden " + ordenActual.getNumero_orden() + " está lista para facturación.");

            // 4. Redirección
            return "orden?faces-redirect=true";

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo finalizar: " + e.getMessage());
            return null;
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}