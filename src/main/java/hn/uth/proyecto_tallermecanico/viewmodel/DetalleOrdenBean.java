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

    // Inyección de Repositorios
    @Inject private OrdenRepository ordenRepository;
    @Inject private OrdenRepuestoRepository ordenRepuestoRepository;
    @Inject private DetalleOrdenRepository detalleOrdenRepository;
    @Inject private RepuestoRepository repuestoRepository;
    @Inject private UsuarioRepository usuarioRepository;
    @Inject private AsignacionRepository asignacionRepository;

    // Parámetro de URL (El ID de la orden)
    @Getter @Setter
    private String numeroOrdenParam;

    // Datos de la Orden Actual
    @Getter private Orden ordenActual;

    // Listas de Detalles (Tablas)
    @Getter private List<OrdenRepuesto> listaRepuestosAgregados;
    @Getter private List<DetalleOrden> listaManoObraAgregada;
    @Getter private List<Asignacion> listaAsignaciones;

    // Listas para Combos (Selectores)
    @Getter private List<Repuesto> inventarioRepuestos;
    @Getter private List<Usuario> listaTecnicos;

    // Objetos para los Formularios de "Agregar"
    @Getter @Setter
    private OrdenRepuesto nuevoRepuesto;
    @Getter @Setter
    private DetalleOrden nuevaManoObra;
    @Getter @Setter
    private Asignacion nuevaAsignacion;

    // Memoria interna para UX (Recordar quién es el jefe de la orden)
    private String idTecnicoPrincipal = null;

    @PostConstruct
    public void init() {
        this.nuevoRepuesto = new OrdenRepuesto();
        this.nuevaManoObra = new DetalleOrden();
        this.nuevaAsignacion = new Asignacion();
    }

    /**
     * Método principal de carga. Se ejecuta al entrar a la página.
     */
    public void cargarDatos() {
        if (numeroOrdenParam != null && !numeroOrdenParam.isEmpty()) {
            // 1. Cargar la Cabecera
            this.ordenActual = ordenRepository.findById(numeroOrdenParam);

            if (this.ordenActual != null) {
                // 2. Cargar Listas de Detalle
                this.listaRepuestosAgregados = ordenRepuestoRepository.findByOrden(numeroOrdenParam);
                this.listaManoObraAgregada = detalleOrdenRepository.findByOrden(numeroOrdenParam);
                this.listaAsignaciones = asignacionRepository.findByOrden(numeroOrdenParam);

                // 3. Lógica UX: Detectar Técnico Principal
                // Si hay asignaciones, tomamos al primero como el "default" para futuros registros
                if (this.listaAsignaciones != null && !this.listaAsignaciones.isEmpty()) {
                    this.idTecnicoPrincipal = this.listaAsignaciones.get(0).getDoc_tecnico();

                    // Si el formulario está vacío, lo pre-llenamos
                    if (this.nuevaManoObra.getDoc_tecnico() == null) {
                        this.nuevaManoObra.setDoc_tecnico(this.idTecnicoPrincipal);
                    }
                }

                // 4. Lógica UX: Pre-cargar descripción del problema
                // Si no ha escrito nada, ponemos el problema reportado por el cliente
                if (this.nuevaManoObra.getDescripcion_tarea() == null || this.nuevaManoObra.getDescripcion_tarea().trim().isEmpty()) {
                    this.nuevaManoObra.setDescripcion_tarea(this.ordenActual.getDescripcion());
                }

                // 5. Cargar Catálogos
                this.inventarioRepuestos = repuestoRepository.findAll();
                this.listaTecnicos = usuarioRepository.findAll();
            }
        }
    }

    /**
     * Seguridad: Verifica si la orden ya está cerrada (facturada).
     */
    public boolean isOrdenCerrada() {
        return this.ordenActual != null && "CERRADA".equals(this.ordenActual.getEstado());
    }

    /**
     * Lógica de Negocio: Si la orden estaba PENDIENTE, pasa a EN PROCESO al hacer cualquier movimiento.
     */
    private void verificarInicioTrabajo() {
        if (this.ordenActual != null && "PENDIENTE".equals(this.ordenActual.getEstado())) {
            this.ordenActual.setEstado("EN PROCESO");
            try {
                ordenRepository.update(this.ordenActual);
                addMessage(FacesMessage.SEVERITY_INFO, "Estado Actualizado", "La orden cambió a EN PROCESO.");
            } catch (Exception e) {
                System.err.println("Error actualizando estado automático: " + e.getMessage());
            }
        }
    }

    // =========================================================================
    // ACCIONES: ASIGNACIÓN DE TÉCNICOS
    // =========================================================================

    public void asignarTecnicoPrincipal() {
        if (isOrdenCerrada()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Bloqueado", "No se puede modificar una orden CERRADA.");
            return;
        }

        try {
            // Validación Null-Safe
            if (this.nuevaAsignacion.getDoc_tecnico() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionar un técnico.");
                return;
            }

            // Evitar Duplicados
            boolean yaExiste = false;
            if (listaAsignaciones != null) {
                yaExiste = listaAsignaciones.stream()
                        .filter(a -> a.getDoc_tecnico() != null)
                        .anyMatch(a -> a.getDoc_tecnico().equals(this.nuevaAsignacion.getDoc_tecnico()));
            }

            if (yaExiste) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Duplicado", "Este técnico ya es responsable.");
                return;
            }

            this.nuevaAsignacion.setNumero_orden(ordenActual.getNumero_orden());
            asignacionRepository.create(this.nuevaAsignacion);

            verificarInicioTrabajo();
            addMessage(FacesMessage.SEVERITY_INFO, "Asignado", "Responsable vinculado correctamente.");

            this.nuevaAsignacion = new Asignacion(); // Limpiar
            cargarDatos(); // Refrescar listas
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void eliminarAsignacion(Long id) {
        if (isOrdenCerrada()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Bloqueado", "Orden CERRADA.");
            return;
        }
        try {
            asignacionRepository.delete(id);
            addMessage(FacesMessage.SEVERITY_WARN, "Eliminado", "Asignación removida.");
            this.idTecnicoPrincipal = null; // Resetear memoria
            cargarDatos();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    // =========================================================================
    // ACCIONES: MATERIALES Y REPUESTOS
    // =========================================================================

    public void agregarRepuesto() {
        if (isOrdenCerrada()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Bloqueado", "No se puede modificar una orden CERRADA.");
            return;
        }
        try {
            this.nuevoRepuesto.setNumero_orden(ordenActual.getNumero_orden());
            ordenRepuestoRepository.addRepuesto(ordenActual.getNumero_orden(), nuevoRepuesto);

            verificarInicioTrabajo();
            addMessage(FacesMessage.SEVERITY_INFO, "Agregado", "Repuesto añadido a la orden.");

            this.nuevoRepuesto = new OrdenRepuesto(); // Limpiar
            cargarDatos(); // Recargar para actualizar Costo Total
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void eliminarRepuesto(String sku) {
        if (isOrdenCerrada()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Bloqueado", "Orden CERRADA.");
            return;
        }
        try {
            ordenRepuestoRepository.removeRepuesto(ordenActual.getNumero_orden(), sku);
            addMessage(FacesMessage.SEVERITY_WARN, "Eliminado", "Repuesto devuelto al inventario.");
            cargarDatos();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    // =========================================================================
    // ACCIONES: MANO DE OBRA Y SERVICIOS
    // =========================================================================

    public void agregarManoObra() {
        if (isOrdenCerrada()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Bloqueado", "No se puede modificar una orden CERRADA.");
            return;
        }
        try {
            this.nuevaManoObra.setNumero_orden(ordenActual.getNumero_orden());
            detalleOrdenRepository.create(nuevaManoObra);

            verificarInicioTrabajo();
            addMessage(FacesMessage.SEVERITY_INFO, "Agregado", "Tarea registrada.");

            // Limpiar formulario
            this.nuevaManoObra = new DetalleOrden();

            // UX: Restaurar valores inteligentes para la siguiente tarea
            if (this.idTecnicoPrincipal != null) {
                this.nuevaManoObra.setDoc_tecnico(this.idTecnicoPrincipal);
            }
            if (this.ordenActual != null) {
                this.nuevaManoObra.setDescripcion_tarea(this.ordenActual.getDescripcion());
            }

            cargarDatos();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void eliminarManoObra(Long id) {
        if (isOrdenCerrada()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Bloqueado", "Orden CERRADA.");
            return;
        }
        try {
            detalleOrdenRepository.delete(id);
            addMessage(FacesMessage.SEVERITY_WARN, "Eliminado", "Tarea eliminada.");
            cargarDatos();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    // =========================================================================
    // ACCIÓN FINAL: TERMINAR TRABAJO
    // =========================================================================

    public String finalizarOrden() {
        if (isOrdenCerrada()) return null;

        try {
            if (this.ordenActual == null) return null;

            // Validación: No permitir terminar si no hay costos (orden vacía)
            if (this.ordenActual.getCosto_total().doubleValue() <= 0) {
                addMessage(FacesMessage.SEVERITY_WARN, "Atención", "La orden tiene costo cero. Agregue servicios antes de terminar.");
                return null;
            }

            this.ordenActual.setEstado("TERMINADA");
            ordenRepository.update(this.ordenActual);

            // Mensaje Flash para la siguiente pantalla
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            addMessage(FacesMessage.SEVERITY_INFO, "¡Trabajo Terminado!", "La orden " + ordenActual.getNumero_orden() + " está lista para facturación.");

            // Redirigir a la lista principal
            return "orden?faces-redirect=true";

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo finalizar: " + e.getMessage());
            return null;
        }
    }

    // Helper de mensajes
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}