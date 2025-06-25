package Services;

import dao.ITestDao;
import dto.*;
import entity.*;
import exceptions.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class TestRunnerService {
    private final ITestDao testDao;
    private final ResultService resultService;
    private static final int TEST_DURATION_IN_MINUTES = 10;

    public TestRunnerService(ITestDao testDao, ResultService resultService) {
        this.testDao = testDao;
        this.resultService = resultService;
    }

    /**
     * Начинает прохождение теста пользователем
     */
    public TestSessionDTO startTest(UUID testId, UserDTO userDTO) {
        Test currentTest = testDao.findById(testId);
        if (currentTest == null) {
            throw new ValidationException("Test with id " + testId + " not found");
        }
        if (userDTO == null) {
            throw new ValidationException("User not found");
        }
        UUID userid = userDTO.getId();

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime roundedEndTime = startTime.plusMinutes(TEST_DURATION_IN_MINUTES).truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String roundedEndTime_formatted = roundedEndTime.format(formatter);

        Result result = resultService.buildResultObject(currentTest, userid, startTime);

        Question currentQuestion = currentTest.getQuestions().stream().findFirst().orElse(null);
        if (currentQuestion != null) {
            return TestSessionDTO.builder()
                    .currentQuestion(currentQuestion.toDTO())
                    .roundedEndTime(roundedEndTime_formatted)
                    .result(result.toDTO())
                    .testTimeOut(false)
                    .build();
        } else {
            throw new ValidationException("Test " + currentTest.getTitle() + " has no questions");
        }
    }

    /**
     * Проверяет, истекло ли время прохождения теста
     */
    public boolean checkTimeIsEnded(String endTime) {
        LocalDateTime endTime_formatted = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.isAfter(endTime_formatted);
    }

    /**
     * Переходит к следующему вопросу теста и сохраняет ответы
     */
    public TestProgressDTO nextQuestion(TestProgressDTO testProgressDTO) {
        if(testProgressDTO.getAnswers() == null){
            throw new ValidationException("Answers are not selected");
        }
        ResultDTO resultDTO = testProgressDTO.getResult();
        Test test = testDao.findById(resultDTO.getTest_id());
        if(test == null) {
            throw new ValidationException("Test with id " + resultDTO.getTest_id() + " not found");
        }

        String [] answers = testProgressDTO.getAnswers();
            QuestionDTO questionDTO = testProgressDTO.getQuestion();
        List<AnswerDTO> selectedAnswersList = new ArrayList<>();
        for (String selectedAnswer : answers) {
            UUID answerId = UUID.fromString(selectedAnswer);
            Optional<AnswerDTO> currentAnswer = getAnswerByIdFromTest(questionDTO, answerId);
            currentAnswer.ifPresent(selectedAnswersList::add);
        }
        ResultAnswerDTO resultAnswer = ResultAnswerDTO.builder()
                .question(questionDTO)
                .selectedAnswers(selectedAnswersList)
                .build();

        resultDTO.getResultAnswers().add(resultAnswer);

        Optional<QuestionDTO> nextQuestionOptional = getNextQuestion(test.toDTO(), questionDTO);
        testProgressDTO.setQuestion(nextQuestionOptional.orElse(null));
            if (nextQuestionOptional.isEmpty()) {
                testProgressDTO.setTestFinished(true);
                Result finalResult = resultService.calculateScoreResult(resultDTO.toEntity());
                testProgressDTO.setResult(finalResult.toDTO());
        }
        return testProgressDTO;
    }

    /**
     * Получает следующий вопрос в тесте
     */
    private Optional<QuestionDTO> getNextQuestion(TestDTO currentTest, QuestionDTO currentQuestion) {
        return currentTest.getQuestions().stream()
                .filter(question -> question.getQuestion_number() == currentQuestion.getQuestion_number() + 1)
                .findFirst();
    }

    /**
     * Получает ответ по идентификатору из вопроса
     */
    private Optional<AnswerDTO> getAnswerByIdFromTest(QuestionDTO currentQuestion, UUID answerId) {
        return currentQuestion.getAnswers().stream()
                .filter(answer -> answer.getId().equals(answerId))
                .findFirst();
    }
}
