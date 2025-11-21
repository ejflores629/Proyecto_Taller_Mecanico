package hn.uth.proyecto_tallermecanico.viewmodel;

// Se eliminan las importaciones de RetrofitClient y ApiService
import hn.uth.proyecto_tallermecanico.model.Cliente;
import hn.uth.proyecto_tallermecanico.repository.ClienteRepository; // Nueva dependencia
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

@Named("clienteBean")
@ViewScoped
public class ClienteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ClienteRepository clienteRepository;

    @Getter
    private List<Cliente> clientes;
    @Setter
    @Getter
    private Cliente clienteSeleccionado;
    @Setter
    @Getter
    private Cliente clienteNuevo;

    @PostConstruct
    public void init() {
        this.clienteNuevo = new Cliente();
        cargarClientes();
    }


    public void cargarClientes() {
        try {
            // Llama al Repository en lugar de Retrofit
            this.clientes = clienteRepository.findAll();
            if (this.clientes == null) {
                this.clientes = List.of(); // Si hay error en findAll, retorna lista vacía.
                addMessage(FacesMessage.SEVERITY_WARN, "API Status", "No se pudieron cargar los clientes o la lista está vacía.");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_FATAL, "Error de Conexión", "Fallo al conectar con el servidor: " + e.getMessage());
        }
    }

    public void guardarCliente() {
        try {
            // CASO 1: EDICIÓN (Viene del Modal)
            if (this.clienteSeleccionado != null && this.clienteSeleccionado.getDoc_identidad() != null) {
                clienteRepository.save(this.clienteSeleccionado); // Guardamos el seleccionado
                this.clienteSeleccionado = null; // Limpiamos selección al terminar
                addMessage(FacesMessage.SEVERITY_INFO, "Actualizado", "Cliente actualizado correctamente.");
            }
            // CASO 2: CREACIÓN (Viene del Panel de Arriba)
            else {
                clienteRepository.save(this.clienteNuevo); // Guardamos el nuevo
                this.clienteNuevo = new Cliente(); // Limpiamos el formulario de arriba
                addMessage(FacesMessage.SEVERITY_INFO, "Creado", "Cliente creado correctamente.");
            }

            cargarClientes(); // Refrescar tabla

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage());
        }
    }

    public void eliminarCliente(Cliente cliente) {
        try {
            // Llama al Repository
            clienteRepository.delete(cliente.getDoc_identidad());

            addMessage(FacesMessage.SEVERITY_INFO, "Eliminado", "Cliente " + cliente.getNombre() + " desactivado.");
            cargarClientes();

        } catch (RuntimeException e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error de API", "No se pudo eliminar el cliente: " + e.getMessage());
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_FATAL, "Error de Conexión", "Fallo al eliminar: " + e.getMessage());
        }
    }


    public void seleccionarClienteParaEdicion(Cliente cliente) {
        // Se clona el objeto para evitar modificar la lista directamente si la edición falla
        this.clienteSeleccionado = cliente;
        this.clienteNuevo = new Cliente(
                cliente.getDoc_identidad(),
                cliente.getNombre(),
                cliente.getTelefono(),
                cliente.getCorreo(),
                cliente.getDireccion(),
                cliente.getActivo()
        );
    }

    public void resetFormulario() {
        this.clienteNuevo = new Cliente();
        this.clienteSeleccionado = null;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }



}