package servlets.authServlets;

import servlets.BaseServlet;
import constants.ServletPaths;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = ServletPaths.REGISTER_PATH)
public class RegistrationServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        forwardTo(req,resp,ServletPaths.REGISTER_JSP);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDTO userDTO = UserDTO.builder()
                .username(getParam(req,"username"))
                .build();
        UserDTO registeredUser = userService.registerUser(userDTO, getParam(req, "password"));
        setCurrentUser(req, registeredUser);
        setSessionSuccess(req, "Registration successful! Please log in.");
        redirectTo(resp, ServletPaths.LOGIN_PATH);
    }
}

