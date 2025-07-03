package servlets.testsServlets.CreateAndEditTestServlets;

import servlets.BaseServlet;
import constants.ServletPaths;
import dto.AnswerDTO;
import dto.QuestionDTO;
import dto.TestDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebServlet(urlPatterns = ServletPaths.EDIT_TEST_PATH)
public class TestEditingManagerServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!checkAdminRightsAndSendError(req, resp)) {
            return;
        }
        redirectTo(resp, ServletPaths.START_EDIT_TEST_JSP);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        if (!checkAdminRightsAndSendError(req, resp)) {
            return;
        }
        HttpSession session = req.getSession();
        TestDTO testDTO = (TestDTO) session.getAttribute("currentTest");
        String questionId = req.getParameter("questionId");
        String QuestionText = req.getParameter("question");
        if (testDTO != null && questionId != null && QuestionText != null) {
            List<AnswerDTO> answerDTOList = extractAnswersFromRequest(req);
            if (!hasCorrectAnswer(answerDTOList)) {
                redirectWithError(req, resp, "Failed to edit question in test " + testDTO.getTitle() + "! \n" + "At least one answer must be correct!", ServletPaths.EDIT_QUESTION_JSP);
                return;
            }

            TestDTO updatedTest = testService.updateQuestion(testDTO.getId(), QuestionDTO.builder()
                    .id(UUID.fromString(questionId))
                    .questionText(QuestionText)
                    .answers(answerDTOList)
                    .build());


            setCurrentTest(req, updatedTest);
            setSessionSuccess(req, "The answers/question text of the question has been successfully updated in test " + updatedTest.getTitle() + "!");
            redirectTo(resp, ServletPaths.EDIT_QUESTIONS_MENU_JSP);

        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request parameters.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        if (!checkAdminRightsAndSendError(req, resp)) {
            return;
        }
        HttpSession session = req.getSession();
        TestDTO testDTO = (TestDTO) session.getAttribute("currentTest");
        testService.saveTest(testDTO);
        setSessionSuccess(req, "The test has been successfully saved!");
        redirectTo(resp, ServletPaths.FINISH_TEST_CREATE_JSP);

    }
}
