package hn.uth.proyecto_tallermecanico.viewmodel;

import hn.uth.proyecto_tallermecanico.model.Cliente;
import hn.uth.proyecto_tallermecanico.repository.ClienteRepository;
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

@Named("clienteBean")
@ViewScoped
public class ClienteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ClienteRepository clienteRepository;

    @Getter
    private LazyDataModel<Cliente> clientesLazy;

    @Getter @Setter
    private Cliente clienteSeleccionado;

    @Getter @Setter
    private Cliente clienteNuevo;

    @PostConstruct
    public void init() {
        this.clienteNuevo = new Cliente();
        iniciarLazyModel();
    }

    private void iniciarLazyModel() {
        this.clientesLazy = new LazyDataModel<Cliente>() {
            private static final long serialVersionUID = 1L;

            @Override
            public int count(Map<String, FilterMeta> filterBy) {
                return (int) clienteRepository.count();
            }

            @Override
            public List<Cliente> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                List<Cliente> data = clienteRepository.findRange(first, pageSize);
                this.setRowCount((int) clienteRepository.count());
                return data;
            }

            @Override
            public String getRowKey(Cliente cliente) {
                return cliente.getDoc_identidad();
            }

            @Override
            public Cliente getRowData(String rowKey) {
                return clienteRepository.findById(rowKey);
            }
        };
    }

    public void guardarCliente() {
        try {
            if (this.clienteSeleccionado != null && this.clienteSeleccionado.getDoc_identidad() != null) {
                // EDICIÓN
                clienteRepository.update(this.clienteSeleccionado);
                addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cliente actualizado correctamente.");
                this.clienteSeleccionado = null;
            } else {
                // CREACIÓN
                clienteRepository.create(this.clienteNuevo);
                addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cliente registrado correctamente.");
                this.clienteNuevo = new Cliente();
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    public void eliminarCliente(Cliente cliente) {
        try {
            clienteRepository.delete(cliente.getDoc_identidad());
            addMessage(FacesMessage.SEVERITY_INFO, "Eliminado", "Cliente desactivado correctamente.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar: " + e.getMessage());
        }
    }

    public void resetFormulario() {
        this.clienteNuevo = new Cliente();
        this.clienteSeleccionado = null;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}