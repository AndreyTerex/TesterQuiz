package entity;

import dto.AnswerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer {
    private UUID id;
    private String answerText;
    private boolean isCorrect;

    public AnswerDTO toDTO() {
        return AnswerDTO.builder()
                .id(id)
                .answerText(answerText)
                .isCorrect(isCorrect)
                .build();
    }
}