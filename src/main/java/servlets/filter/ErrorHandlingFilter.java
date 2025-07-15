package servlets.filter;

import exceptions.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
@Slf4j
public class ErrorHandlingFilter extends HttpFilter {
    

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException {
        try {
            chain.doFilter(req, resp);
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for request {}: {}", req.getRequestURI(), e.getMessage());

            req.getSession().setAttribute( "error", e.getMessage());
            resp.sendRedirect("/login");
        } catch (ValidationException e) {
            log.warn("Validation failed for request {}: {}", req.getRequestURI(), e.getErrors());

            req.getSession().setAttribute("error", e.getMessage());
            String referer = req.getHeader("Referer");
            resp.sendRedirect(referer != null ? referer : "/");
        }catch (RegistrationException e){
            log.warn("Registration failed for request {}: {}", req.getRequestURI(), e.getMessage());

            req.getSession().setAttribute("error", e.getMessage());
            resp.sendRedirect("/register");

        } catch (SaveException e) {
            log.warn("Save operation failed for request {}: {}", req.getRequestURI(), e.getMessage());

            req.getSession().setAttribute("error", e.getMessage());
            String referer = req.getHeader("Referer");
            resp.sendRedirect(referer != null ? referer : "/");
        } catch (TestDeletionFailedException e) {
            log.warn("Test deletion failed for request {}: {}", req.getRequestURI(), e.getMessage());

            req.getSession().setAttribute("error", e.getMessage());
            String referer = req.getHeader("Referer");
            resp.sendRedirect(referer != null ? referer : "/");
        } catch (DataAccessException e) {
            log.error("Data access error for request: {}", req.getRequestURI(), e);

            req.getSession().setAttribute( "error", "A system error occurred while accessing data. Please try again later.");
            resp.sendRedirect("/error");
        } catch (Throwable t) {
            log.error("Unhandled exception caught by filter for request: {}", req.getRequestURI(), t);

            req.getSession().setAttribute( "error", "A critical server error occurred. Please try again later.");
            resp.sendRedirect("/error");
        }
    }
}
