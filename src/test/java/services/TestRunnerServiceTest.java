package services;

import dao.TestDao;
import dto.*;
import entity.Answer;
import entity.Question;
import entity.Result;
import exceptions.ValidationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import validators.ValidatorTestRunnerService;
import validators.ValidatorUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static testdata.TestDataBuilders.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TestRunnerService Tests")
public class TestRunnerServiceTest {

    @Mock
    private TestDao testDao;

    @Mock
    private ResultService resultService;

    @Mock
    private ValidatorTestRunnerService validatorTestRunnerService;

    private TestRunnerService testRunnerService;

    @BeforeAll
    static void beforeAll() {
        ValidatorUtil.init();
    }

    @AfterAll
    static void afterAll() {
        ValidatorUtil.close();
    }

    @BeforeEach
    void setUp() {
        testRunnerService = new TestRunnerService(testDao, resultService,validatorTestRunnerService);
    }

    @Nested
    @DisplayName("Start Test Tests")
    class StartTestTests {

        @Test
        @DisplayName("Should start test successfully")
        void startTest() {
            // ARRANGE
            UUID testId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            UUID questionId = UUID.randomUUID();

            UserDTO userDTO = userDTO(userId, null, null);

            Answer answer = answerEntityWithId("Test Answer", true, UUID.randomUUID());
            Question question = questionEntityWithIdAndAnswers(questionId, "Test Question", 1, List.of(answer));
            entity.Test test = testEntityFull(testId, "Test Title", "Test Topic", List.of(question));

            Result result = newResult(UUID.randomUUID(), userId, testId, new ArrayList<>(), null, null);

            when(testDao.findById(testId)).thenReturn(test);
            when(resultService.buildResultObject(eq(test), eq(userId), any(LocalDateTime.class))).thenReturn(result);

            // ACT
            TestSessionDTO resultSession = testRunnerService.startTest(testId, userDTO);

            // ASSERT
            assertNotNull(resultSession);
            assertNotNull(resultSession.getCurrentQuestion());
            assertEquals(questionId, resultSession.getCurrentQuestion().getId());
            assertNotNull(resultSession.getRoundedEndTime());
            assertNotNull(resultSession.getResult());
            assertFalse(resultSession.isTestTimeOut());

            verify(testDao).findById(testId);
            verify(resultService).buildResultObject(eq(test), eq(userId), any(LocalDateTime.class));
        }

        @Test
        @DisplayName("Should throw exception when test not found")
        void startTestNotFound() {
            // ARRANGE
            UUID testId = UUID.randomUUID();
            UserDTO userDTO = userDTO(UUID.randomUUID(), null, null);

            when(testDao.findById(testId)).thenReturn(null);
            doCallRealMethod().when(validatorTestRunnerService).validateTestSessionStart(null, userDTO);

            // ACT & ASSERT
            ValidationException exception = assertThrows(ValidationException.class,
                    () -> testRunnerService.startTest(testId, userDTO));

            assertEquals("Test not found", exception.getMessage());
            verify(testDao).findById(testId);
            verifyNoInteractions(resultService);
        }

        @Test
        @DisplayName("Should throw exception when user is null")
        void startTestUserNull() {
            // ARRANGE
            UUID testId = UUID.randomUUID();
            entity.Test test = new entity.Test();

            when(testDao.findById(testId)).thenReturn(test);
            doCallRealMethod().when(validatorTestRunnerService).validateTestSessionStart(test, null);

            // ACT & ASSERT
            ValidationException exception = assertThrows(ValidationException.class,
                    () -> testRunnerService.startTest(testId, null));

            assertEquals("User not found", exception.getMessage());
            verify(testDao).findById(testId);
            verifyNoInteractions(resultService);
        }

        @Test
        @DisplayName("Should throw exception when test has no questions")
        void startTestNoQuestions() {
            // ARRANGE
            UUID testId = UUID.randomUUID();
            UserDTO userDTO = userDTO(UUID.randomUUID(), null, null);

            entity.Test test = testEntityFull(testId, "Test Title", null, Collections.emptyList());

            when(testDao.findById(testId)).thenReturn(test);
            doNothing().when(validatorTestRunnerService).validateTestSessionStart(test, userDTO);

            // ACT & ASSERT
            ValidationException exception = assertThrows(ValidationException.class,
                    () -> testRunnerService.startTest(testId, userDTO));

            assertEquals("Test Test Title has no questions", exception.getMessage());
            verify(testDao).findById(testId);
        }
    }

    @Nested
    @DisplayName("Time Validation Tests")
    class TimeValidationTests {

