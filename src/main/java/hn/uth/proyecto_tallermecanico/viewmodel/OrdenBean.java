package hn.uth.proyecto_tallermecanico.viewmodel;

import hn.uth.proyecto_tallermecanico.model.Orden;
import hn.uth.proyecto_tallermecanico.model.Vehiculo;
import hn.uth.proyecto_tallermecanico.repository.OrdenRepository;
import hn.uth.proyecto_tallermecanico.repository.VehiculoRepository;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import java.io.Serializable;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Named("ordenBean")
@ViewScoped
public class OrdenBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private OrdenRepository ordenRepository;

    @Inject
    private VehiculoRepository vehiculoRepository;

    @Getter
    private LazyDataModel<Orden> ordenesLazy;

    @Getter @Setter
    private Orden ordenSeleccionada;

    @Getter @Setter
    private Orden ordenNueva;

    // --- CONTROL DE UI ---
    @Getter @Setter
    private boolean incluirObservaciones;

    @Getter
    private List<Vehiculo> listaVehiculos;

    @Getter
    private final List<String> listaEstados = Arrays.asList("PENDIENTE", "EN PROCESO", "ESPERA REPUESTOS", "TERMINADA", "CERRADA");

    @PostConstruct
    public void init() {
        this.ordenNueva = new Orden();
        this.ordenNueva.setEstado("PENDIENTE");
        // CAMBIO: Ya no generamos el código aquí para evitar desincronización visual
        iniciarLazyModel();
        cargarListas();
    }

    // --- LÓGICA DE NEGOCIO ---

    public void generarNumeroOrden() {
        try {
            int year = Year.now().getValue();
            // Paso 1: Obtener un punto de partida lógico (Total + 1)
            long total = ordenRepository.count();
            int secuencia = (int) total + 1;

            String codigoGenerado;

            // Paso 2: Bucle para encontrar el primer número libre
            // Esto evita duplicados si borraron una orden intermedia o si el count no es exacto
            while (true) {
                // Formato: ORD-2025-0001
                codigoGenerado = String.format("ORD-%d-%04d", year, secuencia);

                // Verificamos si existe en la BD
                if (ordenRepository.findById(codigoGenerado) == null) {
                    // Si es null, está libre -> ¡Lo usamos!
                    break;
                }
                // Si existe, probamos el siguiente
                secuencia++;
            }

            this.ordenNueva.setNumero_orden(codigoGenerado);
            addMessage(FacesMessage.SEVERITY_INFO, "Generado", "Se ha asignado el número: " + codigoGenerado);

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo generar el número: " + e.getMessage());
        }
    }

    private void cargarListas() {
        this.listaVehiculos = vehiculoRepository.findAll();
    }

    private void iniciarLazyModel() {
        this.ordenesLazy = new LazyDataModel<Orden>() {
            private static final long serialVersionUID = 1L;
            @Override
            public int count(Map<String, FilterMeta> filterBy) { return (int) ordenRepository.count(); }
            @Override
            public List<Orden> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                List<Orden> data = ordenRepository.findRange(first, pageSize);
                setRowCount((int) ordenRepository.count());
                return data;
            }
            @Override
            public String getRowKey(Orden orden) { return orden.getNumero_orden(); }
            @Override
            public Orden getRowData(String rowKey) { return ordenRepository.findById(rowKey); }
        };
    }

    public void guardarOrden() {
        try {
            if (this.ordenSeleccionada != null && this.ordenSeleccionada.getNumero_orden() != null) {
                ordenRepository.update(this.ordenSeleccionada);
                addMessage(FacesMessage.SEVERITY_INFO, "Actualizado", "Orden actualizada correctamente.");
                this.ordenSeleccionada = null;
            } else {
                if (!incluirObservaciones) {
                    this.ordenNueva.setObservaciones(null);
                }

                // Validación extra: Asegurar que el número se haya generado
                if (this.ordenNueva.getNumero_orden() == null || this.ordenNueva.getNumero_orden().isEmpty()) {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe generar un número de orden antes de guardar.");
                    return;
                }

                ordenRepository.create(this.ordenNueva);
                addMessage(FacesMessage.SEVERITY_INFO, "Creado", "Orden " + this.ordenNueva.getNumero_orden() + " registrada.");

                resetFormulario();
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    public void eliminarOrden(Orden orden) {
        try {
            ordenRepository.delete(orden.getNumero_orden());
            addMessage(FacesMessage.SEVERITY_INFO, "Anulada", "La orden ha sido anulada.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo anular: " + e.getMessage());
        }
    }

    public void resetFormulario() {
        this.ordenNueva = new Orden();
        this.ordenNueva.setEstado("PENDIENTE");
        this.incluirObservaciones = false;
        this.ordenSeleccionada = null;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}