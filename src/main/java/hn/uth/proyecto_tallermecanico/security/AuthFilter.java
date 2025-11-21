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
        String reqURI = req.getRequestURI();

        // Permitir acceso a login (index.xhtml) y recursos estáticos (CSS/JS/Images)
        boolean esLogin = reqURI.contains("/index.xhtml");
        boolean esRecurso = reqURI.contains("/jakarta.faces.resource/");

        if (esLogin || esRecurso) {
            // Si ya está logueado y quiere ir al login, mandarlo adentro
            if (esLogin && userSession != null && userSession.isLoggedIn()) {
                res.sendRedirect(req.getContextPath() + "/orden.xhtml");
            } else {
                chain.doFilter(request, response);
            }
        } else {
            // Si intenta entrar a otra página...
            if (userSession != null && userSession.isLoggedIn()) {
                // ...y está logueado -> PASE
                chain.doFilter(request, response);
            } else {
                // ...y NO está logueado -> AL LOGIN
                res.sendRedirect(req.getContextPath() + "/index.xhtml");
            }
        }
    }
}