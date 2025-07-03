package services;

import dto.AnswerDTO;
import dto.QuestionDTO;
import validator.ValidatorUtil;
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

    public TestService(TestDao testDao) {
        this.testDao = testDao;
    }


    public void checkTestTitleAndValidate(TestDTO testDTO) {
        ValidatorUtil.validate(testDTO);
        if (testDao.existByTitle(testDTO.getTitle())) {
            throw new ValidationException("Test with title '" + testDTO.getTitle() + "' already exists.");
        }
    }


    /**
     * Adds a question to the test.
     */
    public TestDTO addQuestion(TestDTO testDTO, QuestionDTO questionDTO) {
        ValidatorUtil.validate(questionDTO);
        ValidatorUtil.validate(testDTO);
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
        } catch (DataAccessException e){
            throw new TestDeletionFailedException("Failed to delete test with id " + testId, e);
        }
    }

    /**
     * Finds a test by its id (as string).
     */
    public TestDTO findById(String id) {
        if(id == null || id.isEmpty()){
            throw new ValidationException("Test id is null or empty");
        }
        TestDTO testDTO = findTestByIdOrThrow(UUID.fromString(id)).toDTO();
        if (testDTO == null) {
            throw new ValidationException("Test with id= + "+ id + " not found");
        }
        return testDTO;
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
        ValidatorUtil.validate(questionDTO);
        Test test = findTestByIdOrThrow(testId);
        Optional<Question> questionToUpdate = test.getQuestions().stream()
                .filter(q -> q.getId().equals(questionDTO.getId()))
                .findFirst();

        if (questionToUpdate.isPresent()) {
            Question q = questionToUpdate.get();
            q.setQuestionText(questionDTO.getQuestionText());
            q.setAnswers(questionDTO.getAnswers().stream().map(AnswerDTO::toEntity).collect(Collectors.toList()));
            try {
                testDao.saveUniqueTest(test);
            } catch (DataAccessException e) {
                throw new SaveException("Failed to save test.", e);
            }
            return test.toDTO();
        } else {
            throw new ValidationException("Question does not belong to this test.");
        }
    }

    /**
     * Saves a test (validates and persists).
     */
    public void saveTest(TestDTO testDTO) {
        ValidatorUtil.validate(testDTO);
        if (testDTO.getQuestions() == null || testDTO.getQuestions().isEmpty()) {
            throw new ValidationException("Test must have at least one question.");
        }
        for (QuestionDTO question : testDTO.getQuestions()) {
            if (question.getAnswers() == null || question.getAnswers().isEmpty()) {
                throw new ValidationException("Question '" + question.getQuestionText() + "' must have at least one answer.");
            }
            boolean hasCorrectAnswer = question.getAnswers().stream().anyMatch(AnswerDTO::isCorrect);
            if (!hasCorrectAnswer) {
                throw new ValidationException("Question '" + question.getQuestionText() + "' must have at least one correct answer.");
            }
        }
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

        try {
            testDao.saveUniqueTest(test);
        } catch (DataAccessException e) {
            throw new SaveException("Failed to save test.", e);
        }
        ValidatorUtil.validate(test.toDTO());
        return test.toDTO();
    }

    private Test findTestByIdOrThrow(UUID testId) {
        Test test = testDao.findById(testId);
        if (test == null) {
            throw new ValidationException("Test not found");
        }
        return test;
    }
}
