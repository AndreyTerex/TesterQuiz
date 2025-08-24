package servlets.testsServlets.passingTestsServlet;

import constants.ServletPaths;
import dto.QuestionDTO;
import dto.ResultDTO;
import dto.TestProgressDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import servlets.BaseServlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@WebServlet(urlPatterns = ServletPaths.NEXT_QUESTION_PATH)
public class NextQuestionServlet extends BaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String timeForTest = (String) session.getAttribute("timeForTest");

        if (testRunnerService.checkTimeIsEnded(timeForTest)) {
            session.setAttribute("testTimeOut", true);
            setSessionError(req, "Time is out");
            redirectTo(resp, ServletPaths.PASSING_TEST_JSP);
        } else {
                String[] selectedAnswers = req.getParameterValues("selectedAnswers");
            List<String> selected = selectedAnswers != null ? Arrays.asList(selectedAnswers) : Collections.emptyList();
                QuestionDTO currentQuestionDTO = (QuestionDTO) session.getAttribute("currentQuestion");
                ResultDTO resultDTO = (ResultDTO) session.getAttribute("result");

                TestProgressDTO testProgressDTOBuild = TestProgressDTO.builder()
                        .result(resultDTO)
                        .question(currentQuestionDTO)
                        .answers(selected)
                        .build();
            TestProgressDTO UpdatedTestProgressDTO = testRunnerService.nextQuestion(testProgressDTOBuild);

                updateTestProgress(req, UpdatedTestProgressDTO);

                if (UpdatedTestProgressDTO.isTestFinished()) {
                    redirectTo(resp, ServletPaths.TEST_END_RESULT_JSP);
                } else {
                    redirectTo(resp, ServletPaths.PASSING_TEST_JSP);
                }

        }
    }
}
