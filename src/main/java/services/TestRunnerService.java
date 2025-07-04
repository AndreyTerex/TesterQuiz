package services;

import dao.TestDao;
import dto.*;
import entity.*;
import exceptions.ValidationException;
import validators.ValidatorTestRunnerService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class TestRunnerService {
    private final TestDao testDao;
    private final ResultService resultService;
    private final ValidatorTestRunnerService validatorTestRunnerService;
    private static final int TEST_DURATION_IN_MINUTES = 10;

    public TestRunnerService(TestDao testDao, ResultService resultService, ValidatorTestRunnerService validatorTestRunnerService) {
        this.testDao = testDao;
        this.resultService = resultService;
        this.validatorTestRunnerService = validatorTestRunnerService;
    }

    /**
     * Starts a test session for a user.
     */
    public TestSessionDTO startTest(UUID testId, UserDTO userDTO) {
        Test currentTest = testDao.findById(testId);
        validatorTestRunnerService.validateTestSessionStart(currentTest, userDTO);
        UUID userid = userDTO.getId();

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime roundedEndTime = startTime.plusMinutes(TEST_DURATION_IN_MINUTES).truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String roundedEndTime_formatted = roundedEndTime.format(formatter);

        Result result = resultService.buildResultObject(currentTest, userid, startTime);

        Question currentQuestion = currentTest.getQuestions().stream().findFirst().orElseThrow(() -> new ValidationException("Test " + currentTest.getTitle() + " has no questions"));
            return TestSessionDTO.builder()
                    .currentQuestion(currentQuestion.toDTO())
                    .roundedEndTime(roundedEndTime_formatted)
                    .result(result.toDTO())
                    .testTimeOut(false)
                    .build();
    }

    /**
     * Checks if the test time has ended.
     */
    public boolean checkTimeIsEnded(String endTime) {
        LocalDateTime endTime_formatted = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.isAfter(endTime_formatted);
    }


    /**
     * Proceeds to the next question and saves answers.
     */
    public TestProgressDTO nextQuestion(TestProgressDTO testProgressDTO) {
        validatorTestRunnerService.validateTestProgressDTO(testProgressDTO);

        ResultDTO resultDTO = testProgressDTO.getResult();
        QuestionDTO questionDTO = testProgressDTO.getQuestion();
        String[] answers = testProgressDTO.getAnswers();

        Test test = testDao.findById(resultDTO.getTestId());
        validatorTestRunnerService.validateTest(test);

        ResultAnswerDTO resultAnswer = getSelectedAnswersAndBuildResultAnswer(questionDTO, answers);
        resultDTO.getResultAnswers().add(resultAnswer);

        Optional<QuestionDTO> nextQuestionOptional = getNextQuestion(test.toDTO(), questionDTO);

        return getTestProgressDTO(nextQuestionOptional.orElse(null), testProgressDTO);
    }

    private Optional<QuestionDTO> getNextQuestion(TestDTO currentTest, QuestionDTO currentQuestion) {
        return currentTest.getQuestions().stream()
                .filter(question -> question.getQuestionNumber() == currentQuestion.getQuestionNumber() + 1)
                .findFirst();
    }

    private Optional<AnswerDTO> getAnswerByIdFromTest(QuestionDTO currentQuestion, UUID answerId) {
        return currentQuestion.getAnswers().stream()
                .filter(answer -> answer.getId().equals(answerId))
                .findFirst();
    }

    private TestProgressDTO getTestProgressDTO(QuestionDTO nextQuestion, TestProgressDTO testProgressDTO) {
        if (nextQuestion != null) {
            return TestProgressDTO.builder()
                    .question(nextQuestion)
                    .result(testProgressDTO.getResult())
                    .isTestFinished(false)
                    .build();
        } else {
            ResultDTO finalResult = resultService.calculateScoreResult(testProgressDTO.getResult().toEntity()).toDTO();
            return TestProgressDTO.builder()
                    .result(finalResult)
                    .isTestFinished(true)
                    .build();
        }
    }

    private ResultAnswerDTO getSelectedAnswersAndBuildResultAnswer(QuestionDTO questionDTO, String[] answers) {
        List<AnswerDTO> selectedAnswersList = new ArrayList<>();
        for (String selectedAnswer : answers) {
            UUID answerId = UUID.fromString(selectedAnswer);
            Optional<AnswerDTO> currentAnswer = getAnswerByIdFromTest(questionDTO, answerId);
            currentAnswer.ifPresent(selectedAnswersList::add);
        }
        return ResultAnswerDTO.builder()
                .question(questionDTO)
                .selectedAnswers(selectedAnswersList)
                .build();
    }
}
