package dto;

import entity.Question;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDTO {
    private UUID id;
    @Min(value = 1, message = "Question number must be a positive integer.")
    private Integer questionNumber;
    @NotBlank(message = "question text must not be blank")
    @Size(min = 10, max = 255, message = "Question text must be between 10 and 255 characters long.")
    @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я ]+$", message = "Question text can only contain letters, numbers, and spaces.")
    private String questionText;
    @Valid
    @NotEmpty(message = "answers must not be empty")
    private List<AnswerDTO> answers;

    public Question toEntity() {
        return Question.builder()
                .id(id)
                .questionNumber(questionNumber)
                .questionText(questionText)
                .answers(answers.stream()
                        .map(AnswerDTO::toEntity)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .build();
    }
}
