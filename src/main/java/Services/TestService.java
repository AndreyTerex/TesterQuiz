package Services;

import dao.TestDao;
import dto.*;
import entity.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Consumer;

public class TestService {
    private final TestDao testDao;
    private final ResultService resultService;

    public TestService(TestDao testDao, ResultService resultService) {
        this.testDao = testDao;
        this.resultService = resultService;
    }

    /**
     * Создает новый тест в системе
     */
    public boolean createTest(TestDTO testDTO) throws IOException {
        Test test = testDTO.toEntity();
        return testDao.saveNewTestToBaseFile(test);
    }

    /**
     * Добавляет новый вопрос к существующему тесту
     */
    public TestDTO addQuestion(TestDTO testDTO, String questionText, List<AnswerDTO> answerDTOList, String realPath) throws IOException {
        Test currentTest = testDTO.toEntity();
        if (currentTest != null) {
            List<Answer> answerList = new ArrayList<>();
            answerDTOList.forEach(answerDTO -> answerList.add(answerDTO.toEntity()));

            int questionNumber = currentTest.getQuestions().size() + 1;
            Question question = Question.builder()
                    .question_text(questionText)
                    .answers(answerList)
                    .question_number(questionNumber)
                    .id(UUID.randomUUID())
                    .build();

            currentTest.getQuestions().add(question);
            return currentTest.toDTO();
        }

        return null;
    }

    /**
     * Получает список всех тестов
     */
    private List<Test> findAll() throws IOException {
        return testDao.findAll();
    }

    /**
     * Удаляет тест из системы по идентификатору
     */
    public boolean deleteTest(String pathInfo, String realPath) throws IOException {
        if (pathInfo != null && pathInfo.length() > 1) {
            String id = pathInfo.substring(1);
            UUID testId = UUID.fromString(id);
            return testDao.deleteFromBaseFileAndUniqueFile(testId, realPath);
        }
        return false;
    }

    /**
     * Находит тест по идентификатору
     */
    public TestDTO findById(String id) throws IOException {
        Test test = testDao.findById(UUID.fromString(id));
        return test != null ? test.toDTO() : null;
    }

    /**
     * Начинает прохождение теста пользователем
     */
    public Map<String, Object> startTest(TestDTO currentTestDTO, UserDTO userDTO) throws IOException {
        Test currentTest = findById(currentTestDTO.getId().toString()).toEntity();
        UUID userid = userDTO.getId();

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime roundedEndTime = startTime.plusMinutes(10).truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String roundedEndTime_formatted = roundedEndTime.format(formatter);


        Result result = resultService.buildResultObject(currentTest, userid, startTime);

        Question currentQuestion = currentTest.getQuestions().stream().findFirst().orElse(null);
        if (currentQuestion != null) {
            Map<String, Object> sessionAttributes = new HashMap<>();
            sessionAttributes.put("currentQuestion", currentQuestion.toDTO());
            sessionAttributes.put("timeForTest", roundedEndTime_formatted);
            sessionAttributes.put("result", result.toDTO());
            sessionAttributes.put("testTimeOut", false);
            return sessionAttributes;

        } else {
            return null;
        }
    }

