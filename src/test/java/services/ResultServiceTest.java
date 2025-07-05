package services;

import dao.ResultDao;
import dto.ResultDTO;
import dto.TestDTO;
import dto.TestStatsDTO;
import entity.Answer;
import dto.QuestionDTO;
import entity.Question;
import entity.Result;
import entity.ResultAnswer;
import exceptions.DataAccessException;
import exceptions.SaveException;
import exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import mappers.ResultMapper;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import services.interfaces.ResultServiceInterface;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static testdata.TestDataBuilders.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResultService Tests")
public class ResultServiceTest {

    @Mock
    private ResultDao resultDao;

    private ResultMapper resultMapper = Mappers.getMapper(ResultMapper.class);

    private ResultServiceInterface resultService;

    @BeforeEach
    void setUp() {
        resultService = new ResultService(resultDao, resultMapper);
    }

    @Nested
    @DisplayName("buildResultObject Tests")
    class BuildResultObjectTests {

        @Test
        @DisplayName("Should build newResult object correctly")
        void buildResultObject() {
            // ARRANGE
            entity.Test test = testEntityFull(UUID.randomUUID(), "Sample Test", null, null);
            UUID userId = UUID.randomUUID();
            LocalDateTime startTime = LocalDateTime.now();

            // ACT
            Result result = resultService.buildResultObject(test, userId, startTime);

            // ASSERT
            assertNotNull(result);
            assertNotNull(result.getId());
            assertEquals(userId, result.getUserId());
            assertEquals(test.getId(), result.getTestId());
            assertEquals("Sample Test", result.getTestTitle());
            assertEquals(startTime, result.getDate());
            assertTrue(result.getResultAnswers().isEmpty());
            assertNull(result.getScore());
        }

        @Test
        @DisplayName("Should throw exception for null test")
        void buildResultObjectNullTest() {
            // ARRANGE
            UUID userId = UUID.randomUUID();
            LocalDateTime startTime = LocalDateTime.now();

            // ACT & ASSERT
            assertThrows(NullPointerException.class,
                () -> resultService.buildResultObject(null, userId, startTime));
        }
    }

    @Nested
    @DisplayName("saveResult Tests")
    class SaveResultTests {

        @Test
        @DisplayName("Should save newResult successfully")
        void saveResult() {
            // ARRANGE
            ResultDTO resultDTO = ResultDTO.builder()
                    .id(UUID.randomUUID())
                    .userId(UUID.randomUUID())
                    .testId(UUID.randomUUID())
                    .resultAnswers(new ArrayList<>())
                    .build();

            // ACT
            resultService.saveResult(resultDTO);

            // ASSERT
            verify(resultDao).save(any(Result.class));
        }

        @Test
        @DisplayName("Should throw SaveException when DAO fails")
        void saveResultDaoError() {
            // ARRANGE
            ResultDTO resultDTO = ResultDTO.builder()
                    .resultAnswers(new ArrayList<>())
                    .build();
            doThrow(new DataAccessException("DB error")).when(resultDao).save(any(Result.class));

            // ACT & ASSERT
            assertThrows(SaveException.class, () -> resultService.saveResult(resultDTO));
        }
    }

    @Nested
    @DisplayName("calculateScoreResult Tests")
    class CalculateScoreResultTests {

        @Test
        @DisplayName("Should calculate score correctly for all correct answers")
        void calculateScoreAllCorrect() {
            // ARRANGE
            Answer correctAnswer = correctAnswerEntity("");
            correctAnswer = Answer.builder()
                    .id(UUID.randomUUID())
                    .answerText(correctAnswer.getAnswerText())
                    .correct(true)
                    .build();
            Question question = Question.builder()
                    .id(UUID.randomUUID())
                    .answers(List.of(correctAnswer))
                    .build();
            
            Result result = Result.builder()
                    .resultAnswers(new ArrayList<>())
                    .build();
            result.getResultAnswers().add(new ResultAnswer(question, List.of(correctAnswer)));

            // ACT
            Result scoredResult = resultService.calculateScoreResult(result);

            // ASSERT
            assertEquals(1, scoredResult.getScore());
        }

        @Test
        @DisplayName("Should calculate zero score for incorrect answers")
        void calculateScoreAllIncorrect() {
            // ARRANGE
            Answer correctAnswer = correctAnswerEntity("");
            correctAnswer = Answer.builder()
                    .id(UUID.randomUUID())
                    .answerText(correctAnswer.getAnswerText())
                    .correct(true)
                    .build();
            Answer incorrectAnswer = Answer.builder()
                    .id(UUID.randomUUID())
                    .answerText("")
                    .correct(false)
                    .build();
            Question question = Question.builder()
                    .id(UUID.randomUUID())
                    .answers(List.of(correctAnswer, incorrectAnswer))
                    .build();
            
            Result result = Result.builder()
                    .resultAnswers(new ArrayList<>())
                    .build();
            result.getResultAnswers().add(new ResultAnswer(question, List.of(incorrectAnswer)));

            // ACT
            Result scoredResult = resultService.calculateScoreResult(result);

            // ASSERT
            assertEquals(0, scoredResult.getScore());
        }

        @Test
        @DisplayName("Should return zero for empty answers")
        void calculateScoreEmptyAnswers() {
            // ARRANGE
            Result result = Result.builder()
                    .resultAnswers(new ArrayList<>())
                    .build();

            // ACT
            Result scoredResult = resultService.calculateScoreResult(result);

            // ASSERT
            assertEquals(0, scoredResult.getScore());
        }

