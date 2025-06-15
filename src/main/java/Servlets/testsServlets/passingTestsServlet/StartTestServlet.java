package Servlets.testsServlets.passingTestsServlet;

import Servlets.BaseServlet;
import constants.ServletPaths;
import dto.TestDTO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = ServletPaths.START_TEST_PATH)
public class StartTestServlet extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        TestDTO currentTestDTO = (TestDTO) session.getAttribute("currentTest");
        UserDTO userDTO = getCurrentUser(req);
        
        if(currentTestDTO != null && userDTO != null) {
            Map<String, Object> sessionAttributes = testService.startTest(currentTestDTO, userDTO);
            if(sessionAttributes != null) {
                for (Map.Entry<String, Object> entry : sessionAttributes.entrySet()) {
                    session.setAttribute(entry.getKey(), entry.getValue());
                }
                redirectTo(resp, ServletPaths.PASSING_TEST_JSP);
            } else {
                redirectWithError(req, resp, "Questions not found, please contact the administrator", ServletPaths.TESTS_JSP);
            }
        } else {
            redirectWithError(req, resp, "Test not found", ServletPaths.TESTS_JSP);
        }
    }
}
