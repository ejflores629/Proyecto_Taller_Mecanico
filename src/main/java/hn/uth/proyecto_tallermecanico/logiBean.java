package hn.uth.proyecto_tallermecanico;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Named("loginBean") // Nombre para invocarlo desde el XHTML: #{loginBean.usuario}
@ViewScoped         // Vive mientras el usuario esté en la misma pantalla
public class logiBean implements Serializable {

        private static final long serialVersionUID = 1L;

        @Getter @Setter
        private String usuario;

        @Getter @Setter
        private String password;

        @PostConstruct
        public void init() {
            // Se ejecuta justo después de crear el bean (ideal para cargar datos iniciales)
            System.out.println("Bean iniciado...");
        }

        public String ingresar() {
            // Lógica de validación (aquí llamarías a tu Service de Retrofit)
            if ("admin".equals(usuario) && "123".equals(password)) {
                addMessage(FacesMessage.SEVERITY_INFO, "Bienvenido", "Acceso correcto");
                return "dashboard?faces-redirect=true"; // Navegación
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Credenciales incorrectas");
                return null; // Se queda en la misma página
            }
        }

        // Método utilitario para mensajes Growl de PrimeFaces
        private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
        }

}
