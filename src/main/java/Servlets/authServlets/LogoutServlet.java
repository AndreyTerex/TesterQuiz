package Servlets.authServlets;

import Servlets.BaseServlet;
import constants.ServletPaths;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = ServletPaths.LOGOUT_PATH)
public class LogoutServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getSession().invalidate();
        redirectTo(resp, ServletPaths.INDEX_JSP);
    }
}