        @Test
        @DisplayName("Should return true when time expired")
        void checkTimeEnded() {
            // ARRANGE
            LocalDateTime pastTime = LocalDateTime.now().minusMinutes(5);
            String pastTimeString = pastTime.format(DateTimeFormatter.ISO_DATE_TIME);

            // ACT
            boolean result = testRunnerService.checkTimeIsEnded(pastTimeString);

            // ASSERT
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false when time not expired")
        void checkTimeNotEnded() {
            // ARRANGE
            LocalDateTime futureTime = LocalDateTime.now().plusMinutes(5);
            String futureTimeString = futureTime.format(DateTimeFormatter.ISO_DATE_TIME);

            // ACT
            boolean result = testRunnerService.checkTimeIsEnded(futureTimeString);

            // ASSERT
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Next Question Tests")
    class NextQuestionTests {

        @Test
        @DisplayName("Should proceed to next question")
        void nextQuestion() {
            // ARRANGE
            UUID testId = UUID.randomUUID();
            UUID answer1Id = UUID.randomUUID();

            QuestionDTO questionDTO1 = questionWithAnswerAndAnswerId("Question 1", "Answer 1", UUID.randomUUID(), 1, answer1Id);
            QuestionDTO questionDTO2 = questionWithAnswerAndAnswerId("Question 2", "Answer 1", UUID.randomUUID(), 2, answer1Id);
            TestDTO testDTO = testDTOWithIdAndQuestions("Test Title", testId, List.of(questionDTO1, questionDTO2));

            entity.Test mockTest = mock(entity.Test.class);
            when(mockTest.toDTO()).thenReturn(testDTO);

            ResultDTO resultDTO = resultDTOWithTestId(testId);

            TestProgressDTO testProgressDTO = TestProgressDTO.builder()
                    .result(resultDTO)
                    .question(questionDTO1)
                    .answers(new String[]{answer1Id.toString()})
                    .build();

            when(testDao.findById(testId)).thenReturn(mockTest);

            // ACT
            TestProgressDTO TestProgressDTOResult = testRunnerService.nextQuestion(testProgressDTO);

            // ASSERT
            assertNotNull(TestProgressDTOResult);
            assertEquals(questionDTO2.getId(), TestProgressDTOResult.getQuestion().getId());
            assertFalse(TestProgressDTOResult.isTestFinished());
            assertEquals(1, TestProgressDTOResult.getResult().getResultAnswers().size());

            ResultAnswerDTO savedAnswer = TestProgressDTOResult.getResult().getResultAnswers().get(0);
            assertEquals(questionDTO1.getId(), savedAnswer.getQuestion().getId());
            assertEquals(1, savedAnswer.getSelectedAnswers().size());
            assertEquals(answer1Id, savedAnswer.getSelectedAnswers().get(0).getId());

            verify(testDao).findById(testId);
            verify(mockTest).toDTO();
        }

        @Test
        @DisplayName("Should finish test when no more questions")
        void nextQuestionLast() {
            // ARRANGE
            UUID testId = UUID.randomUUID();
            UUID answerId = UUID.randomUUID();

            QuestionDTO questionDTO = questionWithAnswerAndAnswerId("Last Question", "Answer", UUID.randomUUID(), 1, answerId);
            TestDTO testDTO = testDTOWithIdAndQuestions("Test Title", testId, List.of(questionDTO));

            entity.Test mockTest = mock(entity.Test.class);
            when(mockTest.toDTO()).thenReturn(testDTO);

            ResultDTO resultDTO = resultDTOWithTestId(testId);

            TestProgressDTO testProgressDTO = TestProgressDTO.builder()
                    .result(resultDTO)
                    .question(questionDTO)
                    .answers(new String[]{answerId.toString()})
                    .build();

            when(testDao.findById(testId)).thenReturn(mockTest);
            when(resultService.calculateScoreResult(any(Result.class))).thenAnswer(invocation -> {
                Result res = invocation.getArgument(0);
                res.setScore(1);
                return res;
            });
            // ACT
            TestProgressDTO result = testRunnerService.nextQuestion(testProgressDTO);

            // ASSERT
            assertNotNull(result);
            assertNull(result.getQuestion());
            assertTrue(result.isTestFinished());
            assertEquals(1, result.getResult().getResultAnswers().size());
            assertEquals(1, result.getResult().getScore());

            ResultAnswerDTO savedAnswer = result.getResult().getResultAnswers().get(0);
            assertEquals(questionDTO.getId(), savedAnswer.getQuestion().getId());
            assertEquals(1, savedAnswer.getSelectedAnswers().size());
            assertEquals(answerId, savedAnswer.getSelectedAnswers().get(0).getId());

            verify(testDao).findById(testId);
            verify(mockTest).toDTO();
            verify(resultService).calculateScoreResult(any(Result.class));
            verify(resultService, never()).saveResult(any(ResultDTO.class));
        }

        @Test
        @DisplayName("Should throw exception when answers not selected")
        void nextQuestionNoAnswers() {
            // ARRANGE
            ResultDTO mockResult = mock(ResultDTO.class);
            QuestionDTO mockQuestion = mock(QuestionDTO.class);

            TestProgressDTO testProgressDTO = TestProgressDTO.builder()
                    .result(mockResult)
                    .question(mockQuestion)
                    .answers(null)
                    .build();

            doCallRealMethod().when(validatorTestRunnerService).validateTestProgressDTO(any(TestProgressDTO.class));

            // ACT & ASSERT
            ValidationException exception = assertThrows(ValidationException.class,
                    () -> testRunnerService.nextQuestion(testProgressDTO));

            assertEquals("Answers are not selected", exception.getMessage());

            verifyNoInteractions(testDao);
            verifyNoInteractions(resultService);
        }

        @Test
        @DisplayName("Should throw exception when test not found")
        void nextQuestionTestNotFound() {
            // ARRANGE
            UUID testId = UUID.randomUUID();
            ResultDTO mockResult = mock(ResultDTO.class);
            QuestionDTO mockQuestion = mock(QuestionDTO.class);

            TestProgressDTO testProgressDTO = TestProgressDTO.builder()
                    .result(mockResult)
                    .question(mockQuestion)
                    .answers(new String[]{"answer"})
                    .build();

            when(mockResult.getTestId()).thenReturn(testId);
            when(testDao.findById(testId)).thenReturn(null);

            doNothing().when(validatorTestRunnerService).validateTestProgressDTO(testProgressDTO);
            doCallRealMethod().when(validatorTestRunnerService).validateTest(null);

            // ACT & ASSERT
            ValidationException exception = assertThrows(ValidationException.class,
                    () -> testRunnerService.nextQuestion(testProgressDTO));

            assertEquals("Test not found", exception.getMessage());
            verify(testDao).findById(testId);
            verifyNoInteractions(resultService);
        }
    }
}
