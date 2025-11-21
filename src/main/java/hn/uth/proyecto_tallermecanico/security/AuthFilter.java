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

        // ========================================================================
        // 1. SOLUCIÓN AL BOTÓN "ATRÁS": CABECERAS ANTI-CACHÉ
        // ========================================================================
        // Esto obliga al navegador a no guardar la página en su memoria.
        // Si el usuario da "Atrás", el navegador tendrá que pedirla al servidor de nuevo,
        // y el filtro detectará que no hay sesión.
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        res.setDateHeader("Expires", 0);

        // ========================================================================
        // 2. LÓGICA DE SEGURIDAD (Igual que antes)
        // ========================================================================
        String reqURI = req.getRequestURI();
        boolean esLogin = reqURI.contains("/index.xhtml");
        boolean esRecurso = reqURI.contains("/jakarta.faces.resource/");

        if (esLogin || esRecurso) {
            // Si ya está logueado y quiere ir al login, lo mandamos adentro
            if (esLogin && userSession != null && userSession.isLoggedIn()) {
                res.sendRedirect(req.getContextPath() + "/orden.xhtml");
            } else {
                chain.doFilter(request, response);
            }
        } else {
            // Páginas protegidas
            if (userSession != null && userSession.isLoggedIn()) {
                // Tiene sesión -> PASE
                chain.doFilter(request, response);
            } else {
                // No tiene sesión -> AL LOGIN
                res.sendRedirect(req.getContextPath() + "/index.xhtml");
            }
        }
    }
}