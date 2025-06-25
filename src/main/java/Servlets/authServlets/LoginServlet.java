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
        forwardTo(req,resp,ServletPaths.LOGIN_JSP);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDTO userDTO = userService.login(getParam(req, "username"), getParam(req, "password"));
        setCurrentUser(req, userDTO);
        redirectTo(resp, ServletPaths.MENU_PATH);
    }
}

