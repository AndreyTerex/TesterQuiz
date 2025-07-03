package servlets.filter;

import exceptions.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebFilter("/*")
public class ErrorHandlingFilter extends HttpFilter {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingFilter.class);

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException {
        try {
            chain.doFilter(req, resp);
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for request {}: {}", req.getRequestURI(), e.getMessage());

            req.getSession().setAttribute( "error", e.getMessage());
            resp.sendRedirect("/login");
        } catch (ValidationException e) {
            logger.warn("Validation failed for request {}: {}", req.getRequestURI(), e.getErrors());

            req.getSession().setAttribute("error", e.getMessage());
            String referer = req.getHeader("Referer");
            resp.sendRedirect(referer != null ? referer : "/");
        }catch (RegistrationException e){
            logger.warn("Registration failed for request {}: {}", req.getRequestURI(), e.getMessage());

            req.getSession().setAttribute("error", e.getMessage());
            resp.sendRedirect("/register");

        } catch (SaveException e) {
            logger.warn("Save operation failed for request {}: {}", req.getRequestURI(), e.getMessage());

            req.getSession().setAttribute("error", e.getMessage());
            String referer = req.getHeader("Referer");
            resp.sendRedirect(referer != null ? referer : "/");
        } catch (TestDeletionFailedException e) {
            logger.warn("Test deletion failed for request {}: {}", req.getRequestURI(), e.getMessage());

            req.getSession().setAttribute("error", e.getMessage());
            String referer = req.getHeader("Referer");
            resp.sendRedirect(referer != null ? referer : "/");
        } catch (DataAccessException e) {
            logger.error("Data access error for request: {}", req.getRequestURI(), e);

            req.getSession().setAttribute( "error", "A system error occurred while accessing data. Please try again later.");
            resp.sendRedirect("/error");
        } catch (Throwable t) {
            logger.error("Unhandled exception caught by filter for request: {}", req.getRequestURI(), t);

            req.getSession().setAttribute( "error", "A critical server error occurred. Please try again later.");
            resp.sendRedirect("/error");
        }
    }
}
