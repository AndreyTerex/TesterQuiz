package servlets.filter;

import constants.ServletPaths;
import dto.UserDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;


@WebFilter(urlPatterns = ServletPaths.ADMIN_FILTER_PATTERN)
public class AdminFilterServlet extends HttpFilter {
    
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpSession session = request.getSession(false);
        UserDTO user = session != null ? (UserDTO) session.getAttribute("user") : null;
        
        if (user == null || !"ADMIN".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + ServletPaths.MENU_PATH);
        } else {
            chain.doFilter(request, response);
        }
    }
}
