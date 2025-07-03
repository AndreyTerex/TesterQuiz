package entity;

import dto.QuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    private UUID id;
    private Integer questionNumber;
    private String questionText;
    private List<Answer> answers;

    public QuestionDTO toDTO() {
        return QuestionDTO.builder()
                .id(id)
                .questionNumber(questionNumber)
                .questionText(questionText)
                .answers(answers.stream()
                        .map(Answer::toDTO)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .build();
    }
}
