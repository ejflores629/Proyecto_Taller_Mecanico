package hn.uth.proyecto_tallermecanico.security;

import hn.uth.proyecto_tallermecanico.viewmodel.UserSession;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"*.xhtml"})
public class AuthFilter implements Filter {

    @Inject
    private UserSession userSession;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 1. Anti-Caché
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        String reqURI = req.getRequestURI();
        boolean esLogin = reqURI.contains("/index.xhtml");
        boolean esRecurso = reqURI.contains("/jakarta.faces.resource/");

        // 2. Gestión de Login
        if (esLogin || esRecurso) {
            if (esLogin && userSession != null && userSession.isLoggedIn()) {
                res.sendRedirect(req.getContextPath() + "/orden.xhtml");
            } else {
                chain.doFilter(request, response);
            }
            return;
        }

        // 3. Validar Sesión Activa
        if (userSession == null || !userSession.isLoggedIn()) {
            res.sendRedirect(req.getContextPath() + "/index.xhtml");
            return;
        }

        // 4. CONTROL DE ROLES (ACL)
        String rol = userSession.getRolUsuario();
        boolean accesoDenegado = false;

        if ("TECNICO".equals(rol)) {
            // El Técnico NO puede entrar a:
            // - Usuarios (Seguridad del sistema)
            // CAMBIO: Se eliminó factura.xhtml de la lista negra, ahora tiene acceso.
            if (reqURI.contains("/usuario.xhtml")) {
                accesoDenegado = true;
            }
        }

        // El ADMIN tiene acceso total.

        if (accesoDenegado) {
            // Rebotar al inicio operativo
            res.sendRedirect(req.getContextPath() + "/orden.xhtml");
        } else {
            chain.doFilter(request, response);
        }
    }
}