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

    // CAMBIO: Usamos LazyDataModel en lugar de List
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
                // PrimeFaces pregunta: "¿Cuántos hay en total?"
                // Llamamos al endpoint /total
                return (int) clienteRepository.count();
            }

            @Override
            public List<Cliente> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                // PrimeFaces pregunta: "Dame los registros desde X hasta Y"
                // 'first' es el offset (inicio), 'pageSize' es el limit (cuantos traer)

                List<Cliente> data = clienteRepository.findRange(first, pageSize);

                // Importante: Decirle a PrimeFaces el tamaño total real para que calcule las páginas
                this.setRowCount((int) clienteRepository.count());

                return data;
            }

            @Override
            public String getRowKey(Cliente cliente) {
                return cliente.getDoc_identidad();
            }

            @Override
            public Cliente getRowData(String rowKey) {
                // Si seleccionas una fila, PrimeFaces usa esto para recuperar el objeto
                return clienteRepository.findById(rowKey);
            }
        };
    }

    public void guardarCliente() {
        try {
            // Lógica unificada: Si clienteSeleccionado existe, es Edición. Si no, es Nuevo.
            if (this.clienteSeleccionado != null && this.clienteSeleccionado.getDoc_identidad() != null) {
                // EDICIÓN
                clienteRepository.save(this.clienteSeleccionado);
                addMessage(FacesMessage.SEVERITY_INFO, "Actualizado", "Cliente actualizado correctamente.");
                this.clienteSeleccionado = null; // Limpiar selección
            } else {
                // CREACIÓN
                clienteRepository.save(this.clienteNuevo);
                addMessage(FacesMessage.SEVERITY_INFO, "Creado", "Cliente registrado correctamente.");
                this.clienteNuevo = new Cliente(); // Limpiar formulario
            }

            // No necesitamos recargar toda la lista manualmente,
            // el LazyDataModel se refrescará automáticamente en la vista (update="dt-clientes")

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    public void eliminarCliente(Cliente cliente) {
        try {
            clienteRepository.delete(cliente.getDoc_identidad());
            addMessage(FacesMessage.SEVERITY_INFO, "Eliminado", "Cliente desactivado correctamente.");

            // Al actualizar la tabla desde el xhtml, el LazyModel volverá a cargar los datos
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