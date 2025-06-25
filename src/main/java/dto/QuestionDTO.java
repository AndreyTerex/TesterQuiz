package dto;

import entity.Question;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
    private Integer question_number;
    @NotBlank(message = "question text must not be blank")
    private String question_text;
    @Valid
    @NotEmpty(message = "answers must not be empty")
    private List<AnswerDTO> answers;

    public Question toEntity() {
        return Question.builder()
                .id(id)
                .question_number(question_number)
                .question_text(question_text)
                .answers(answers.stream()
                        .map(AnswerDTO::toEntity)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .build();
    }
}
