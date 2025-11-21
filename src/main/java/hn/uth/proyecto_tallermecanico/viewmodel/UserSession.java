package hn.uth.proyecto_tallermecanico.viewmodel;

import hn.uth.proyecto_tallermecanico.model.Usuario;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Named("userSession")
@SessionScoped
public class UserSession implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter @Setter
    private Usuario usuarioLogin;

    public boolean isLoggedIn() {
        return usuarioLogin != null;
    }

    public String getNombreUsuario() {
        return usuarioLogin != null ? usuarioLogin.getNombre_completo() : "Invitado";
    }

    public String getRolUsuario() {
        return usuarioLogin != null ? usuarioLogin.getRol() : "";
    }

    public void cerrarSesion() {
        this.usuarioLogin = null;
    }
}