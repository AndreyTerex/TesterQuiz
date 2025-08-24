package validators;

import dto.AnswerDTO;
import dto.QuestionDTO;
import dto.TestDTO;
import exceptions.ValidationException;
import util.ValidatorUtil;

import java.util.List;
import java.util.UUID;

public class ValidatorTestService extends ValidatorServiceBase  {
    public void validate(TestDTO testDTO) {
        if (testDTO == null) {
            throw new ValidationException("Test not found");
        }
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

    public void validateDetails(String title, String topic) {
        if (title != null && (title.isBlank() || title.length() > 255)) {
            throw new ValidationException("Title cannot be blank and must be less than 255 characters.");
        }
        if (topic != null && (topic.isBlank() || topic.length() > 255)) {
            throw new ValidationException("Topic cannot be blank and must be less than 255 characters.");
        }
        if (title == null || !title.matches("^(?!\\d+$)[a-zA-Zа-яА-ЯёЁ0-9]+$")) {
            throw new ValidationException("Title must contain only letters and digits and cannot be only digits");
        }
        if (topic == null || !topic.matches("^(?!\\d+$)[a-zA-Zа-яА-ЯёЁ0-9]+$")) {
            throw new ValidationException("Topic must contain only letters and digits and cannot be only digits");
        }
    }
}
