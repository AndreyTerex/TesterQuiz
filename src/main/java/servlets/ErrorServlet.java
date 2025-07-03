package servlets;

import constants.ServletPaths;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(ServletPaths.ERROR_PATH)
public class ErrorServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        forwardTo(req, resp, ServletPaths.ERROR_JSP);
    }
}
