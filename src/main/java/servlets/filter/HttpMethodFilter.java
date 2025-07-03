package servlets.filter;

import constants.ServletPaths;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

@WebFilter(urlPatterns = ServletPaths.HTTP_METHOD_FILTER_PATTERN)
public class HttpMethodFilter extends HttpFilter {
    
    private static final Set<String> SUPPORTED_METHODS = Set.of("DELETE", "PUT", "PATCH");
    private static final String METHOD_PARAMETER = "_method";

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        String methodOverride = request.getParameter(METHOD_PARAMETER);
        
        if (methodOverride != null && !methodOverride.trim().isEmpty() && 
            SUPPORTED_METHODS.contains(methodOverride.toUpperCase())) {
            
            HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request) {
                @Override
                public String getMethod() {
                    return methodOverride.toUpperCase();
                }
            };
            chain.doFilter(requestWrapper, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
