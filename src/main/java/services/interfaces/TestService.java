package services.interfaces;

import dto.AnswerDTO;
import dto.QuestionDTO;
import dto.TestDTO;
import entity.Test;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing tests, questions, and answers.
 */
public interface TestService {

    /** Validates the test DTO and checks if a test with the same title already exists. */
    void checkTestTitleAndValidate(TestDTO testDTO);

    /** Adds a question to a test DTO in memory (does not persist changes). */
    TestDTO addQuestion(TestDTO testDTO, QuestionDTO questionDTO);

    /** Deletes a test by its ID. */
    void deleteTest(UUID testId);

    /** Finds a test by its ID and returns it as a DTO. */
    TestDTO findDTOById(UUID id);

    /** Finds a test entity by its ID. */
    Test findTestById(UUID id);

    /** Returns all tests as a list of DTOs. */
    List<TestDTO> findAllTestsDTO();

    /** Updates a question and its answers within a test. */
    TestDTO updateQuestion(UUID testId, QuestionDTO questionDTO);

    /** Saves a new test to the database. */
    void saveTest(TestDTO testDTO);

    /** Updates the title and topic of an existing test. */
    TestDTO updateTestDetails(UUID testId, String newTitle, String newTopic);

    /** Checks if a list of answers for a question contains at least one correct answer. */
    void checkCorrectAnswers(List<AnswerDTO> answers);
}
