package hn.uth.proyecto_tallermecanico.viewmodel;

import hn.uth.proyecto_tallermecanico.model.Repuesto;
import hn.uth.proyecto_tallermecanico.repository.RepuestoRepository;
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
import java.util.List;
import java.util.Map;

@Named("repuestoBean")
@ViewScoped
public class RepuestoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private RepuestoRepository repuestoRepository;

    @Getter
    private LazyDataModel<Repuesto> repuestosLazy;

    @Getter @Setter
    private Repuesto repuestoSeleccionado;

    @Getter @Setter
    private Repuesto repuestoNuevo;

    @PostConstruct
    public void init() {
        this.repuestoNuevo = new Repuesto();
        iniciarLazyModel();
    }

    private void iniciarLazyModel() {
        this.repuestosLazy = new LazyDataModel<Repuesto>() {
            private static final long serialVersionUID = 1L;

            @Override
            public int count(Map<String, FilterMeta> filterBy) {
                return (int) repuestoRepository.count();
            }

            @Override
            public List<Repuesto> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                List<Repuesto> data = repuestoRepository.findRange(first, pageSize);
                this.setRowCount((int) repuestoRepository.count());
                return data;
            }

            @Override
            public String getRowKey(Repuesto repuesto) {
                return repuesto.getCodigo_sku();
            }

            @Override
            public Repuesto getRowData(String rowKey) {
                return repuestoRepository.findById(rowKey);
            }
        };
    }

    public void guardarRepuesto() {
        try {
            // LÓGICA UNIFICADA: Si existe objeto seleccionado con ID, es Update.
            if (this.repuestoSeleccionado != null && this.repuestoSeleccionado.getCodigo_sku() != null) {
                // EDICIÓN
                repuestoRepository.update(this.repuestoSeleccionado);
                addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Repuesto actualizado correctamente.");
                this.repuestoSeleccionado = null;
            } else {
                // CREACIÓN
                repuestoRepository.create(this.repuestoNuevo);
                addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Repuesto registrado correctamente.");
                this.repuestoNuevo = new Repuesto();
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    public void eliminarRepuesto(Repuesto repuesto) {
        try {
            repuestoRepository.delete(repuesto.getCodigo_sku());
            addMessage(FacesMessage.SEVERITY_INFO, "Eliminado", "Repuesto eliminado del inventario.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar: " + e.getMessage());
        }
    }

    public void resetFormulario() {
        this.repuestoNuevo = new Repuesto();
        this.repuestoSeleccionado = null;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}