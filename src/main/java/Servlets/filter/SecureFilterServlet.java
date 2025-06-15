package filter;

import constants.ServletPaths;
import dto.UserDTO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Фильтр безопасности для защищенных страниц
 */
@WebFilter(urlPatterns = ServletPaths.SECURE_FILTER_PATTERN)
public class SecureFilterServlet extends HttpFilter {
    
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpSession session = request.getSession(false);
        UserDTO user = session != null ? (UserDTO) session.getAttribute("user") : null;
        
        if (user == null) {
            System.out.println("Unauthorized access attempt to: " + request.getRequestURI());
            response.sendRedirect(request.getContextPath() + ServletPaths.LOGIN_PATH);
        } else {
            chain.doFilter(request, response);
        }
    }
}

