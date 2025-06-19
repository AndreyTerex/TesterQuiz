package Servlets.testsServlets.CreateAndEditTestServlets;

import Servlets.BaseServlet;
import constants.ServletPaths;
import dto.AnswerDTO;
import dto.TestDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/secure/admin/editTest")
public class EditTestServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkAdminRightsAndSendError(req, resp)) {
            return;
        }
        redirectTo(resp, "/secure/admin/editTest/startEditTest.jsp");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkAdminRightsAndSendError(req, resp)) {
            return;
        }

        HttpSession session = req.getSession();
        TestDTO currentTest = (TestDTO) session.getAttribute("currentTest");
        String title = req.getParameter("title");
        String topic = req.getParameter("topic");
        String realPath = getRealPath(req, ServletPaths.WEB_INF_DATA_TESTS);
        boolean isUpdatedTitle = testService.updateTitle(currentTest.getId(), title, realPath);
        boolean isUpdatedTopic = testService.updateTopic(currentTest.getId(), topic, realPath);
        if (isUpdatedTitle || isUpdatedTopic) {
            currentTest.setTitle(title);
            currentTest.setTopic(topic);
            setSessionSuccess(req, "The test has been successfully updated!");
        } else {
            setSessionError(req, "Failed to update the test!");
        }
        redirectTo(resp, "/secure/admin/editTest/editQuestionsMenu.jsp");
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkAdminRightsAndSendError(req, resp)) {
            return;
        }
        HttpSession session = req.getSession();
        TestDTO testDTO = (TestDTO) session.getAttribute("currentTest");
        String questionId = req.getParameter("questionId");
        String QuestionText = req.getParameter("question");
        String realPath = getRealPath(req, ServletPaths.WEB_INF_DATA_TESTS);
        if (testDTO != null && questionId != null) {
            List<AnswerDTO> answerDTOList = extractAnswersFromRequest(req);
            if (!hasCorrectAnswer(answerDTOList)) {
                redirectWithError(req, resp, "Failed to edit question in test " + testDTO.getTitle() + "! \n" + "At least one answer must be correct!", "/secure/admin/editTest/editQuestion.jsp");
                return;
            }

            boolean answersUpdated = testService.replaceAnswersAndTextOfQuestion(testDTO.getId(), questionId,QuestionText, answerDTOList, realPath);
            TestDTO updatedTest = testService.findById(String.valueOf(testDTO.getId()));

            if (answersUpdated) {
                session.setAttribute("currentTest", updatedTest);
                setSessionSuccess(req, "The answers/question text of the question has been successfully updated in test " + updatedTest.getTitle() + "!");
                redirectTo(resp, "/secure/admin/editTest/editQuestionsMenu.jsp");
            } else {
                redirectWithError(req, resp, "Failed to update the answers/question of the question in test" + updatedTest.getTitle() + "!", "/secure/admin/editTest/editQuestion.jsp");
            }

        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No test in session");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkAdminRightsAndSendError(req, resp)) {
            return;
        }
        HttpSession session = req.getSession();
        TestDTO testDTO = (TestDTO) session.getAttribute("currentTest");
        String realPath = getRealPath(req, ServletPaths.WEB_INF_DATA_TESTS);
        if(testService.saveTest(testDTO,realPath)){
            setSessionSuccess(req, "The test has been successfully saved!");
            redirectTo(resp, "/secure/admin/createTest/finishTestCreate.jsp");
        }
        else{
            redirectWithError(req, resp, "Failed to save the test!", "/secure/admin/createTest/finishTestCreate.jsp");
        }

    }
}