        @Test
        @DisplayName("Should calculate score correctly for mixed correct and incorrect answers")
        void calculateScoreMixedAnswers() {
            // ARRANGE
            Answer correctAnswer = correctAnswerEntity("Correct");
            correctAnswer = Answer.builder()
                    .id(UUID.randomUUID())
                    .answerText(correctAnswer.getAnswerText())
                    .correct(true)
                    .build();
            Answer incorrectAnswer = Answer.builder()
                    .id(UUID.randomUUID())
                    .answerText("Incorrect")
                    .correct(false)
                    .build();
            Question question1 = Question.builder()
                    .id(UUID.randomUUID())
                    .answers(List.of(correctAnswer, incorrectAnswer))
                    .build();
            Question question2 = Question.builder()
                    .id(UUID.randomUUID())
                    .answers(List.of(correctAnswer, incorrectAnswer))
                    .build();

            Result result = Result.builder()
                    .resultAnswers(new ArrayList<>())
                    .build();

            result.getResultAnswers().add(new ResultAnswer(question1, List.of(correctAnswer)));
            result.getResultAnswers().add(new ResultAnswer(question2, List.of(incorrectAnswer)));

            // ACT
            Result scoredResult = resultService.calculateScoreResult(result);

            // ASSERT
            assertEquals(1, scoredResult.getScore());
        }
    }

    @Nested
    @DisplayName("getAllResultsByUserId Tests")
    class GetAllResultsByUserIdTests {

        @Test
        @DisplayName("Should return sorted results by date")
        void getAllResultsByUserId() {
            // ARRANGE
            UUID userId = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();
            Result result1 = resultWithDateAndScore(UUID.randomUUID(), null, null, new ArrayList<>(), null, now.minusDays(1));
            Result result2 = resultWithDateAndScore(UUID.randomUUID(), null, null, new ArrayList<>(), null, now);
            
            when(resultDao.getAllResultsByUserId(userId)).thenReturn(List.of(result1, result2));

            // ACT
            List<ResultDTO> results = resultService.getAllResultsByUserId(userId);

            // ASSERT
            assertEquals(2, results.size());
            assertEquals(result2.getId(), results.get(0).getId());
            assertEquals(result1.getId(), results.get(1).getId());
        }
    }

    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return newResult when found")
        void findById() {
            // ARRANGE
            UUID resultId = UUID.randomUUID();
            Result result = newResult(resultId, null, null, new ArrayList<>(), null, null);
            when(resultDao.findById(resultId)).thenReturn(Optional.of(result));

            // ACT
            ResultDTO resultDTO = resultService.findById(resultId.toString());

            // ASSERT
            assertNotNull(resultDTO);
            assertEquals(resultId, resultDTO.getId());
        }

        @Test
        @DisplayName("Should throw exception when not found")
        void findByIdNotFound() {
            // ARRANGE
            UUID resultId = UUID.randomUUID();
            when(resultDao.findById(resultId)).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThrows(ValidationException.class,
                () -> resultService.findById(resultId.toString()));
        }

        @Test
        @DisplayName("Should throw exception for empty id")
        void findByIdEmpty() {
            // ACT & ASSERT
            assertThrows(ValidationException.class,
                () -> resultService.findById(""));
        }

        @Test
        @DisplayName("Should throw exception for null id")
        void findByIdNull() {
            // ACT & ASSERT
            assertThrows(ValidationException.class,
                () -> resultService.findById(null));
        }
    }

    @Nested
    @DisplayName("getStats Tests")
    class GetStatsTests {

        @Test
        @DisplayName("Should return correct statistics")
        void getStats() {
            // ARRANGE
            QuestionDTO question1 = QuestionDTO.builder().build();
            TestDTO test1 = testDTOWithIdAndQuestions("Test 1", UUID.randomUUID(), List.of(question1));
            TestDTO test2 = testDTOWithIdAndQuestions("Test 2", UUID.randomUUID(), Collections.emptyList());
            
            Result result1 = Result.builder().score(10).date(LocalDateTime.now()).resultAnswers(new ArrayList<>()).build();
            Result result2 = Result.builder().score(20).date(LocalDateTime.now().minusDays(1)).resultAnswers(new ArrayList<>()).build();

            when(resultDao.getAllResultsByTestId(test1.getId())).thenReturn(List.of(result1, result2));
            when(resultDao.getAllResultsByTestId(test2.getId())).thenReturn(Collections.emptyList());

            // ACT
            List<TestStatsDTO> stats = resultService.getStats(List.of(test1, test2));

            // ASSERT
            assertEquals(2, stats.size());
            
            TestStatsDTO stats1 = stats.stream().filter(s -> s.getTestTitle().equals("Test 1")).findFirst().orElseThrow();
            assertEquals(1, stats1.getTotalQuestions());
            assertEquals(2, stats1.getTotalPassed());
            assertEquals(20, stats1.getMaxScore());
            assertNotNull(stats1.getLastPassed());

            TestStatsDTO stats2 = stats.stream().filter(s -> s.getTestTitle().equals("Test 2")).findFirst().orElseThrow();
            assertEquals(0, stats2.getTotalQuestions());
            assertEquals(0, stats2.getTotalPassed());
            assertEquals(0, stats2.getMaxScore());
            assertNull(stats2.getLastPassed());
        }
    }

    @Nested
    @DisplayName("countAttempts Tests")
    class CountAttemptsTests {

        @Test
        @DisplayName("Should return attempt count")
        void countAttempts() {
            // ARRANGE
            when(resultDao.getCount()).thenReturn(5);

            // ACT
            Integer count = resultService.countAttempts();

            // ASSERT
            assertEquals(5, count);
            verify(resultDao).getCount();
        }
    }
}
