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
            // Validación de la lógica de negocio (antes de llamar al Repository)
            if (clienteNuevo.getDoc_identidad() == null || clienteNuevo.getDoc_identidad().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "El Documento de Identidad es obligatorio.");
                return;
            }
            if (clienteNuevo.getNombre() == null || clienteNuevo.getNombre().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "El Nombre es obligatorio.");
                return;
            }

            // Llama al Repository
            clienteRepository.save(clienteNuevo);

            String mensajeExito = (clienteSeleccionado == null) ? "Cliente Creado con Éxito." : "Cliente Actualizado con Éxito.";

            addMessage(FacesMessage.SEVERITY_INFO, "Éxito", mensajeExito);
            resetFormulario();
            cargarClientes();

        } catch (RuntimeException e) {
            // Manejo de errores de la API traducidos por el Repository
            if (e.getMessage().contains("409")) {
                addMessage(FacesMessage.SEVERITY_WARN, "Conflicto", "El Documento de Identidad ya existe.");
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Operación fallida: " + e.getMessage());
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_FATAL, "Error General", "Fallo al guardar: " + e.getMessage());
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

    // --- Métodos de UI y Utilidad (Resto del código igual) ---

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