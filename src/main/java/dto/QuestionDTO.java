package dto;

import entity.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDTO {
    private UUID id;
    private Integer question_number;
    private String question_text;
    private List<AnswerDTO> answers;

    public Question toEntity() {
        return Question.builder()
                .id(id)
                .question_number(question_number)
                .question_text(question_text)
                .answers(answers.stream().map(AnswerDTO::toEntity).collect(Collectors.toList()))
                .build();
    }
}