    /**
     * Проверяет, истекло ли время прохождения теста
     */
    public boolean checkTimeIsEnded(String endTime) {
        LocalDateTime endTime_formatted = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(endTime_formatted)) {
            return true;
        }
        return false;
    }

    /**
     * Переходит к следующему вопросу теста и сохраняет ответы
     */
    public Map<String, Object> nextQuestion(String[] selectedAnswers, QuestionDTO currentQuestionDTO, TestDTO currentTestDTO, ResultDTO resultDTO) throws IOException {
        Question currentQuestion = currentQuestionDTO.toEntity();
        Test currentTest = currentTestDTO.toEntity();
        Result result = resultDTO.toEntity();

        List<Answer> selectedAnswersList = new ArrayList<>();
        for (String selectedAnswer : selectedAnswers) {
            UUID answerId = UUID.fromString(selectedAnswer);
            Optional<Answer> currentAnswer = getAnswerByIdFromTest(currentQuestion, answerId);
            currentAnswer.ifPresent(selectedAnswersList::add);
        }
        ResultAnswer resultAnswer = ResultAnswer.builder()
                .question(currentQuestion)
                .selectedAnswers(selectedAnswersList)
                .build();

        result.getResultAnswers().add(resultAnswer);

        Optional<Question> nextQuestionDTO = getNextQuestion(currentTest, currentQuestion);
        Map<String, Object> resultAndNextQuestion = new HashMap<>();
        if (nextQuestionDTO.isPresent()) {
            resultAndNextQuestion.put("question", nextQuestionDTO.get().toDTO());
            resultAndNextQuestion.put("result", result.toDTO());
        } else {
            Result result1 = resultService.calculateScoreResult(result);
            resultAndNextQuestion.put("result", result1.toDTO());
        }
        return resultAndNextQuestion;
    }


    /**
     * Получает следующий вопрос в тесте
     */
    private Optional<Question> getNextQuestion(Test currentTest, Question currentQuestion) {
        return currentTest.getQuestions().stream().filter(question -> question.getQuestion_number() == currentQuestion.getQuestion_number() + 1).findFirst();

    }

    /**
     * Получает ответ по идентификатору из вопроса
     */
    private Optional<Answer> getAnswerByIdFromTest(Question currentQuestion, UUID answerId) {
        return currentQuestion.getAnswers().stream()
                .filter(answer -> answer.getId().equals(answerId))
                .findFirst();
    }

    /**
     * Проверяет существование теста по названию
     */
    public boolean existByTitle(TestDTO testDTO) throws IOException {
        return testDao.existsByTitle(testDTO.getTitle());
    }

    /**
     * Получает список всех тестов в формате DTO
     */
    public List<TestDTO> findAllTestsDTO() throws IOException {
        return findAll().stream()
                .map(Test::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Общий метод для обновления полей теста
     */
    private boolean updateTestField(UUID testId, String value, String realPath, Consumer<Test> fieldUpdater) throws IOException {
        if (value != null && !value.isBlank()) {
            Test test = testDao.findById(testId);
            testDao.deleteFromBaseFileAndUniqueFile(test.getId(), realPath);
            fieldUpdater.accept(test);
            return testDao.saveUniqueTest(test, realPath);
        }
        return false;
    }

    public boolean updateTitle(UUID testId, String title, String realPath) throws IOException {
        return updateTestField(testId, title, realPath, test -> test.setTitle(title));
    }

    public boolean updateTopic(UUID testId, String topic, String realPath) throws IOException {
        return updateTestField(testId, topic, realPath, test -> test.setTopic(topic));
    }


    /**
     * Обновляет текст вопроса и список ответов на вопрос
     */
    public boolean replaceAnswersAndTextOfQuestion(UUID testId, String questionId, String questionText, List<AnswerDTO> answerDTOList, String realPath) throws IOException {
        if (questionId == null || questionId.isBlank() || 
            questionText == null || questionText.isBlank() || 
            answerDTOList == null || answerDTOList.isEmpty()) {
            return false;
        }
        
        UUID questionUUID;
        try {
            questionUUID = UUID.fromString(questionId);
        } catch (IllegalArgumentException e) {
            return false;
        }

        List<Answer> answersList = answerDTOList.stream()
                .map(AnswerDTO::toEntity)
                .collect(Collectors.toList());

        return updateTestField(testId, questionId, realPath, test -> {
            for (Question question : test.getQuestions()) {
                if (question.getId().equals(questionUUID)) {
                    question.setQuestion_text(questionText);
                    question.setAnswers(answersList);
                    break;
                }
            }
        });
    }

    public boolean saveTest(TestDTO testDTO, String realPath) throws IOException {
        Test test = testDTO.toEntity();
        return testDao.saveUniqueTest(test, realPath);
    }
}
