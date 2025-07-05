package dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class QuestionDTO {
    private final UUID id;
    @Min(value = 1, message = "Question number must be a positive integer.")
    private final Integer questionNumber;
    @NotBlank(message = "question text must not be blank")
    @Size(min = 10, max = 255, message = "Question text must be between 10 and 255 characters long.")
    @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я ]+$", message = "Question text can only contain letters, numbers, and spaces.")
    private final String questionText;
    @Valid
    @NotEmpty(message = "answers must not be empty")
    private final List<AnswerDTO> answers;
}
