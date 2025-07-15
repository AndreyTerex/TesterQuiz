package servlets.testsServlets.CreateAndEditTestServlets;

import servlets.BaseServlet;
import constants.ServletPaths;
import dto.AnswerDTO;
import dto.QuestionDTO;
import dto.TestDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = ServletPaths.ADD_QUESTION_PATH)
public class AddQuestionToTestServlet extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        if (!checkAdminRightsAndSendError(req, resp)) {
            return;
        }
        HttpSession session = req.getSession();
        TestDTO testDTO = (TestDTO) session.getAttribute("currentTest");
        String questionText = (getParam(req, "question"));

            List<AnswerDTO> answerDTOList = extractAnswersFromRequest(req);

            testDTO = testService.addQuestion(testDTO, QuestionDTO.builder()
                                                                  .questionText(questionText)
                                                                  .answers(answerDTOList)
                                                                  .build());

            setCurrentTest(req, testDTO);
            setSessionSuccess(req, "The question has been successfully added to the test " + testDTO.getTitle() + "!");
            redirectTo(resp, ServletPaths.CONTINUE_OR_END_MENU_JSP);
    }
}
