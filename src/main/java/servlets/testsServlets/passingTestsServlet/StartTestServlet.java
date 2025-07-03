package servlets.testsServlets.passingTestsServlet;

import servlets.BaseServlet;
import constants.ServletPaths;
import dto.TestDTO;
import dto.TestSessionDTO;
import dto.UserDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = ServletPaths.START_TEST_PATH)
public class StartTestServlet extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        TestDTO currentTestDTO = (TestDTO) session.getAttribute("currentTest");
        UserDTO userDTO = getCurrentUser(req);
        TestSessionDTO sessionAttributes = testRunnerService.startTest(currentTestDTO.getId(), userDTO);
        setTestSessionAttributes(req, sessionAttributes);
        redirectTo(resp, ServletPaths.PASSING_TEST_JSP);
    }
}
