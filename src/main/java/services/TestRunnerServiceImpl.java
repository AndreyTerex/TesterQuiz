package services;

import dao.TestDAO;
import dto.*;
import entity.*;
import mappers.QuestionMapper;
import mappers.ResultMapper;
import mappers.TestMapper;
import exceptions.ValidationException;
import mappers.UserMapper;
import services.interfaces.ResultService;
import services.interfaces.TestRunnerService;
import validators.ValidatorTestRunnerService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TestRunnerServiceImpl implements TestRunnerService {
    private final TestDAO testDao;
    private final ResultService resultService;
    private final ValidatorTestRunnerService validatorTestRunnerService;
    private final TestMapper testMapper;
    private final QuestionMapper questionMapper;
    private final ResultMapper resultMapper;
    private final UserMapper userMapper;
    private static final int TEST_DURATION_IN_MINUTES = 10;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public TestRunnerServiceImpl(TestDAO testDao, ResultService resultService, ValidatorTestRunnerService validatorTestRunnerService, TestMapper testMapper, QuestionMapper questionMapper, ResultMapper resultMapper, UserMapper userMapper) {
        this.testDao = testDao;
        this.resultService = resultService;
        this.validatorTestRunnerService = validatorTestRunnerService;
        this.testMapper = testMapper;
        this.questionMapper = questionMapper;
        this.resultMapper = resultMapper;
        this.userMapper = userMapper;
    }

    public TestSessionDTO startTest(UUID testId, UserDTO userDTO) {
        Test currentTest = testDao.findByIdWithDetails(testId).orElseThrow(() -> new ValidationException("Test not found"));
        validatorTestRunnerService.validateTestSessionStart(currentTest, userDTO);
        User user = userMapper.toEntity(userDTO);

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime roundedEndTime = startTime.plusMinutes(TEST_DURATION_IN_MINUTES);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String roundedEndTime_formatted = roundedEndTime.format(formatter);

        Result result = resultService.buildStartResultObject(currentTest, user, startTime);

        Question currentQuestion = currentTest.getQuestions().stream().findFirst().orElseThrow(() -> new ValidationException("Test " + currentTest.getTitle() + " has no questions"));
            return TestSessionDTO.builder()
                    .currentQuestion(questionMapper.toDTO(currentQuestion))
                    .roundedEndTime(roundedEndTime_formatted)
                    .result(resultMapper.toDTO(result))
                    .testTimeOut(false)
                    .build();
    }

    public boolean checkTimeIsEnded(String endTime) {
        LocalDateTime endTime_formatted = LocalDateTime.parse(endTime, ISO_FORMATTER);
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.isAfter(endTime_formatted);
    }


    public TestProgressDTO nextQuestion(TestProgressDTO testProgressDTO) {
        validatorTestRunnerService.validateTestProgressDTO(testProgressDTO);

        ResultDTO resultDTO = testProgressDTO.getResult();
        QuestionDTO questionDTO = testProgressDTO.getQuestion();
        String[] answers = testProgressDTO.getAnswers();

        Test test = testDao.findByIdWithDetails(resultDTO.getTestId()).orElseThrow(() -> new ValidationException("Test not found"));
        validatorTestRunnerService.validateTest(test);

        AnswersInResultDTO answerInResultDTO = buildAnswerInResult(questionDTO, answers);
        resultDTO.getAnswersInResults().add(answerInResultDTO);

        Optional<QuestionDTO> nextQuestionOptional = getNextQuestion(testMapper.toDTO(test), questionDTO);

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
            ResultDTO finalResult = resultMapper.toDTO(resultService.calculateScoreResult(resultMapper.toEntity(testProgressDTO.getResult())));
            return TestProgressDTO.builder()
                    .result(finalResult)
                    .isTestFinished(true)
                    .build();
        }
    }

    private AnswersInResultDTO buildAnswerInResult(QuestionDTO questionDTO, String[] answers) {
        List<AnswerDTO> selectedAnswersList = new ArrayList<>();
        for (String selectedAnswer : answers) {
            UUID answerId = UUID.fromString(selectedAnswer);
            Optional<AnswerDTO> currentAnswer = getAnswerByIdFromTest(questionDTO, answerId);
            currentAnswer.ifPresent(selectedAnswersList::add);
        }
        return AnswersInResultDTO.builder()
                .question(questionDTO)
                .selectedAnswers(selectedAnswersList)
                .build();
    }


}
