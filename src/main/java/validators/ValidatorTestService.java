package validators;

import dto.AnswerDTO;
import dto.QuestionDTO;
import dto.TestDTO;
import exceptions.ValidationException;

import java.util.List;
import java.util.UUID;

public class ValidatorTestService {
    public void validate(TestDTO testDTO) {
        ValidatorUtil.validate(testDTO);
    }

    public void validate(QuestionDTO questionDTO) {
        ValidatorUtil.validate(questionDTO);
    }

    public void validateTestId(UUID id) {
        if (id == null) {
            throw new ValidationException("Test id is null or empty");
        }
    }

    public void validateQuestions(List<QuestionDTO> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new ValidationException("Test must have at least one question.");
        }
        for (QuestionDTO question : questions) {
            List<AnswerDTO> answers = question.getAnswers();
            if (answers == null || answers.isEmpty()) {
                throw new ValidationException("Question '" + question.getQuestionText() + "' must have at least one answer.");
            }
            boolean questionHaveCorrectAnswer = answers.stream().anyMatch(AnswerDTO::isCorrect);
            if (!questionHaveCorrectAnswer) {
                throw new ValidationException("Question must have at least one correct answer.");
            }
        }
    }
}
