package Servlets.testsServlets;

import Servlets.BaseServlet;
import constants.ServletPaths;
import dto.AnswerDTO;
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo) || pathInfo.isEmpty()) {
            List<TestDTO> allTestsDTO = testService.findAllTestsDTO();
            sendJsonResponse(resp, allTestsDTO);
        } else {
            String id = pathInfo.substring(1);
            TestDTO testDTO = testService.findById(id);
            if (testDTO != null) {
                sendJsonResponse(resp, testDTO);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDTO user = getCurrentUser(req);
        TestDTO testDTO = TestDTO.builder()
                .title((getParam(req,"title")))
                .topic((getParam(req,"topic")))
                .id(UUID.randomUUID())
                .creator_id(user.getId())
                .questions(new ArrayList<>())
                .build();

        if (testService.existByTitle(testDTO)) {
            redirectWithError(req, resp, "Such a test already exists!", ServletPaths.CREATE_TEST_START_JSP);
        } else if (testService.createTest(testDTO)) {
            req.getSession().setAttribute("currentTest", testDTO);
            setSessionSuccess(req, "Test successfully created! Now add questions");
            redirectTo(resp, ServletPaths.ADD_QUESTION_JSP);
        } else {
            redirectWithError(req, resp, "Failed to create test!", ServletPaths.CREATE_TEST_START_JSP);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkAdminRightsAndSendError(req, resp)) {
            return;
        }
        
        String pathInfo = req.getPathInfo();
        String realPath = getRealPath(req, ServletPaths.WEB_INF_DATA_TESTS);
        boolean isDeleted = testService.deleteTest(pathInfo, realPath);
        
        if (isDeleted) {
            resp.sendRedirect("/secure/tests.jsp");
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete test");
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        TestDTO testDTO = (TestDTO) session.getAttribute("currentTest");
        String realPath = getRealPath(req, ServletPaths.WEB_INF_DATA_TESTS);
        if (testDTO != null) {
            List<AnswerDTO> answerDTOList = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                String answerText = (getParam(req,"answer" + i));
                String isCorrect = (getParam(req,"correct" + i));

                if (answerText != null && !answerText.trim().isEmpty()) {
                    AnswerDTO answer  = AnswerDTO.builder()
                            .id(UUID.randomUUID())
                            .answer_text(answerText)
                            .isCorrect("true".equals(isCorrect))
                            .build();

                    answerDTOList.add(answer);
                }
            }
            if (answerDTOList.stream().noneMatch(AnswerDTO::isCorrect)) {
                redirectWithError(req, resp, "Failed to add question to test " + testDTO.getTitle() + "! \n" + "At least one answer must be correct!", ServletPaths.CONTINUE_OR_END_MENU_JSP);
                return;
            }

            TestDTO currentTestDTO = testService.addQuestion(testDTO, (getParam(req,"question")), answerDTOList, realPath);
            
            if(currentTestDTO != null){
                session.setAttribute("currentTest", currentTestDTO);
                setSessionSuccess(req, "The question has been successfully added to the test " + testDTO.getTitle() + "!");
                redirectTo(resp, ServletPaths.CONTINUE_OR_END_MENU_JSP);
            } else{
                redirectWithError(req, resp, "Failed to add question to test " + testDTO.getTitle() + "!", ServletPaths.CONTINUE_OR_END_MENU_JSP);
            }

        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No test in session");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }
}

