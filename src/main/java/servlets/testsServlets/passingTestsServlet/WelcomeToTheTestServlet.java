package servlets.testsServlets.passingTestsServlet;

import servlets.BaseServlet;
import constants.ServletPaths;
import dto.TestDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet(ServletPaths.WELCOME_TO_TEST_PATH)
public class WelcomeToTheTestServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = getParam(req, "id");
        TestDTO currentTestDTO = testService.findDTOById(UUID.fromString(id));
        setCurrentTest(req, currentTestDTO);
        forwardTo(req, resp, ServletPaths.TEST_WELCOME_PAGE_JSP);
    }
}
