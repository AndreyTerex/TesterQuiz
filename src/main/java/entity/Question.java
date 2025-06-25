package entity;

import dto.QuestionDTO;

import java.util.ArrayList;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    private UUID id;
    private Integer question_number;
    private String question_text;
    private List<Answer> answers;

    public QuestionDTO toDTO() {
        return QuestionDTO.builder()
                .id(id)
                .question_number(question_number)
                .question_text(question_text)
                .answers(answers.stream()
                        .map(Answer::toDTO)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .build();
    }
}
