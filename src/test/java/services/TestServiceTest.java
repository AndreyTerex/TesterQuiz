
package services;

import dao.TestDao;
import dto.QuestionDTO;
import dto.TestDTO;
import exceptions.DataAccessException;
import exceptions.SaveException;
import exceptions.TestDeletionFailedException;
import exceptions.ValidationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import validators.ValidatorTestService;
import validators.ValidatorUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static testdata.TestDataBuilders.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TestService Tests")
public class TestServiceTest {

    @Mock
    private TestDao testDao;

    @Mock
    private ValidatorTestService validatorTestService;

    private TestService testService;

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
        testService = new TestService(testDao, validatorTestService);
    }

    @Nested
    @DisplayName("checkTestTitleAndValidate Tests")
    class CheckTestTitleAndValidateTests {

        @Test
        @DisplayName("Should not throw exception for a new test title")
        void validateNewTestTitle() {
            // ARRANGE
            TestDTO testDTO = emptyTest("New Test", "Any topic");
            when(testDao.existByTitle(testDTO.getTitle())).thenReturn(false);
            doNothing().when(validatorTestService).validate(testDTO);

            // ACT & ASSERT
            assertDoesNotThrow(() -> testService.checkTestTitleAndValidate(testDTO));
            verify(testDao).existByTitle(testDTO.getTitle());
            verify(validatorTestService).validate(testDTO);
        }

        @Test
        @DisplayName("Should throw ValidationException for an existing test title")
        void validateTestTitleExists() {
            // ARRANGE
            TestDTO testDTO = emptyTest("Existing Title", "Any topic");
            when(testDao.existByTitle(testDTO.getTitle())).thenReturn(true);
            doNothing().when(validatorTestService).validate(testDTO);

            // ACT & ASSERT
            assertThrows(ValidationException.class, () -> testService.checkTestTitleAndValidate(testDTO));
            verify(testDao).existByTitle(testDTO.getTitle());
            verify(validatorTestService).validate(testDTO);
        }
    }

    @Nested
    @DisplayName("addQuestion Tests")
    class AddQuestionTests {

        @Test
        @DisplayName("Should add a question to a test")
        void addQuestion() {
            // ARRANGE
            TestDTO testDTO = emptyTest("Any title", "Any topic");
            QuestionDTO questionDTO = questionWithAnswer(
                "This is a question text that is long enough",
                "Correct Answer"
            );
            doNothing().when(validatorTestService).validate(any(TestDTO.class));
            doNothing().when(validatorTestService).validate(any(QuestionDTO.class));

            // ACT
            TestDTO result = testService.addQuestion(testDTO, questionDTO);

            // ASSERT
            assertEquals(1, result.getQuestions().size());
            assertEquals(questionDTO.getQuestionText(), result.getQuestions().get(0).getQuestionText());
            assertEquals(1, result.getQuestions().get(0).getQuestionNumber());
            verify(validatorTestService).validate(any(TestDTO.class));
            verify(validatorTestService).validate(any(QuestionDTO.class));
        }
    }

    @Nested
    @DisplayName("deleteTest Tests")
    class DeleteTestTests {

        @Test
        @DisplayName("Should delete test successfully")
        void deleteTest() {
            // ARRANGE
            UUID testId = UUID.randomUUID();
            doNothing().when(testDao).deleteById(testId);

            // ACT
            testService.deleteTest(testId);

            // ASSERT
            verify(testDao).deleteById(testId);
        }

        @Test
        @DisplayName("Should throw TestDeletionFailedException when DAO fails")
        void deleteTestDaoError() {
            // ARRANGE
            UUID testId = UUID.randomUUID();
            doThrow(new DataAccessException("DB error")).when(testDao).deleteById(testId);

            // ACT & ASSERT
            assertThrows(TestDeletionFailedException.class, () -> testService.deleteTest(testId));
            verify(testDao).deleteById(testId);
        }
    }

    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return TestDTO when test exists")
        void findById() {
            // ARRANGE
            UUID testId = UUID.randomUUID();
            entity.Test testFromDao = testEntityFull(testId, "Found Test", "Found Topic", new ArrayList<>());
            when(testDao.findById(testId)).thenReturn(testFromDao);
            doNothing().when(validatorTestService).validateTestId(testId);

            // ACT
            TestDTO resultDto = testService.findById(testId.toString());

            // ASSERT
            assertNotNull(resultDto);
            assertEquals(testId, resultDto.getId());
            assertEquals(testFromDao.getTitle(), resultDto.getTitle());
            verify(testDao).findById(testId);
        }

        @Test
        @DisplayName("Should throw ValidationException when id does not exist")
        void findByIdNotFound() {
            // ARRANGE
            UUID testId = UUID.randomUUID();
            when(testDao.findById(testId)).thenReturn(null);
            doNothing().when(validatorTestService).validateTestId(testId);

            // ACT & ASSERT
            assertThrows(ValidationException.class, () -> testService.findById(testId.toString()));
            verify(testDao).findById(testId);
        }

        @Test
        @DisplayName("Should throw ValidationException for empty id")
        void findByIdEmpty() {
            // ARRANGE
            String emptyId = "";

            // ACT & ASSERT
            assertThrows(ValidationException.class, () -> testService.findById(emptyId));
            verifyNoInteractions(testDao);
        }
    }

    @Nested
    @DisplayName("findAllTestsDTO Tests")
    class FindAllTestsDTOTests {

        @Test
        @DisplayName("Should return list of DTOs when tests exist")
        void findAllTests() {
            // ARRANGE
            List<entity.Test> testsFromDao = List.of(
                    testEntityFull(null, "Test 1", "Topic 1", new ArrayList<>()),
                    testEntityFull(null, "Test 2", "Topic 2", new ArrayList<>())
            );
            when(testDao.findAll()).thenReturn(testsFromDao);

            // ACT
            List<TestDTO> result = testService.findAllTestsDTO();

            // ASSERT
            assertEquals(2, result.size());
            assertEquals("Test 1", result.get(0).getTitle());
            assertEquals("Test 2", result.get(1).getTitle());
            verify(testDao).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no tests exist")
        void findAllTestsEmpty() {
            // ARRANGE
            when(testDao.findAll()).thenReturn(Collections.emptyList());

            // ACT
            List<TestDTO> result = testService.findAllTestsDTO();

            // ASSERT
            assertTrue(result.isEmpty());
            verify(testDao).findAll();
        }
    }

    @Nested
    @DisplayName("updateQuestion Tests")
    class UpdateQuestionTests {

        @Test
        @DisplayName("Should update a question successfully")
        void updateQuestion() {
            // ARRANGE
            UUID testId = UUID.randomUUID();
            UUID questionId = UUID.randomUUID();

            entity.Test test = testEntityWithQuestion(testId, questionId);
            QuestionDTO updatedQuestionDTO = questionWithId(questionId, "New question text is long enough");

            when(testDao.findById(testId)).thenReturn(test);
            doNothing().when(testDao).saveUniqueTest(any(entity.Test.class));
            doNothing().when(validatorTestService).validate(updatedQuestionDTO);
            doNothing().when(validatorTestService).validateTestId(testId);

            // ACT
            TestDTO result = testService.updateQuestion(testId, updatedQuestionDTO);

            // ASSERT
            assertEquals("New question text is long enough", result.getQuestions().get(0).getQuestionText());
            assertEquals(1, result.getQuestions().get(0).getAnswers().size());
            assertEquals("Correct Answer", result.getQuestions().get(0).getAnswers().get(0).getAnswerText());
            verify(testDao).findById(testId);
            verify(testDao).saveUniqueTest(any(entity.Test.class));
            verify(validatorTestService).validate(updatedQuestionDTO);
        }

        @Test
        @DisplayName("Should throw ValidationException if question not in test")
        void updateQuestionNotFound() {
            // ARRANGE
            UUID testId = UUID.randomUUID();
            entity.Test test = testEntity(testId, "Empty Test");
            when(testDao.findById(testId)).thenReturn(test);

            QuestionDTO updatedQuestionDTO = questionWithId(
                UUID.randomUUID(), // another ID
                "Some question text that is long enough"
            );
            doNothing().when(validatorTestService).validate(updatedQuestionDTO);
            doNothing().when(validatorTestService).validateTestId(testId);

            // ACT & ASSERT
            assertThrows(ValidationException.class, () -> testService.updateQuestion(testId, updatedQuestionDTO));
            verify(testDao).findById(testId);
            verify(testDao, never()).saveUniqueTest(any(entity.Test.class));
            verify(validatorTestService).validate(updatedQuestionDTO);
        }
    }

    @Nested
    @DisplayName("saveTest Tests")
    class SaveTestTests {

        @Test
        @DisplayName("Should save a valid test")
        void saveTest() {
            // ARRANGE
            TestDTO testDTO = validTestForSave();
            doNothing().when(validatorTestService).validate(testDTO);
            doNothing().when(validatorTestService).validateQuestions(testDTO.getQuestions());
            doNothing().when(testDao).saveUniqueTest(any(entity.Test.class));

            // ACT & ASSERT
            assertDoesNotThrow(() -> testService.saveTest(testDTO));
            verify(validatorTestService).validate(testDTO);
            verify(validatorTestService).validateQuestions(testDTO.getQuestions());
            verify(testDao).saveUniqueTest(any(entity.Test.class));
        }

        @Test
        @DisplayName("Should throw ValidationException when test has no questions")
        void saveTestNoQuestions() {
            // ARRANGE
            TestDTO testDTO = testWithNoQuestions();
            doNothing().when(validatorTestService).validate(testDTO);
            doThrow(new ValidationException("Test must have at least one question.")).when(validatorTestService).validateQuestions(testDTO.getQuestions());

            // ACT & ASSERT
            assertThrows(ValidationException.class, () -> testService.saveTest(testDTO));
            verify(validatorTestService).validate(testDTO);
            verify(validatorTestService).validateQuestions(testDTO.getQuestions());
            verifyNoInteractions(testDao);
        }

        @Test
        @DisplayName("Should throw ValidationException when a question has no answers")
        void saveTestNoAnswers() {
            // ARRANGE
            TestDTO testDTO = testWithQuestionWithoutAnswers();
            doNothing().when(validatorTestService).validate(testDTO);
            doThrow(new ValidationException("Question must have answers.")).when(validatorTestService).validateQuestions(testDTO.getQuestions());

            // ACT & ASSERT
            assertThrows(ValidationException.class, () -> testService.saveTest(testDTO));
            verify(validatorTestService).validate(testDTO);
            verify(validatorTestService).validateQuestions(testDTO.getQuestions());
            verifyNoInteractions(testDao);
        }

        @Test
        @DisplayName("Should throw ValidationException when a question has no correct answer")
        void saveTestNoCorrectAnswer() {
            // ARRANGE
            TestDTO testDTO = testWithQuestionWithoutCorrectAnswers();
            doNothing().when(validatorTestService).validate(testDTO);
            doThrow(new ValidationException("Question must have at least one correct answer.")).when(validatorTestService).validateQuestions(testDTO.getQuestions());

            // ACT & ASSERT
            assertThrows(ValidationException.class, () -> testService.saveTest(testDTO));
            verify(validatorTestService).validate(testDTO);
            verify(validatorTestService).validateQuestions(testDTO.getQuestions());
            verifyNoInteractions(testDao);
        }

        @Test
        @DisplayName("Should throw SaveException when DAO throws an exception")
        void saveTestDaoError() {
            // ARRANGE
            TestDTO testDTO = validTestForSave();
            doNothing().when(validatorTestService).validate(testDTO);
            doNothing().when(validatorTestService).validateQuestions(testDTO.getQuestions());
            doThrow(new DataAccessException("DB error")).when(testDao).saveUniqueTest(any(entity.Test.class));

            // ACT & ASSERT
            assertThrows(SaveException.class, () -> testService.saveTest(testDTO));
            verify(validatorTestService).validate(testDTO);
            verify(validatorTestService).validateQuestions(testDTO.getQuestions());
            verify(testDao).saveUniqueTest(any(entity.Test.class));
        }
    }

    @Nested
    @DisplayName("updateTestDetails Tests")
    class UpdateTestDetailsTests {

        @Test
        @DisplayName("Should update test details successfully")
        void updateTestDetails() {
            // ARRANGE
            UUID testId = UUID.randomUUID();
            entity.Test test = testEntityFull(testId, "Old Title", "Old Topic", new ArrayList<>());
            when(testDao.findById(testId)).thenReturn(test);
            when(testDao.existByTitle("New Title")).thenReturn(false);
            doNothing().when(testDao).saveUniqueTest(any(entity.Test.class));
            doNothing().when(validatorTestService).validateTestId(testId);

            // ACT
            TestDTO result = testService.updateTestDetails(testId, "New Title", "New Topic");

            // ASSERT
            assertEquals("New Title", result.getTitle());
            assertEquals("New Topic", result.getTopic());
            verify(testDao).findById(testId);
            verify(testDao).existByTitle("New Title");
            verify(testDao).saveUniqueTest(argThat(savedTest ->
                    savedTest.getTitle().equals("New Title") &&
                    savedTest.getTopic().equals("New Topic")
            ));
        }

        @Test
        @DisplayName("Should throw ValidationException if new title already exists")
        void updateTestDetailsTitleExists() {
            // ARRANGE
            UUID testId = UUID.randomUUID();
            entity.Test test = testEntityFull(testId, "Old Title", "Old Topic", new ArrayList<>());
            when(testDao.findById(testId)).thenReturn(test);
            when(testDao.existByTitle("Existing Title")).thenReturn(true);
            doNothing().when(validatorTestService).validateTestId(testId);

            // ACT & ASSERT
            assertThrows(ValidationException.class, () -> testService.updateTestDetails(testId, "Existing Title", "New Topic"));
            verify(testDao).findById(testId);
            verify(testDao).existByTitle("Existing Title");
            verify(testDao, never()).saveUniqueTest(any(entity.Test.class));
        }
    }
}
