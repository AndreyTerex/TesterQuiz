package services;

import dao.ResultDAO;
import dto.*;
import entity.User;
import entity.Answer;
import entity.Question;
import entity.AnswersInResult;
import entity.Result;
import exceptions.DataAccessException;
import exceptions.SaveException;
import exceptions.ValidationException;
import mappers.ResultMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import services.interfaces.TestService;
import services.interfaces.UserService;
import validators.ValidatorResultService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResultServiceImplTest {
    @Mock
    private ResultDAO resultDao;
    @Mock
    private ResultMapper resultMapper;
    @Mock
    private UserService userService;
    @Mock
    private TestService testService;


    @InjectMocks
    private ResultServiceImpl resultService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ValidatorResultService validatorResultService = new ValidatorResultService();
        resultService = new ResultServiceImpl(resultDao, resultMapper, testService, userService, validatorResultService);
    }

    @Test
    @DisplayName("buildStartResultObject creates Result with correct fields")
    void buildStartResultObject_createsResultWithCorrectFields() {
        // ARRANGE
        User user = TestDataBuilders.user();
        entity.Test test = TestDataBuilders.test();
        LocalDateTime now = LocalDateTime.now();

        // ACT
        Result result = resultService.buildStartResultObject(test, user, now);

        // ASSERT
        assertEquals(user, result.getUser());
        assertEquals(test, result.getTest());
        assertEquals(now, result.getDate());
        assertEquals("Sample Test", result.getTestTitle());
        assertNotNull(result.getAnswersInResults());
        assertTrue(result.getAnswersInResults().isEmpty());
    }

    @Test
    @DisplayName("buildAndSaveFinalResult saves result successfully")
    void buildAndSaveFinalResult_savesResult() {
        // ARRANGE
        ResultDTO resultDTO = mock(ResultDTO.class);
        Result result = mock(Result.class);
        entity.Test test = TestDataBuilders.test();
        Question question = TestDataBuilders.question(new ArrayList<>(), 1);
        test.setQuestions(List.of(question));
        when(userService.findUserById(any())).thenReturn(TestDataBuilders.user());
        when(testService.findTestById(any())).thenReturn(test);
        when(resultMapper.toEntity(resultDTO)).thenReturn(result);
        when(result.getTest()).thenReturn(test);
        when(result.getAnswersInResults()).thenReturn(new ArrayList<>());

        // ACT
        resultService.buildAndSaveFinalResult(resultDTO);

        // ASSERT
        verify(resultDao, times(1)).save(any(Result.class));
    }

    @Test
    @DisplayName("buildAndSaveFinalResult throws SaveException on DataAccessException")
    void buildAndSaveFinalResult_throwsSaveExceptionOnDataAccess() {
        // ARRANGE
        ResultDTO resultDTO = mock(ResultDTO.class);
        Result result = mock(Result.class);
        entity.Test test = TestDataBuilders.test();
        Question question = TestDataBuilders.question(new ArrayList<>(), 1);
        test.setQuestions(List.of(question));
        when(userService.findUserById(any())).thenReturn(TestDataBuilders.user());
        when(testService.findTestById(any())).thenReturn(test);
        when(resultMapper.toEntity(resultDTO)).thenReturn(result);
        when(result.getTest()).thenReturn(test);
        when(result.getAnswersInResults()).thenReturn(new ArrayList<>());
        doThrow(new DataAccessException("fail")).when(resultDao).save(any());

        // ACT & ASSERT
        assertThrows(SaveException.class, () -> resultService.buildAndSaveFinalResult(resultDTO));
    }

    @Test
    @DisplayName("calculateScoreResult counts correct answers")
    void calculateScoreResult_countsCorrectAnswers() {
        // ARRANGE
        Answer correct = TestDataBuilders.answer(true);
        Answer wrong = TestDataBuilders.answer(false);
        Question question = TestDataBuilders.question(List.of(correct, wrong), 1);
        AnswersInResult air = TestDataBuilders.answersInResult(question, List.of(correct));
        Result result = TestDataBuilders.result(List.of(air));

        // ACT
        Result scored = resultService.calculateScoreResult(result);

        // ASSERT
        assertEquals(1, scored.getScore());
    }

    @Test
    @DisplayName("getAllResultsByUserId returns sorted ResultDTOs by date")
    void getAllResultsByUserId_returnsSortedDTOs() {
        // ARRANGE
        UUID userId = UUID.randomUUID();
        Result result1 = TestDataBuilders.result(new ArrayList<>());
        result1.setDate(LocalDateTime.now().minusDays(1));
        Result result2 = TestDataBuilders.result(new ArrayList<>());
        result2.setDate(LocalDateTime.now());
        when(resultDao.getAllResultsByUserId(userId)).thenReturn(List.of(result1, result2));
        ResultDTO dto1 = mock(ResultDTO.class);
        ResultDTO dto2 = mock(ResultDTO.class);
        when(resultMapper.toDTO(result1)).thenReturn(dto1);
        when(resultMapper.toDTO(result2)).thenReturn(dto2);
        when(dto1.getDate()).thenReturn(result1.getDate());
        when(dto2.getDate()).thenReturn(result2.getDate());

        // ACT
        List<ResultDTO> result = resultService.getAllResultsByUserId(userId);

        // ASSERT
        assertEquals(List.of(dto2, dto1), result);
    }

    @Test
    @DisplayName("getStats returns statistics list for tests")
    void getStats_returnsStatsList() {
        // ARRANGE
        Result result = TestDataBuilders.result(new ArrayList<>());
        result.setTestTitle("Test1");
        result.setScore(5);
        result.setDate(LocalDateTime.now());
        Question q = TestDataBuilders.question(new ArrayList<>(), 1);
        AnswersInResult air = TestDataBuilders.answersInResult(q, new ArrayList<>());
        result.setAnswersInResults(List.of(air));
        when(resultDao.findAll()).thenReturn(List.of(result));

        // ACT
        List<TestStatsDTO> stats = resultService.getStats();

        // ASSERT
        assertEquals(1, stats.size());
        assertEquals("Test1", stats.get(0).getTestTitle());
    }

    @Test
    @DisplayName("countAttempts returns correct count from DAO")
    void countAttempts_returnsCount() {
        // ARRANGE
        when(resultDao.getCount()).thenReturn(42L);

        // ACT & ASSERT
        assertEquals(42L, resultService.countAttempts());
    }

    @Test
    @DisplayName("findByIdWithDetails returns ResultDTO if found")
    void findByIdWithDetails_returnsDTO() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        Result result = TestDataBuilders.result(new ArrayList<>());
        ResultDTO dto = mock(ResultDTO.class);
        when(resultDao.findByIdWithDetails(id)).thenReturn(Optional.of(result));
        when(resultMapper.toDTO(result)).thenReturn(dto);

        // ACT & ASSERT
        assertEquals(dto, resultService.findByIdWithDetails(id));
    }

    @Test
    @DisplayName("findByIdWithDetails throws ValidationException if not found")
    void findByIdWithDetails_throwsIfNotFound() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        when(resultDao.findByIdWithDetails(id)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(ValidationException.class, () -> resultService.findByIdWithDetails(id));
    }
    @Test
    @DisplayName("buildAndSaveFinalResult throws ValidationException if user not found")
    void buildAndSaveFinalResult_throwsValidationExceptionIfUserNotFound() {
        // ARRANGE
        ResultDTO resultDTO = mock(ResultDTO.class);
        when(userService.findUserById(any())).thenReturn(null);
        // ACT & ASSERT
        assertThrows(ValidationException.class, () -> resultService.buildAndSaveFinalResult(resultDTO));
    }

    @Test
    @DisplayName("buildAndSaveFinalResult throws ValidationException if test not found")
    void buildAndSaveFinalResult_throwsValidationExceptionIfTestNotFound() {
        // ARRANGE
        ResultDTO resultDTO = mock(ResultDTO.class);
        when(userService.findUserById(any())).thenReturn(TestDataBuilders.user());
        when(testService.findTestById(any())).thenReturn(null);
        // ACT & ASSERT
        assertThrows(ValidationException.class, () -> resultService.buildAndSaveFinalResult(resultDTO));
    }

    @Test
    @DisplayName("buildAndSaveFinalResult throws ValidationException if result mapping failed")
    void buildAndSaveFinalResult_throwsValidationExceptionIfResultMappingFailed() {
        // ARRANGE
        ResultDTO resultDTO = mock(ResultDTO.class);
        when(userService.findUserById(any())).thenReturn(TestDataBuilders.user());
        when(testService.findTestById(any())).thenReturn(TestDataBuilders.test());
        when(resultMapper.toEntity(resultDTO)).thenReturn(null);
        // ACT & ASSERT
        assertThrows(ValidationException.class, () -> resultService.buildAndSaveFinalResult(resultDTO));
    }

    @Test
    @DisplayName("buildAndSaveFinalResult throws SaveException if question not found")
    void buildAndSaveFinalResult_throwsSaveExceptionIfQuestionNotFound() {
        // ARRANGE
        ResultDTO resultDTO = mock(ResultDTO.class);
        when(userService.findUserById(any())).thenReturn(TestDataBuilders.user());
        entity.Test test = TestDataBuilders.test();
        test.setQuestions(new ArrayList<>());
        when(testService.findTestById(any())).thenReturn(test);
        Result result = mock(Result.class);
        when(resultMapper.toEntity(resultDTO)).thenReturn(result);
        when(result.getTest()).thenReturn(test);
        AnswersInResultDTO airDto = AnswersInResultDTO.builder()
                .question(QuestionDTO.builder().id(UUID.randomUUID()).build())
                .selectedAnswers(new ArrayList<>())
                .build();
        when(resultDTO.getAnswersInResults()).thenReturn(List.of(airDto));
        when(resultDTO.getUserId()).thenReturn(UUID.randomUUID());
        when(resultDTO.getTestId()).thenReturn(UUID.randomUUID());
        // ACT & ASSERT
        assertThrows(SaveException.class, () -> resultService.buildAndSaveFinalResult(resultDTO));
    }

    @Test
    @DisplayName("buildAndSaveFinalResult throws SaveException if answer not found")
    void buildAndSaveFinalResult_throwsSaveExceptionIfAnswerNotFound() {
        // ARRANGE
        ResultDTO resultDTO = mock(ResultDTO.class);
        when(userService.findUserById(any())).thenReturn(TestDataBuilders.user());
        entity.Test test = TestDataBuilders.test();
        // Вопрос с пустым списком ответов
        Question question = TestDataBuilders.question(new ArrayList<>(), 1);
        UUID qId = question.getId();
        test.setQuestions(List.of(question));
        when(testService.findTestById(any())).thenReturn(test);
        Result result = mock(Result.class);
        when(resultMapper.toEntity(resultDTO)).thenReturn(result);
        when(result.getTest()).thenReturn(test);
        UUID missingAnswerId = UUID.randomUUID();
        AnswerDTO answerDTO = AnswerDTO.builder().id(missingAnswerId).build();
        AnswersInResultDTO airDto = AnswersInResultDTO.builder()
                .question(QuestionDTO.builder().id(qId).build())
                .selectedAnswers(List.of(answerDTO))
                .build();
        when(resultDTO.getAnswersInResults()).thenReturn(List.of(airDto));
        when(resultDTO.getUserId()).thenReturn(UUID.randomUUID());
        when(resultDTO.getTestId()).thenReturn(UUID.randomUUID());
        // ACT & ASSERT
        assertThrows(SaveException.class, () -> resultService.buildAndSaveFinalResult(resultDTO));
    }

}
