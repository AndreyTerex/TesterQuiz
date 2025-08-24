package services;

import dao.TestDAO;
import dto.*;
import entity.Question;
import entity.Result;
import entity.User;
import exceptions.ValidationException;
import mappers.QuestionMapper;
import mappers.ResultMapper;
import mappers.TestMapper;
import mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import services.interfaces.ResultService;
import validators.ValidatorTestRunnerService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestRunnerServiceImplTest {
    @Mock
    private TestDAO testDao;
    @Mock
    private ResultService resultService;
    @Mock
    private ValidatorTestRunnerService validatorTestRunnerService;
    @Mock
    private TestMapper testMapper;
    @Mock
    private QuestionMapper questionMapper;
    @Mock
    private ResultMapper resultMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private TestRunnerServiceImpl testRunnerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testRunnerService = new TestRunnerServiceImpl(testDao, resultService, validatorTestRunnerService, testMapper, questionMapper, resultMapper, userMapper);
    }

    @Test
    @DisplayName("startTest returns first question and result on success")
    void startTest_success() {
        // ARRANGE
        UUID testId = UUID.randomUUID();
        UserDTO userDTO = UserDTO.builder().username("user").build();
        entity.Test test = TestDataBuilders.test();
        Question question = TestDataBuilders.question(new ArrayList<>(), 1);
        test.setQuestions(List.of(question));
        User user = TestDataBuilders.user();
        Result result = TestDataBuilders.result(new ArrayList<>());
        ResultDTO resultDTO = mock(ResultDTO.class);
        QuestionDTO questionDTO = mock(QuestionDTO.class);
        when(testDao.findByIdWithDetails(testId)).thenReturn(Optional.of(test));
        doNothing().when(validatorTestRunnerService).validateTestSessionStart(test, userDTO);
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(resultService.buildStartResultObject(eq(test), eq(user), any())).thenReturn(result);
        when(questionMapper.toDTO(question)).thenReturn(questionDTO);
        when(resultMapper.toDTO(result)).thenReturn(resultDTO);
        // ACT
        TestSessionDTO session = testRunnerService.startTest(testId, userDTO);
        // ASSERT
        assertEquals(questionDTO, session.getCurrentQuestion());
        assertEquals(resultDTO, session.getResult());
        assertFalse(session.isTestTimeOut());
    }

    @Test
    @DisplayName("startTest throws if test not found")
    void startTest_throwsIfTestNotFound() {
        // ARRANGE
        UUID testId = UUID.randomUUID();
        UserDTO userDTO = UserDTO.builder().username("user").build();
        when(testDao.findByIdWithDetails(testId)).thenReturn(Optional.empty());
        // ACT & ASSERT
        assertThrows(ValidationException.class, () -> testRunnerService.startTest(testId, userDTO));
    }

    @Test
    @DisplayName("startTest throws if no questions in test")
    void startTest_throwsIfNoQuestions() {
        // ARRANGE
        UUID testId = UUID.randomUUID();
        UserDTO userDTO = UserDTO.builder().username("user").build();
        entity.Test test = TestDataBuilders.test();
        test.setQuestions(new ArrayList<>());
        when(testDao.findByIdWithDetails(testId)).thenReturn(Optional.of(test));
        doNothing().when(validatorTestRunnerService).validateTestSessionStart(test, userDTO);
        when(userMapper.toEntity(userDTO)).thenReturn(TestDataBuilders.user());
        // ACT & ASSERT
        assertThrows(ValidationException.class, () -> testRunnerService.startTest(testId, userDTO));
    }

    @Test
    @DisplayName("startTest throws if validation fails")
    void startTest_throwsIfValidationFails() {
        // ARRANGE
        UUID testId = UUID.randomUUID();
        UserDTO userDTO = UserDTO.builder().username("user").build();
        entity.Test test = TestDataBuilders.test();
        Question question = TestDataBuilders.question(new ArrayList<>(), 1);
        test.setQuestions(List.of(question));
        when(testDao.findByIdWithDetails(testId)).thenReturn(Optional.of(test));
        doThrow(new ValidationException("fail")).when(validatorTestRunnerService).validateTestSessionStart(test, userDTO);
        // ACT & ASSERT
        assertThrows(ValidationException.class, () -> testRunnerService.startTest(testId, userDTO));
    }

    @Test
    @DisplayName("checkTimeIsEnded true if after end time")
    void checkTimeIsEnded_trueIfAfter() {
        // ARRANGE
        LocalDateTime now = LocalDateTime.now();
        String endTime = now.minusMinutes(1).format(DateTimeFormatter.ISO_DATE_TIME);
        // ACT & ASSERT
        assertTrue(testRunnerService.checkTimeIsEnded(endTime));
    }

    @Test
    @DisplayName("checkTimeIsEnded false if before end time")
    void checkTimeIsEnded_falseIfBefore() {
        // ARRANGE
        LocalDateTime now = LocalDateTime.now();
        String endTime = now.plusMinutes(1).format(DateTimeFormatter.ISO_DATE_TIME);
        // ACT & ASSERT
        assertFalse(testRunnerService.checkTimeIsEnded(endTime));
    }

    @Test
    @DisplayName("nextQuestion returns next question")
    void nextQuestion_successNext() {
        // ARRANGE
        UUID testId = UUID.randomUUID();
        QuestionDTO q1 = TestDataBuilders.questionDTO(UUID.randomUUID(), 1);
        QuestionDTO q2 = TestDataBuilders.questionDTO(UUID.randomUUID(), 2);
        List<QuestionDTO> questions = List.of(q1, q2);
        TestDTO testDTO = TestDTO.builder().id(testId).questions(questions).build();
        entity.Test test = TestDataBuilders.test();
        test.setQuestions(new ArrayList<>());
        doNothing().when(validatorTestRunnerService).validateTestProgressDTO(any());
        doNothing().when(validatorTestRunnerService).validateTest(test);
        when(testDao.findByIdWithDetails(testId)).thenReturn(Optional.of(test));
        when(testMapper.toDTO(test)).thenReturn(testDTO);
        TestProgressDTO progress = TestProgressDTO.builder()
                .result(TestDataBuilders.resultDTO(UUID.randomUUID(), testId, new ArrayList<>()))
                .question(q1)
                .answers(java.util.Collections.emptyList())
                .build();
        // ACT
        TestProgressDTO result = testRunnerService.nextQuestion(progress);
        // ASSERT
        assertEquals(q2, result.getQuestion());
        assertFalse(result.isTestFinished());
    }

    @Test
    @DisplayName("nextQuestion returns result if last question")
    void nextQuestion_successFinish() {
        // ARRANGE
        UUID testId = UUID.randomUUID();
        QuestionDTO q1 = TestDataBuilders.questionDTO(UUID.randomUUID(), 1);
        List<QuestionDTO> questions = List.of(q1);
        TestDTO testDTO = TestDTO.builder().id(testId).questions(questions).build();
        entity.Test test = TestDataBuilders.test();
        test.setQuestions(new ArrayList<>());
        doNothing().when(validatorTestRunnerService).validateTestProgressDTO(any());
        doNothing().when(validatorTestRunnerService).validateTest(test);
        when(testDao.findByIdWithDetails(testId)).thenReturn(Optional.of(test));
        when(testMapper.toDTO(test)).thenReturn(testDTO);
        ResultDTO resultDTO = mock(ResultDTO.class);
        Result result = mock(Result.class);
        when(resultMapper.toEntity(any())).thenReturn(result);
        when(resultService.calculateScoreResult(result)).thenReturn(result);
        when(resultMapper.toDTO(result)).thenReturn(resultDTO);
        TestProgressDTO progress = TestProgressDTO.builder()
                .result(TestDataBuilders.resultDTO(UUID.randomUUID(), testId, new ArrayList<>()))
                .question(q1)
                .answers(java.util.Collections.emptyList())
                .build();
        // ACT
        TestProgressDTO resultProgress = testRunnerService.nextQuestion(progress);
        // ASSERT
        assertTrue(resultProgress.isTestFinished());
        assertEquals(resultDTO, resultProgress.getResult());
    }

    @Test
    @DisplayName("nextQuestion throws if test not found")
    void nextQuestion_throwsIfTestNotFound() {
        // ARRANGE
        TestProgressDTO progress = mock(TestProgressDTO.class);
        ResultDTO resultDTO = mock(ResultDTO.class);
        when(progress.getResult()).thenReturn(resultDTO);
        when(resultDTO.getTestId()).thenReturn(UUID.randomUUID());
        doNothing().when(validatorTestRunnerService).validateTestProgressDTO(progress);
        when(testDao.findByIdWithDetails(any())).thenReturn(Optional.empty());
        // ACT & ASSERT
        assertThrows(ValidationException.class, () -> testRunnerService.nextQuestion(progress));
    }

    @Test
    @DisplayName("nextQuestion throws if progress invalid")
    void nextQuestion_throwsIfInvalidProgress() {
        // ARRANGE
        TestProgressDTO progress = mock(TestProgressDTO.class);
        doThrow(new ValidationException("fail")).when(validatorTestRunnerService).validateTestProgressDTO(progress);
        // ACT & ASSERT
        assertThrows(ValidationException.class, () -> testRunnerService.nextQuestion(progress));
    }

    @Test
    @DisplayName("nextQuestion throws if test invalid")
    void nextQuestion_throwsIfInvalidTest() {
        // ARRANGE
        TestProgressDTO progress = mock(TestProgressDTO.class);
        ResultDTO resultDTO = mock(ResultDTO.class);
        when(progress.getResult()).thenReturn(resultDTO);
        when(resultDTO.getTestId()).thenReturn(UUID.randomUUID());
        doNothing().when(validatorTestRunnerService).validateTestProgressDTO(progress);
        entity.Test test = TestDataBuilders.test();
        when(testDao.findByIdWithDetails(any())).thenReturn(Optional.of(test));
        doThrow(new ValidationException("fail")).when(validatorTestRunnerService).validateTest(test);
        // ACT & ASSERT
        assertThrows(ValidationException.class, () -> testRunnerService.nextQuestion(progress));
    }
}
