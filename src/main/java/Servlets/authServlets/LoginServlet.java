package Servlets.authServlets;

import Servlets.BaseServlet;
import constants.ServletPaths;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = ServletPaths.LOGIN_PATH)
public class LoginServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(ServletPaths.LOGIN_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDTO userDTO = userService.login((getParam(req,"username")), (getParam(req,"password")));

        if (userDTO != null) {
            req.getSession().setAttribute("user", userDTO);
            redirectTo(resp, ServletPaths.MENU_PATH);
        } else {
            redirectWithError(req, resp, "Incorrect login or password", ServletPaths.LOGIN_PATH);
        }
    }
}

