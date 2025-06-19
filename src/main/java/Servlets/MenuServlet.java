package Servlets;

import constants.ServletPaths;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = ServletPaths.MENU_PATH)
public class MenuServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        clearSessionData(req);
        forwardTo(req, resp, ServletPaths.MENU_JSP);
    }
}
