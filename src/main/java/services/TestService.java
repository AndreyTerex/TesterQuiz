package services;

import dto.AnswerDTO;
import dto.QuestionDTO;
import validators.ValidatorTestService;
import dao.TestDao;
import dto.TestDTO;
import entity.Question;
import entity.Test;
import exceptions.DataAccessException;
import exceptions.SaveException;
import exceptions.TestDeletionFailedException;
import exceptions.ValidationException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TestService {
    private final TestDao testDao;
    private final ValidatorTestService validatorTestService;

    public TestService(TestDao testDao, ValidatorTestService validatorTestService) {
        this.testDao = testDao;
        this.validatorTestService = validatorTestService;
    }


    public void checkTestTitleAndValidate(TestDTO testDTO) {
        validatorTestService.validate(testDTO);
        if (testDao.existByTitle(testDTO.getTitle())) {
            throw new ValidationException("Test with title '" + testDTO.getTitle() + "' already exists.");
        }
    }


    /**
     * Adds a question to the test.
     */
    public TestDTO addQuestion(TestDTO testDTO, QuestionDTO questionDTO) {
        validatorTestService.validate(questionDTO);
        validatorTestService.validate(testDTO);
        Test currentTest = testDTO.toEntity();
        Question question = questionDTO.toEntity();
        question.setQuestionNumber(currentTest.getQuestions().size() + 1);
        currentTest.getQuestions().add(question);
        return currentTest.toDTO();
    }

    /**
     * Deletes a test by its id.
     */
    public void deleteTest(UUID testId) {
        try {
            testDao.deleteById(testId);
        } catch (DataAccessException e) {
            throw new TestDeletionFailedException("Failed to delete test with id " + testId, e);
        }
    }

    /**
     * Finds a test by its id (as string).
     */
    public TestDTO findById(String id) {
        UUID uuid = convertToUUID(id);
        return findTestByIdOrThrow(uuid).toDTO();
    }

    /**
     * Returns all tests as DTOs.
     */
    public List<TestDTO> findAllTestsDTO() {
        List<Test> tests = testDao.findAll();
        if (tests == null) {
            return Collections.emptyList();
        }
        return tests.stream()
                .map(Test::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates a question in the test.
     */
    public TestDTO updateQuestion(UUID testId, QuestionDTO questionDTO) {
        validatorTestService.validate(questionDTO);
        Test test = findTestByIdOrThrow(testId);
        Optional<Question> questionToUpdate = test.getQuestions().stream()
                .filter(q -> q.getId().equals(questionDTO.getId()))
                .findFirst();


        Question question = questionToUpdate.orElseThrow(() -> new ValidationException("Question does not belong to this test."));
        question.setQuestionText(questionDTO.getQuestionText());
        question.setAnswers(questionDTO.getAnswers().stream().map(AnswerDTO::toEntity).collect(Collectors.toList()));
        try {
            testDao.saveUniqueTest(test);
        } catch (DataAccessException e) {
            throw new SaveException("Failed to save test.", e);
        }
        return test.toDTO();
    }

    /**
     * Saves a test (validates and persists).
     */
    public void saveTest(TestDTO testDTO) {
        validatorTestService.validate(testDTO);
        validatorTestService.validateQuestions(testDTO.getQuestions());
        Test test = testDTO.toEntity();

        try {
            testDao.saveUniqueTest(test);
        } catch (DataAccessException e) {
            throw new SaveException("Failed to save test.", e);
        }
    }

    /**
     * Updates test title and topic.
     */
    public TestDTO updateTestDetails(UUID testId, String newTitle, String newTopic) {
        Test test = findTestByIdOrThrow(testId);

        if (newTitle != null && !newTitle.isBlank() && !test.getTitle().equals(newTitle)) {
            if (testDao.existByTitle(newTitle)) {
                throw new ValidationException("Another test with this title already exists.");
            }
            test.setTitle(newTitle);
        }

        if (newTopic != null && !newTopic.isBlank()) {
            test.setTopic(newTopic);
        }
        TestDTO dto = test.toDTO();
        validatorTestService.validate(dto);

        try {
            testDao.saveUniqueTest(test);
        } catch (DataAccessException e) {
            throw new SaveException("Failed to save test.", e);
        }
        return dto;
    }

    public void ValidateCorrectAnswers(List<AnswerDTO> answers) {
        boolean questionHaveCorrectAnswer = answers.stream().anyMatch(AnswerDTO::isCorrect);
        if (!questionHaveCorrectAnswer) {
            throw new ValidationException("Question must have at least one correct answer.");
        }
    }

    private Test findTestByIdOrThrow(UUID testId) {
        validatorTestService.validateTestId(testId);
        Test test = testDao.findById(testId);
        if (test == null) {
            throw new ValidationException("Test not found");
        }
        return test;
    }

    private UUID convertToUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid UUID format");
        }
    }
}
