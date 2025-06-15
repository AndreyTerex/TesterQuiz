package Servlets.authServlets;

import Servlets.BaseServlet;
import constants.ServletPaths;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet(urlPatterns = ServletPaths.REGISTER_PATH)
public class RegistrationServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(ServletPaths.REGISTER_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDTO userDTO = UserDTO.builder()
                .username(getParam(req,"username"))
                .id(UUID.randomUUID())
                .role("USER")
                .build();
        try {
            UserDTO registeredUser = userService.registerUser(userDTO, (getParam(req,"password")));
            if (registeredUser != null) {
                req.getSession().setAttribute("user", registeredUser);
                redirectTo(resp, ServletPaths.LOGIN_PATH);
            } else {
                redirectWithError(req, resp, "Login already taken!", ServletPaths.REGISTER_PATH);
            }
        } catch (Exception e) {
            redirectWithError(req, resp, e.getMessage(), ServletPaths.REGISTER_PATH);
        }
    }
}

