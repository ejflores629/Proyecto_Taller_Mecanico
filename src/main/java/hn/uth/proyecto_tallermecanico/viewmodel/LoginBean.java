package hn.uth.proyecto_tallermecanico.viewmodel;

import hn.uth.proyecto_tallermecanico.model.Usuario;
import hn.uth.proyecto_tallermecanico.repository.UsuarioRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

@Named("loginBean")
@RequestScoped
public class LoginBean {

    @Inject
    private UsuarioRepository usuarioRepository;

    @Inject
    private UserSession userSession;

    @Getter @Setter
    private String usuario; // Es el DNI

    @Getter @Setter
    private String password;

    public void ingresar() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            // 1. Buscar usuario en BD por DNI
            Usuario userEncontrado = usuarioRepository.findById(this.usuario);

            // 2. Validar credenciales con seguridad "Null-Safe"
            // Verificamos que el usuario exista, que tenga clave (no nula), que coincida y que esté activo.
            if (userEncontrado != null &&
                    userEncontrado.getClave() != null &&
                    userEncontrado.getClave().equals(this.password) &&
                    "S".equals(userEncontrado.getActivo())) {

                // 3. Guardar usuario en la sesión (SessionScoped)
                userSession.setUsuarioLogin(userEncontrado);

                // 4. Redirigir al menú principal (Ordenes)
                ExternalContext ec = context.getExternalContext();
                ec.redirect(ec.getRequestContextPath() + "/orden.xhtml");

            } else {
                // Error de credenciales o usuario inactivo
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Acceso Denegado", "Usuario o contraseña incorrectos."));
            }
        } catch (Exception e) {
            // Error de conexión o servidor
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error de Sistema", "No se pudo conectar con el servidor."));
            e.printStackTrace(); // Útil para ver el error en la consola del servidor
        }
    }

    public void cerrarSesion() throws IOException {
        // Limpiar sesión del bean
        userSession.cerrarSesion();

        // Invalidar la sesión HTTP completa
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        // Redirigir al Login
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(ec.getRequestContextPath() + "/index.xhtml");
    }
}