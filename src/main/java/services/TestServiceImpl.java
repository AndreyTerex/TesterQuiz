package services;

import dao.TestDAO;
import dto.AnswerDTO;
import dto.QuestionDTO;
import dto.TestDTO;
import entity.Answer;
import entity.Question;
import entity.Test;
import exceptions.DataAccessException;
import exceptions.SaveException;
import exceptions.TestDeletionFailedException;
import exceptions.ValidationException;
import mappers.AnswerMapper;
import mappers.TestMapper;
import services.interfaces.TestService;
import services.interfaces.UserService;
import validators.ValidatorTestService;

import java.util.*;
import java.util.stream.Collectors;

public class TestServiceImpl implements TestService {
    private final TestDAO testDao;
    private final UserService userService;
    private final ValidatorTestService validatorTestService;
    private final TestMapper testMapper;
    private final AnswerMapper answerMapper;

    public TestServiceImpl(TestDAO testDao, UserService userService, ValidatorTestService validatorTestService, TestMapper testMapper, AnswerMapper answerMapper) {
        this.testDao = testDao;
        this.userService = userService;
        this.validatorTestService = validatorTestService;
        this.testMapper = testMapper;
        this.answerMapper = answerMapper;
    }


    public void checkTestTitleAndValidate(TestDTO testDTO) {
        validatorTestService.validate(testDTO);
        if (testDao.existByTitle(testDTO.getTitle())) {
            throw new ValidationException("Test with title '" + testDTO.getTitle() + "' already exists.");
        }
    }


    public TestDTO addQuestion(TestDTO testDTO, QuestionDTO questionDTO) {
        validatorTestService.validate(questionDTO);
        validatorTestService.validate(testDTO);

        List<QuestionDTO> newQuestions = new ArrayList<>(testDTO.getQuestions() != null ? testDTO.getQuestions() : Collections.emptyList());

        QuestionDTO numberedQuestion = questionDTO.toBuilder()
                .questionNumber(newQuestions.size() + 1)
                .build();

        newQuestions.add(numberedQuestion);

        return testDTO.toBuilder()
                .questions(newQuestions)
                .build();
    }
    public void deleteTest(UUID testId) {
        validatorTestService.validateTestId(testId);
        try {
            testDao.deleteById(testId);
        } catch (DataAccessException e) {
            throw new TestDeletionFailedException("Failed to delete test with id " + testId, e);
        }
    }

    public TestDTO findDTOById(UUID id) {
        return testMapper.toDTO(findTestByIdOrThrow(id));
    }

    public Test findTestById(UUID id) {
        return findTestByIdOrThrow(id);
    }

    public List<TestDTO> findAllTestsDTO() {
        return Optional.ofNullable(testDao.findAll())
                .orElse(Collections.emptyList())
                .stream()
                .map(testMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TestDTO updateQuestion(UUID testId, QuestionDTO questionDTO) {
        validatorTestService.validate(questionDTO);
        Test test = findTestByIdOrThrow(testId);

        Question question = test.getQuestions().stream()
                .filter(q -> q.getId().equals(questionDTO.getId()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Question does not belong to this test."));

        question.setQuestionText(questionDTO.getQuestionText());
        updateAnswersForQuestion(question, questionDTO.getAnswers());

        try {
            testDao.update(test);
        } catch (DataAccessException e) {
            throw new SaveException("Failed to update question in test.", e);
        }
        return testMapper.toDTO(findTestByIdOrThrow(testId));
    }

    public void saveTest(TestDTO testDTO) {
        validatorTestService.validate(testDTO);
        validatorTestService.validateQuestions(testDTO.getQuestions());
        Test currentTest = testMapper.toEntity(testDTO);
        currentTest.setCreator(validatorTestService.requireNonNullOrValidation(userService.findUserById(testDTO.getCreatorId()), "User not found"));
        linkQuestionsAndAnswers(currentTest);

        try {
            testDao.update(currentTest);
        } catch (DataAccessException e) {
            throw new SaveException("Failed to save test.", e);
        }
    }

    public TestDTO updateTestDetails(UUID testId, String newTitle, String newTopic) {
        validatorTestService.validateDetails(newTitle, newTopic);
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
            testDao.update(test);
        } catch (DataAccessException e) {
            throw new SaveException("Failed to save test.", e);
        }
        return testMapper.toDTO(findTestByIdOrThrow(testId));
    }

    private Test findTestByIdOrThrow(UUID testId) {
        validatorTestService.validateTestId(testId);
        return testDao.findByIdWithDetails(testId).orElseThrow(() -> new ValidationException("Test not found with id: " + testId));
    }

    private void linkQuestionsAndAnswers(Test test) {
        if (test.getQuestions() == null) {
            return;
        }
        test.getQuestions().forEach(question -> {
            question.setTest(test);
            if (question.getAnswers() != null) {
                question.getAnswers().forEach(answer -> answer.setQuestion(question));
            }
        });
    }

    private void updateAnswersForQuestion(Question question, List<AnswerDTO> answerDTOs) {
        question.getAnswers().clear();

        if (answerDTOs != null) {
            for (AnswerDTO dto : answerDTOs) {
                Answer answer = answerMapper.toEntity(dto);
                answer.setQuestion(question);
                question.getAnswers().add(answer);
            }
        }
    }
}
