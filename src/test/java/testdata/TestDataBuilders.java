package testdata;

import entity.*;
import dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Простые билдеры для создания тестовых данных
 */
public class TestDataBuilders {

    public static AnswerDTO correctAnswer(String text) {
        return AnswerDTO.builder()
                .answerText(text)
                .correct(true)
                .build();
    }

    public static AnswerDTO correctAnswerWithId(String text, UUID id) {
        return AnswerDTO.builder()
                .id(id)
                .answerText(text)
                .correct(true)
                .build();
    }

    public static QuestionDTO questionWithAnswerAndAnswerId(String questionText, String answerText, UUID questionId, int questionNumber, UUID answerId) {
        return QuestionDTO.builder()
                .id(questionId)
                .questionText(questionText)
                .questionNumber(questionNumber)
                .answers(new ArrayList<>(List.of(correctAnswerWithId(answerText, answerId))))
                .build();
    }

    public static AnswerDTO incorrectAnswer(String text) {
        return AnswerDTO.builder()
                .answerText(text)
                .correct(false)
                .build();
    }

    public static QuestionDTO simpleQuestion(String text) {
        return QuestionDTO.builder()
                .questionText(text)
                .answers(new ArrayList<>(List.of(correctAnswer("Correct Answer"))))
                .build();
    }

    public static QuestionDTO questionWithAnswer(String questionText, String answerText) {
        return   QuestionDTO.builder()
                .questionText(questionText)
                .answers(new ArrayList<>(List.of(correctAnswer(answerText))))
                .build();
    }

    public static QuestionDTO questionWithId(UUID id, String text) {
        return   QuestionDTO.builder()
                .id(id)
                .questionText(text)
                .answers(new ArrayList<>(List.of(correctAnswer("Correct Answer"))))
                .build();
    }

    public static QuestionDTO questionWithNoAnswers(String text) {
        return   QuestionDTO.builder()
                .questionText(text)
                .answers(new ArrayList<>())
                .build();
    }

    public static QuestionDTO questionWithIncorrectAnswers(String text) {
        return QuestionDTO.builder()
                .questionText(text)
                .answers(new ArrayList<>(List.of(incorrectAnswer("Wrong Answer"))))
                .build();
    }

    public static TestDTO simpleTest(String title, String topic) {
        return TestDTO.builder()
                .title(title)
                .topic(topic)
                .questions(new ArrayList<>(List.of(simpleQuestion("Default question text that is long enough"))))
                .build();
    }

    public static TestDTO emptyTest(String title, String topic) {
        return TestDTO.builder()
                .title(title)
                .topic(topic)
                .questions(new ArrayList<>())
                .build();
    }

    public static TestDTO testWithQuestion(String title, String topic, QuestionDTO question) {
        return TestDTO.builder()
                .title(title)
                .topic(topic)
                .questions(new ArrayList<>(List.of(question)))
                .build();
    }

    public static Answer correctAnswerEntity(String text) {
        return Answer.builder()
                .answerText(text)
                .correct(true)
                .build();
    }

    public static Answer answerEntityWithId(String text, boolean isCorrect, UUID id) {
        return Answer.builder()
                .id(id)
                .answerText(text)
                .correct(isCorrect)
                .build();
    }

    public static Question questionEntityWithIdAndAnswers(UUID id, String text, int number, List<Answer> answers) {
        return Question.builder()
                .id(id)
                .questionText(text)
                .questionNumber(number)
                .answers(answers)
                .build();
    }

    public static ResultDTO resultDTOWithTestId(UUID testId) {
        return ResultDTO.builder()
                .testId(testId)
                .resultAnswers(new ArrayList<>())
                .build();
    }

    public static Question questionEntity(UUID id, String text) {
        return Question.builder()
                .id(id)
                .questionText(text)
                .answers(new ArrayList<>(List.of(correctAnswerEntity("Old answer"))))
                .build();
    }

    public static Test testEntity(UUID testId, String title) {
        return Test.builder()
                .id(testId)
                .title(title)
                .topic("Default Topic")
                .questions(new ArrayList<>())
                .build();
    }

    public static Test testEntityWithQuestion(UUID testId, UUID questionId) {
        Question question = questionEntity(questionId, "Old question text is long enough");
        return Test.builder()
                .id(testId)
                .questions(new ArrayList<>(List.of(question)))
                .build();
    }

    // Универсальный билдер для Test с id, title, topic, questions
    public static Test testEntityFull(UUID id, String title, String topic, List<Question> questions) {
        return Test.builder()
                .id(id)
                .title(title)
                .topic(topic)
                .questions(questions != null ? questions : new ArrayList<>())
                .build();
    }

    /**
     * Создает валидный тест для сохранения
     */
    public static TestDTO validTestForSave() {
        return simpleTest("Valid Title", "Valid Topic");
    }

    /**
     * Создает тест без вопросов
     */
    public static TestDTO testWithNoQuestions() {
        return emptyTest("Title", "Topic");
    }

    /**
     * Создает тест с вопросом без ответов
     */
    public static TestDTO testWithQuestionWithoutAnswers() {
        QuestionDTO question = questionWithNoAnswers("Question with no answers");
        return testWithQuestion("Title", "Topic", question);
    }

    /**
     * Создает тест с вопросом без правильных ответов
     */
    public static TestDTO testWithQuestionWithoutCorrectAnswers() {
        QuestionDTO question = questionWithIncorrectAnswers("A valid question text that is long enough");
        return testWithQuestion("Title", "Topic", question);
    }

    public static Result newResult(UUID resultId, UUID userId, UUID testId, List<ResultAnswer> resultAnswers, Integer score, java.time.LocalDateTime date) {
        return Result.builder()
                .id(resultId)
                .userId(userId)
                .testId(testId)
                .resultAnswers(resultAnswers != null ? resultAnswers : new ArrayList<>())
                .score(score)
                .date(date)
                .build();
    }
    public static Result resultWithDateAndScore(UUID id, UUID userId, UUID testId, List<ResultAnswer> resultAnswers, Integer score, java.time.LocalDateTime date) {
        return Result.builder()
                .id(id)
                .userId(userId)
                .testId(testId)
                .resultAnswers(resultAnswers != null ? resultAnswers : new ArrayList<>())
                .score(score)
                .date(date)
                .build();
    }


    public static TestDTO testDTOWithIdAndQuestions(String title, UUID id, List<QuestionDTO> questions) {
        return TestDTO.builder()
                .id(id)
                .title(title)
                .questions(questions)
                .build();
    }

    public static User userEntity(UUID id, String username, String password, String role) {
        return User.builder()
                .id(id)
                .username(username)
                .password(password)
                .role(role)
                .build();
    }

    public static UserDTO userDTO(UUID id, String username, String role) {
        return UserDTO.builder()
                .id(id)
                .username(username)
                .role(role)
                .build();
    }
}
