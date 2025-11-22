package hn.uth.proyecto_tallermecanico.viewmodel;

import hn.uth.proyecto_tallermecanico.model.Usuario;
import hn.uth.proyecto_tallermecanico.repository.UsuarioRepository;
import hn.uth.proyecto_tallermecanico.util.CifradoUtil; // Asegúrate de tener esta clase creada
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
    private String usuario; // DNI del usuario

    @Getter @Setter
    private String password; // Contraseña (lo que escribe el usuario)

    public void ingresar() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            // 1. Buscar al usuario en la BD por su DNI
            // La API nos devuelve el objeto con la clave YA CIFRADA (si se guardó así)
            Usuario userEncontrado = usuarioRepository.findById(this.usuario);

            // 2. Cifrar lo que el usuario acaba de escribir en el input
            // Usamos la misma lógica "César Dinámico" (llave = largo de la cadena)
            String passInputCifrado = "";
            if (this.password != null) {
                passInputCifrado = CifradoUtil.cifrar(this.password);
            }

            // 3. Validación de Credenciales (Blindada contra Nulos)
            boolean credencialesValidas = false;

            if (userEncontrado != null) {
                String claveAlmacenada = userEncontrado.getClave();
                String estadoActivo = userEncontrado.getActivo();

                // Verificamos que:
                // a) La clave en BD no sea nula
                // b) La clave cifrada del input coincida con la de la BD
                // c) El usuario esté activo ('S')
                if (claveAlmacenada != null &&
                        claveAlmacenada.equals(passInputCifrado) &&
                        "S".equals(estadoActivo)) {
                    credencialesValidas = true;
                }
            }

            // 4. Acciones según el resultado
            if (credencialesValidas) {
                // Login Exitoso: Guardar en sesión
                userSession.setUsuarioLogin(userEncontrado);

                // Redirigir al área de trabajo (Ordenes)
                ExternalContext ec = context.getExternalContext();
                ec.redirect(ec.getRequestContextPath() + "/home.xhtml");
            } else {
                // Login Fallido
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Acceso Denegado", "Usuario o contraseña incorrectos."));
            }

        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error de Sistema", "No se pudo conectar con el servidor."));
        }
    }

    public void cerrarSesion() throws IOException {
        // 1. Limpiar el bean de sesión
        if (userSession != null) {
            userSession.cerrarSesion();
        }

        // 2. Invalidar la sesión HTTP completa (borrar cookies de sesión del lado servidor)
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        // 3. Redirigir al Login
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(ec.getRequestContextPath() + "/index.xhtml");
    }
}