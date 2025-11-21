package hn.uth.proyecto_tallermecanico.viewmodel;

import hn.uth.proyecto_tallermecanico.model.Usuario;
import hn.uth.proyecto_tallermecanico.repository.UsuarioRepository;
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

@Named("usuarioBean")
@ViewScoped
public class UsuarioBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UsuarioRepository usuarioRepository;

    @Getter
    private LazyDataModel<Usuario> usuariosLazy;

    @Getter @Setter
    private Usuario usuarioSeleccionado;

    @Getter @Setter
    private Usuario usuarioNuevo;

    @PostConstruct
    public void init() {
        this.usuarioNuevo = new Usuario();
        iniciarLazyModel();
    }

    private void iniciarLazyModel() {
        this.usuariosLazy = new LazyDataModel<Usuario>() {
            private static final long serialVersionUID = 1L;

            @Override
            public int count(Map<String, FilterMeta> filterBy) {
                return (int) usuarioRepository.count();
            }

            @Override
            public List<Usuario> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                List<Usuario> data = usuarioRepository.findRange(first, pageSize);
                this.setRowCount((int) usuarioRepository.count());
                return data;
            }

            @Override
            public String getRowKey(Usuario usuario) {
                return usuario.getDoc_identidad();
            }

            @Override
            public Usuario getRowData(String rowKey) {
                return usuarioRepository.findById(rowKey);
            }
        };
    }

    public void guardarUsuario() {
        try {
            // LÓGICA CORREGIDA: Separamos explícitamente Edición vs Creación
            if (this.usuarioSeleccionado != null && this.usuarioSeleccionado.getDoc_identidad() != null) {
                // EDICIÓN -> Llamamos a update()
                usuarioRepository.update(this.usuarioSeleccionado);
                addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Usuario actualizado correctamente.");
                this.usuarioSeleccionado = null;
            } else {
                // CREACIÓN -> Llamamos a create()
                usuarioRepository.create(this.usuarioNuevo);
                addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Usuario creado correctamente.");
                this.usuarioNuevo = new Usuario();
            }
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    public void eliminarUsuario(Usuario usuario) {
        try {
            usuarioRepository.delete(usuario.getDoc_identidad());
            addMessage(FacesMessage.SEVERITY_INFO, "Eliminado", "Usuario desactivado correctamente.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar: " + e.getMessage());
        }
    }

    public void resetFormulario() {
        this.usuarioNuevo = new Usuario();
        this.usuarioSeleccionado = null;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}