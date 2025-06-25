package Services;

import Listener.ValidatorUtil;
import dao.ITestDao;
import dto.AnswerDTO;
import dto.QuestionDTO;
import dto.TestDTO;
import entity.Question;
import entity.Test;
import exceptions.DataAccessException;
import exceptions.SaveException;
import exceptions.TestDeletionFailedException;
import exceptions.ValidationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TestService {
    private final ITestDao testDao;

    public TestService(ITestDao testDao) {
        this.testDao = testDao;
    }

    /**
     * Создает новый тест в системе
     */
    public void checkTestTitleAndValidate(TestDTO testDTO) {
        ValidatorUtil.validate(testDTO);
        if (testDao.existByTitle(testDTO.getTitle())) {
            throw new ValidationException("Test with title '" + testDTO.getTitle() + "' already exists.");
        }
    }

    /**
     * Добавляет новый вопрос к существующему тесту
     */
    public TestDTO addQuestion(TestDTO testDTO, QuestionDTO questionDTO) {
        ValidatorUtil.validate(questionDTO);
        ValidatorUtil.validate(testDTO);
        Test currentTest = testDTO.toEntity();
        Question question = questionDTO.toEntity();
        question.setQuestion_number(currentTest.getQuestions().size() + 1);
        currentTest.getQuestions().add(question);
        return currentTest.toDTO();
    }

    /**
     * Удаляет тест из системы по идентификатору
     */
    public void deleteTest(UUID testId) {
        try {
            testDao.deleteById(testId);
        } catch (DataAccessException e){
            throw new TestDeletionFailedException("Failed to delete test with id " + testId);
        }
    }

    /**
     * Находит тест по идентификатору
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
     * Получает список всех тестов в формате DTO
     */
    public List<TestDTO> findAllTestsDTO() {
        return testDao.findAll().stream()
                .map(Test::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Обновляет текст вопроса и список ответов на вопрос
     */
    public TestDTO updateQuestion(UUID testId, QuestionDTO questionDTO) {
        ValidatorUtil.validate(questionDTO);
        Test test = findTestByIdOrThrow(testId);
        Optional<Question> questionToUpdate = test.getQuestions().stream()
                .filter(q -> q.getId().equals(questionDTO.getId()))
                .findFirst();

        if (questionToUpdate.isPresent()) {
            Question q = questionToUpdate.get();
            q.setQuestion_text(questionDTO.getQuestion_text());
            q.setAnswers(questionDTO.getAnswers().stream().map(AnswerDTO::toEntity).collect(Collectors.toList()));
            try {
                testDao.saveUniqueTest(test);
            } catch (DataAccessException e) {
                throw new SaveException("Failed to save test.");
            }
            return test.toDTO();
        } else {
            throw new ValidationException("Question does not belong to this test.");
        }
    }

    public void saveTest(TestDTO testDTO) {
        ValidatorUtil.validate(testDTO);
        if (testDTO.getQuestions() == null || testDTO.getQuestions().isEmpty()) {
            throw new ValidationException("Test must have at least one question.");
        }
        for (QuestionDTO question : testDTO.getQuestions()) {
            if (question.getAnswers() == null || question.getAnswers().isEmpty()) {
                throw new ValidationException("Question '" + question.getQuestion_text() + "' must have at least one answer.");
            }
            boolean hasCorrectAnswer = question.getAnswers().stream().anyMatch(AnswerDTO::isCorrect);
            if (!hasCorrectAnswer) {
                throw new ValidationException("Question '" + question.getQuestion_text() + "' must have at least one correct answer.");
            }
        }
        Test test = testDTO.toEntity();

        try {
            testDao.saveUniqueTest(test);
        } catch (DataAccessException e) {
            throw new SaveException("Failed to save test.");
        }
    }

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
            throw new SaveException("Failed to save test.");
        }
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
