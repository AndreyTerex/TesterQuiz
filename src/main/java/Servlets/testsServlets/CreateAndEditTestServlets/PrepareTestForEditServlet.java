package Servlets.testsServlets.CreateAndEditTestServlets;

import Servlets.BaseServlet;
import constants.ServletPaths;
import dto.TestDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = ServletPaths.PREPARE_EDIT_TEST_PATH)
public class PrepareTestForEditServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!checkAdminRightsAndSendError(req, resp)) {
            return;
        }
            String id = req.getParameter("id");
            TestDTO testDTO = testService.findById(id);
            setCurrentTest(req, testDTO);
            redirectTo(resp, ServletPaths.EDIT_TEST_PATH);
    }
}
