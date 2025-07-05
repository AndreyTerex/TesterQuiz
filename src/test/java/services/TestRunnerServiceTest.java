package services;

import dao.TestDao;
import dto.*;
import entity.Answer;
import entity.Question;
import entity.Result;
import exceptions.ValidationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import mappers.QuestionMapper;
import mappers.ResultMapper;
import mappers.TestMapper;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import services.interfaces.ResultServiceInterface;
import services.interfaces.TestRunnerServiceInterface;
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
    private ResultServiceInterface resultService;

    private ValidatorTestRunnerService validatorTestRunnerService;

    private TestMapper testMapper = Mappers.getMapper(TestMapper.class);
    private QuestionMapper questionMapper = Mappers.getMapper(QuestionMapper.class);
    private ResultMapper resultMapper = Mappers.getMapper(ResultMapper.class);

    private TestRunnerServiceInterface testRunnerService;

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
        validatorTestRunnerService = new ValidatorTestRunnerService();
        testRunnerService = new TestRunnerService(testDao, resultService, validatorTestRunnerService, testMapper, questionMapper, resultMapper);
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

            Question question1 = questionEntityWithIdAndAnswers(UUID.randomUUID(), "Question 1", 1, List.of(answerEntityWithId("Answer 1", true, answer1Id)));
            Question question2 = questionEntityWithIdAndAnswers(UUID.randomUUID(), "Question 2", 2, List.of(answerEntityWithId("Answer 2", true, UUID.randomUUID())));
            entity.Test test = testEntityFull(testId, "Test Title", "Test Topic", List.of(question1, question2));

            ResultDTO resultDTO = resultDTOWithTestId(testId);
            QuestionDTO questionDTO1 = questionMapper.toDTO(question1);

            TestProgressDTO testProgressDTO = TestProgressDTO.builder()
                    .result(resultDTO)
                    .question(questionDTO1)
                    .answers(new String[]{answer1Id.toString()})
                    .build();

            when(testDao.findById(testId)).thenReturn(test);

            // ACT
            TestProgressDTO result = testRunnerService.nextQuestion(testProgressDTO);

            // ASSERT
            assertNotNull(result);
            assertEquals(question2.getId(), result.getQuestion().getId());
            assertFalse(result.isTestFinished());
            assertEquals(1, result.getResult().getResultAnswers().size());

            ResultAnswerDTO savedAnswer = result.getResult().getResultAnswers().get(0);
            assertEquals(question1.getId(), savedAnswer.getQuestion().getId());
            assertEquals(1, savedAnswer.getSelectedAnswers().size());
            assertEquals(answer1Id, savedAnswer.getSelectedAnswers().get(0).getId());

            verify(testDao).findById(testId);
        }

        @Test
        @DisplayName("Should finish test when no more questions")
        void nextQuestionLast() {
            // ARRANGE
            UUID testId = UUID.randomUUID();
            UUID answerId = UUID.randomUUID();

            Question question = questionEntityWithIdAndAnswers(UUID.randomUUID(), "Last Question", 1, List.of(answerEntityWithId("Answer", true, answerId)));
            entity.Test test = testEntityFull(testId, "Test Title", "Test Topic", List.of(question));

            ResultDTO resultDTO = resultDTOWithTestId(testId);
            QuestionDTO questionDTO = questionMapper.toDTO(question);

            TestProgressDTO testProgressDTO = TestProgressDTO.builder()
                    .result(resultDTO)
                    .question(questionDTO)
                    .answers(new String[]{answerId.toString()})
                    .build();

            when(testDao.findById(testId)).thenReturn(test);
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
            assertEquals(question.getId(), savedAnswer.getQuestion().getId());
            assertEquals(1, savedAnswer.getSelectedAnswers().size());
            assertEquals(answerId, savedAnswer.getSelectedAnswers().get(0).getId());

            verify(testDao).findById(testId);
            verify(resultService).calculateScoreResult(any(Result.class));
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

            // ACT & ASSERT
            ValidationException exception = assertThrows(ValidationException.class, () -> testRunnerService.nextQuestion(testProgressDTO));

            // Check that the message from the @NotNull annotation is present
            assertTrue(exception.getMessage().contains("Answers are not selected"));

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

            // ACT & ASSERT
            ValidationException exception = assertThrows(ValidationException.class,
                    () -> testRunnerService.nextQuestion(testProgressDTO));

            assertEquals("Test not found", exception.getMessage());
            verify(testDao).findById(testId);
            verifyNoInteractions(resultService);
        }
    }
}
