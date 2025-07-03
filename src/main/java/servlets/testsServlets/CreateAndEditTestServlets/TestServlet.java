package servlets.testsServlets.CreateAndEditTestServlets;

import servlets.BaseServlet;
import constants.ServletPaths;
import dto.ResultDTO;
import dto.TestDTO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet(urlPatterns = ServletPaths.TESTS_PATTERN_PATH)
public class TestServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            List<TestDTO> allTestsDTO = testService.findAllTestsDTO();
            sendJsonResponse(resp, allTestsDTO);
            return;
        }

        String id = pathInfo.substring(1).trim();
        TestDTO testDTO = testService.findById(id);
        sendJsonResponse(resp, testDTO);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.endsWith("/submit")) {
            HttpSession session = req.getSession();
            ResultDTO resultDTO = (ResultDTO) session.getAttribute("result");

            if (resultDTO == null) {
                forwardTo(req, resp, ServletPaths.MENU_PATH);
                return;
            }
            resultService.saveResult(resultDTO);
            setSessionSuccess(req, "Test submitted successfully!");
            redirectTo(resp, ServletPaths.MENU_PATH);
            return;
        }
        if (!checkAdminRightsAndSendError(req, resp)) {
            return;
        }
        UserDTO user = getCurrentUser(req);
        TestDTO testDTO = TestDTO.builder()
                .title((getParam(req, "title")))
                .topic((getParam(req, "topic")))
                .id(UUID.randomUUID())
                .creatorId(user.getId())
                .questions(new ArrayList<>())
                .build();

        testService.checkTestTitleAndValidate(testDTO);
        setCurrentTest(req, testDTO);
        setSessionSuccess(req, "Test ready to add questions!");
        redirectTo(resp, ServletPaths.ADD_QUESTION_JSP);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!checkAdminRightsAndSendError(req, resp)) {
            return;
        }
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            String testId = pathInfo.substring(1).trim();
            testService.deleteTest(UUID.fromString(testId));
        }
        redirectTo(resp, ServletPaths.TESTS_JSP);
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!checkAdminRightsAndSendError(req, resp)) {
            return;
        }
        String testIdFromUrl = req.getPathInfo().substring(1).trim();

        String title = getParam(req, "title");
        String topic = getParam(req, "topic");


        TestDTO updatedTest = testService.updateTestDetails(UUID.fromString(testIdFromUrl), title, topic);
        setCurrentTest(req, updatedTest);
        setSessionSuccess(req, "The test has been successfully updated!");
        redirectTo(resp, ServletPaths.EDIT_QUESTIONS_MENU_JSP);
    }

}
