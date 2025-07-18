package services;

import dao.TestDAO;
import dto.*;
import entity.User;
import entity.Question;
import exceptions.DataAccessException;
import exceptions.SaveException;
import exceptions.TestDeletionFailedException;
import exceptions.ValidationException;
import mappers.AnswerMapper;
import mappers.TestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import services.interfaces.UserService;
import validators.ValidatorTestService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestServiceImplTest {
    @Mock
    private TestDAO testDao;
    @Mock
    private UserService userService;
    @Mock
    private ValidatorTestService validatorTestService;
    @Mock
    private TestMapper testMapper;
    @Mock
    private AnswerMapper answerMapper;

    @InjectMocks
    private TestServiceImpl testService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testService = new TestServiceImpl(testDao, userService, validatorTestService, testMapper, answerMapper);
    }

    @Test
    @DisplayName("checkTestTitleAndValidate throws ValidationException if title exists")
    void checkTestTitleAndValidate_throwsIfTitleExists() {
        // ARRANGE
        TestDTO testDTO = TestDataBuilders.testDTO(
                UUID.randomUUID(),
                "title",
                "topic",
                UUID.randomUUID(),
                new ArrayList<>()
        );
        doNothing().when(validatorTestService).validate(testDTO);
        when(testDao.existByTitle("title")).thenReturn(true);
        // ACT & ASSERT
        assertThrows(ValidationException.class, () -> testService.checkTestTitleAndValidate(testDTO));
    }

    @Test
    @DisplayName("addQuestion adds first question with number 1")
    void addQuestion_addsQuestionWithNumber() {
        // ARRANGE
        QuestionDTO questionDTO = TestDataBuilders.questionDTO(UUID.randomUUID(), 0).toBuilder().questionText("Q?").build();
        TestDTO testDTO = TestDTO.builder().questions(new ArrayList<>()).build();
        doNothing().when(validatorTestService).validate(questionDTO);
        doNothing().when(validatorTestService).validate(testDTO);
        // ACT
        TestDTO result = testService.addQuestion(testDTO, questionDTO);
        // ASSERT
        assertEquals(1, result.getQuestions().size());
        assertEquals(1, result.getQuestions().get(0).getQuestionNumber());
    }

    @Test
    @DisplayName("deleteTest throws TestDeletionFailedException on DataAccessException")
    void deleteTest_throwsOnDataAccess() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        doNothing().when(validatorTestService).validateTestId(id);
        doThrow(new DataAccessException("fail")).when(testDao).deleteById(id);
        // ACT & ASSERT
        assertThrows(TestDeletionFailedException.class, () -> testService.deleteTest(id));
    }

    @Test
    @DisplayName("findDTOById returns TestDTO if found")
    void findDTOById_returnsDTO() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        entity.Test test = TestDataBuilders.test();
        TestDTO dto = TestDTO.builder().id(id).build();
        doNothing().when(validatorTestService).validateTestId(id);
        when(testDao.findByIdWithDetails(id)).thenReturn(Optional.of(test));
        when(testMapper.toDTO(test)).thenReturn(dto);
        // ACT
        TestDTO result = testService.findDTOById(id);
        // ASSERT
        assertEquals(dto, result);
    }

    @Test
    @DisplayName("findTestById returns Test entity if found")
    void findTestById_returnsEntity() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        entity.Test test = TestDataBuilders.test();
        doNothing().when(validatorTestService).validateTestId(id);
        when(testDao.findByIdWithDetails(id)).thenReturn(Optional.of(test));
        // ACT
        entity.Test result = testService.findTestById(id);
        // ASSERT
        assertEquals(test, result);
    }

    @Test
    @DisplayName("findAllTestsDTO returns list of TestDTOs")
    void findAllTestsDTO_returnsList() {
        // ARRANGE
        entity.Test test = TestDataBuilders.test();
        TestDTO dto = TestDTO.builder().id(test.getId()).build();
        when(testDao.findAll()).thenReturn(List.of(test));
        when(testMapper.toDTO(test)).thenReturn(dto);
        // ACT
        List<TestDTO> result = testService.findAllTestsDTO();
        // ASSERT
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }


    @Test
    @DisplayName("checkTestTitleAndValidate does not throw if title does not exist")
    void checkTestTitleAndValidate_okIfTitleNotExists() {
        // ARRANGE
        TestDTO testDTO = TestDataBuilders.testDTO(
                UUID.randomUUID(), "title", "topic", UUID.randomUUID(), new ArrayList<>());
        doNothing().when(validatorTestService).validate(testDTO);
        when(testDao.existByTitle("title")).thenReturn(false);
        // ACT & ASSERT
        assertDoesNotThrow(() -> testService.checkTestTitleAndValidate(testDTO));
    }

    @Test
    @DisplayName("addQuestion adds question with correct number when questions already exist")
    void addQuestion_addsQuestionWithExistingQuestions() {
        // ARRANGE
        QuestionDTO q1 = TestDataBuilders.questionDTO(UUID.randomUUID(), 1).toBuilder().questionText("Q1?").build();
        List<QuestionDTO> questions = new ArrayList<>(List.of(q1));
        TestDTO testDTO = TestDTO.builder().questions(questions).build();
        QuestionDTO newQ = TestDataBuilders.questionDTO(UUID.randomUUID(), 0).toBuilder().questionText("Q2?").build();
        doNothing().when(validatorTestService).validate(any(TestDTO.class));
        doNothing().when(validatorTestService).validate(any(QuestionDTO.class));
        // ACT
        TestDTO result = testService.addQuestion(testDTO, newQ);
        // ASSERT
        assertEquals(2, result.getQuestions().size());
        assertEquals(2, result.getQuestions().get(1).getQuestionNumber());
    }

    @Test
    @DisplayName("deleteTest does not throw on successful delete")
    void deleteTest_success() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        doNothing().when(validatorTestService).validateTestId(id);
        doNothing().when(testDao).deleteById(id);
        // ACT & ASSERT
        assertDoesNotThrow(() -> testService.deleteTest(id));
    }

    @Test
    @DisplayName("findTestById throws ValidationException if test not found")
    void findTestById_throwsIfNotFound() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        doNothing().when(validatorTestService).validateTestId(id);
        when(testDao.findByIdWithDetails(id)).thenReturn(Optional.empty());
        // ACT & ASSERT
        assertThrows(ValidationException.class, () -> testService.findTestById(id));
    }

    @Test
    @DisplayName("findAllTestsDTO returns empty list if DAO returns null")
    void findAllTestsDTO_returnsEmptyListIfNull() {
        // ARRANGE
        when(testDao.findAll()).thenReturn(null);
        // ACT
        List<TestDTO> result = testService.findAllTestsDTO();
        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("saveTest does not throw on valid input and DAO update")
    void saveTest_success() {
        // ARRANGE
        TestDTO testDTO = TestDataBuilders.testDTO(
                UUID.randomUUID(), "title", "topic", UUID.randomUUID(), new ArrayList<>());
        entity.Test testEntity = TestDataBuilders.test();
        User user = TestDataBuilders.user();
        doNothing().when(validatorTestService).validate(testDTO);
        doNothing().when(validatorTestService).validateQuestions(any());
        when(testMapper.toEntity(testDTO)).thenReturn(testEntity);
        when(userService.findUserById(any())).thenReturn(user);
        when(validatorTestService.requireNonNullOrValidation(eq(testEntity), anyString())).thenReturn(testEntity);
        when(validatorTestService.requireNonNullOrValidation(eq(user), anyString())).thenReturn(user);
        doNothing().when(testDao).update(any());
        // ACT & ASSERT
        assertDoesNotThrow(() -> testService.saveTest(testDTO));
    }

    @Test
    @DisplayName("saveTest throws ValidationException if test mapping failed")
    void saveTest_throwsIfTestMappingFailed() {
        // ARRANGE
        TestDTO testDTO = TestDataBuilders.testDTO(
                UUID.randomUUID(), "title", "topic", UUID.randomUUID(), new ArrayList<>());
        doNothing().when(validatorTestService).validate(testDTO);
        doNothing().when(validatorTestService).validateQuestions(any());
        when(testMapper.toEntity(testDTO)).thenReturn(null);
        when(validatorTestService.requireNonNullOrValidation(isNull(), anyString()))
                .thenThrow(new ValidationException("Test mapping failed"));
        // ACT & ASSERT
        assertThrows(ValidationException.class, () -> testService.saveTest(testDTO));
    }

    @Test
    @DisplayName("saveTest throws ValidationException if user not found")
    void saveTest_throwsIfUserNotFound() {
        // ARRANGE
        TestDTO testDTO = TestDataBuilders.testDTO(
                UUID.randomUUID(), "title", "topic", UUID.randomUUID(), new ArrayList<>());
        entity.Test testEntity = TestDataBuilders.test();
        doNothing().when(validatorTestService).validate(testDTO);
        doNothing().when(validatorTestService).validateQuestions(any());
        when(testMapper.toEntity(testDTO)).thenReturn(testEntity);
        when(userService.findUserById(any())).thenReturn(null);
        when(validatorTestService.requireNonNullOrValidation(eq(testEntity), anyString())).thenReturn(testEntity);
        when(validatorTestService.requireNonNullOrValidation(isNull(), anyString()))
                .thenThrow(new ValidationException("User not found"));
        // ACT & ASSERT
        assertThrows(ValidationException.class, () -> testService.saveTest(testDTO));
    }

    @Test
    @DisplayName("saveTest throws SaveException on DataAccessException")
    void saveTest_throwsOnDataAccess() {
        // ARRANGE
        TestDTO testDTO = TestDataBuilders.testDTO(
                UUID.randomUUID(), "title", "topic", UUID.randomUUID(), new ArrayList<>());
        entity.Test testEntity = TestDataBuilders.test();
        User user = TestDataBuilders.user();
        doNothing().when(validatorTestService).validate(testDTO);
        doNothing().when(validatorTestService).validateQuestions(any());
        when(testMapper.toEntity(testDTO)).thenReturn(testEntity);
        when(userService.findUserById(any())).thenReturn(user);
        when(validatorTestService.requireNonNullOrValidation(eq(testEntity), anyString())).thenReturn(testEntity);
        when(validatorTestService.requireNonNullOrValidation(eq(user), anyString())).thenReturn(user);
        doThrow(new DataAccessException("fail")).when(testDao).update(any());
        // ACT & ASSERT
        assertThrows(SaveException.class, () -> testService.saveTest(testDTO));
    }

    @Test
    @DisplayName("updateTestDetails updates test details successfully")
    void updateTestDetails_success() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        entity.Test test = TestDataBuilders.test();
        test.setTitle("old");
        test.setTopic("oldTopic");
        TestDTO dto = TestDataBuilders.testDTO(id, "new", "newTopic", UUID.randomUUID(), new ArrayList<>());
        doNothing().when(validatorTestService).validateDetails(any(), any());
        doNothing().when(validatorTestService).validateTestId(id);
        when(testDao.findByIdWithDetails(id)).thenReturn(Optional.of(test));
        when(testDao.existByTitle("new")).thenReturn(false);
        doNothing().when(testDao).update(test);
        when(testMapper.toDTO(any())).thenReturn(dto);
        // ACT
        TestDTO result = testService.updateTestDetails(id, "new", "newTopic");
        // ASSERT
        assertEquals(dto, result);
    }

    @Test
    @DisplayName("updateTestDetails throws ValidationException if duplicate title")
    void updateTestDetails_throwsIfDuplicateTitle() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        entity.Test test = TestDataBuilders.test();
        test.setTitle("old");
        doNothing().when(validatorTestService).validateDetails(any(), any());
        doNothing().when(validatorTestService).validateTestId(id);
        when(testDao.findByIdWithDetails(id)).thenReturn(Optional.of(test));
        when(testDao.existByTitle("new")).thenReturn(true);
        // ACT & ASSERT
        assertThrows(ValidationException.class, () -> testService.updateTestDetails(id, "new", "topic"));
    }

    @Test
    @DisplayName("updateTestDetails throws SaveException on DataAccessException")
    void updateTestDetails_throwsOnDataAccess() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        entity.Test test = TestDataBuilders.test();
        test.setTitle("old");
        doNothing().when(validatorTestService).validateDetails(any(), any());
        doNothing().when(validatorTestService).validateTestId(id);
        when(testDao.findByIdWithDetails(id)).thenReturn(Optional.of(test));
        when(testDao.existByTitle("new")).thenReturn(false);
        doThrow(new DataAccessException("fail")).when(testDao).update(test);
        // ACT & ASSERT
        assertThrows(SaveException.class, () -> testService.updateTestDetails(id, "new", "topic"));
    }

    @Test
    @DisplayName("updateQuestion updates question successfully")
    void updateQuestion_success() {
        // ARRANGE
        UUID testId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        AnswerDTO answerDTO = TestDataBuilders.answerDTO(UUID.randomUUID()).toBuilder().correct(true).build();
        QuestionDTO questionDTO = TestDataBuilders.questionDTO(questionId, 1).toBuilder()
                .questionText("Q?").answers(List.of(answerDTO)).build();
        entity.Test test = TestDataBuilders.test();
        Question question = TestDataBuilders.question(new ArrayList<>(), 1);
        question.setId(questionId);
        test.setQuestions(List.of(question));
        doNothing().when(validatorTestService).validate(questionDTO);
        doNothing().when(validatorTestService).validateTestId(testId);
        when(testDao.findByIdWithDetails(testId)).thenReturn(Optional.of(test));
        when(answerMapper.toEntity(answerDTO)).thenReturn(TestDataBuilders.answer(true));
        doNothing().when(testDao).update(test);
        when(testMapper.toDTO(any())).thenReturn(TestDataBuilders.testDTO(testId, "t", "topic", UUID.randomUUID(), new ArrayList<>()));
        // ACT
        TestDTO result = testService.updateQuestion(testId, questionDTO);
        // ASSERT
        assertNotNull(result);
    }

    @Test
    @DisplayName("updateQuestion throws ValidationException if question not found")
    void updateQuestion_throwsIfQuestionNotFound() {
        // ARRANGE
        UUID testId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        QuestionDTO questionDTO = TestDataBuilders.questionDTO(questionId, 1).toBuilder().questionText("Q?").build();
        entity.Test test = TestDataBuilders.test();
        test.setQuestions(new ArrayList<>()); // no questions
        doNothing().when(validatorTestService).validate(questionDTO);
        doNothing().when(validatorTestService).validateTestId(testId);
        when(testDao.findByIdWithDetails(testId)).thenReturn(Optional.of(test));
        // ACT & ASSERT
        assertThrows(ValidationException.class, () -> testService.updateQuestion(testId, questionDTO));
    }

    @Test
    @DisplayName("updateQuestion throws SaveException on DataAccessException")
    void updateQuestion_throwsOnDataAccess() {
        // ARRANGE
        UUID testId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        AnswerDTO answerDTO = TestDataBuilders.answerDTO(UUID.randomUUID()).toBuilder().correct(true).build();
        QuestionDTO questionDTO = TestDataBuilders.questionDTO(questionId, 1).toBuilder()
                .questionText("Q?").answers(List.of(answerDTO)).build();
        entity.Test test = TestDataBuilders.test();
        Question question = TestDataBuilders.question(new ArrayList<>(), 1);
        question.setId(questionId);
        test.setQuestions(List.of(question));
        doNothing().when(validatorTestService).validate(questionDTO);
        doNothing().when(validatorTestService).validateTestId(testId);
        when(testDao.findByIdWithDetails(testId)).thenReturn(Optional.of(test));
        when(answerMapper.toEntity(answerDTO)).thenReturn(TestDataBuilders.answer(true));
        doThrow(new DataAccessException("fail")).when(testDao).update(test);
        // ACT & ASSERT
        assertThrows(SaveException.class, () -> testService.updateQuestion(testId, questionDTO));
    }
}
