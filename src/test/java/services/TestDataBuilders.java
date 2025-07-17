package services;

import entity.*;
import dto.*;

import java.time.LocalDateTime;
import java.util.*;

public class TestDataBuilders {
    public static User user() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        return user;
    }

    public static entity.Test test() {
        entity.Test test = new entity.Test();
        test.setId(UUID.randomUUID());
        test.setTitle("Sample Test");
        test.setQuestions(new ArrayList<>());
        return test;
    }

    public static Answer answer(boolean correct) {
        Answer answer = new Answer();
        answer.setId(UUID.randomUUID());
        answer.setCorrect(correct);
        answer.setAnswerText(correct ? "Correct" : "Wrong");
        return answer;
    }

    public static Question question(List<Answer> answers, int number) {
        Question question = new Question();
        question.setId(UUID.randomUUID());
        question.setAnswers(answers);
        question.setQuestionNumber(number);
        return question;
    }

    public static AnswersInResult answersInResult(Question question, List<Answer> selectedAnswers) {
        AnswersInResult air = new AnswersInResult();
        air.setQuestion(question);
        air.setSelectedAnswers(selectedAnswers);
        return air;
    }

    public static Result result(List<AnswersInResult> answersInResults) {
        Result result = new Result();
        result.setAnswersInResults(answersInResults);
        result.setDate(LocalDateTime.now());
        return result;
    }

    public static ResultDTO resultDTO(UUID userId, UUID testId, List<AnswersInResultDTO> answersInResults) {
        return ResultDTO.builder()
                .userId(userId)
                .testId(testId)
                .answersInResults(answersInResults)
                .build();
    }

    public static TestDTO testDTO(UUID id, String title, String topic, UUID creatorId, List<QuestionDTO> questions) {
        return TestDTO.builder()
                .id(id)
                .title(title)
                .topic(topic)
                .creatorId(creatorId)
                .questions(questions)
                .build();
    }

    public static AnswersInResultDTO answersInResultDTO(QuestionDTO question, List<AnswerDTO> selectedAnswers) {
        return AnswersInResultDTO.builder()
                .question(question)
                .selectedAnswers(selectedAnswers)
                .build();
    }

    public static QuestionDTO questionDTO(UUID id, int number) {
        return QuestionDTO.builder()
                .id(id)
                .questionNumber(number)
                .build();
    }

    public static AnswerDTO answerDTO(UUID id) {
        return AnswerDTO.builder()
                .id(id)
                .build();
    }
}
