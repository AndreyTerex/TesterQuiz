package Servlets.testsServlets.passingTestsServlet;

import Servlets.BaseServlet;
import constants.ServletPaths;
import dto.QuestionDTO;
import dto.ResultDTO;
import dto.TestDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

import static constants.ServletPaths.WEB_INF_DATA_TEST_RESULTS;

@WebServlet(urlPatterns = ServletPaths.NEXT_QUESTION_PATH)
public class NextQuestionServlet extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String timeForTest = (String) session.getAttribute("timeForTest");

        if (testService.checkTimeIsEnded(timeForTest)) {
            session.setAttribute("testTimeOut", true);
            setSessionError(req, "Time is out");
            redirectTo(resp, ServletPaths.PASSING_TEST_JSP);
        } else {
            String[] selectedAnswers = req.getParameterValues("selectedAnswers");

            if (selectedAnswers != null) {
                QuestionDTO currentQuestionDTO = (QuestionDTO) session.getAttribute("currentQuestion");
                ResultDTO resultDTO = (ResultDTO) session.getAttribute("result");
                TestDTO currentTestDTO = (TestDTO) session.getAttribute("currentTest");

                Map<String, Object> resultAndNextQuestion = testService.nextQuestion(selectedAnswers, currentQuestionDTO, currentTestDTO, resultDTO);

                QuestionDTO questionDTO = (QuestionDTO) resultAndNextQuestion.get("question");
                resultDTO = (ResultDTO) resultAndNextQuestion.get("result");

                if (questionDTO != null) {
                    session.setAttribute("currentQuestion", questionDTO);
                    session.setAttribute("result", resultDTO);
                    redirectTo(resp, ServletPaths.PASSING_TEST_JSP);

                } else if (resultDTO != null) {
                    session.setAttribute("result", resultDTO);
                    redirectTo(resp, ServletPaths.TEST_END_RESULT_JSP);
                }

            } else {
                setSessionError(req, "Please select at least one answer");
                redirectTo(resp, ServletPaths.PASSING_TEST_JSP);
            }
        }
    }
}
