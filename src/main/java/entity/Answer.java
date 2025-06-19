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
    private String answer_text;
    private boolean isCorrect;

    public AnswerDTO toDTO() {
        return AnswerDTO.builder()
                .id(id)
                .answer_text(answer_text)
                .isCorrect(isCorrect)
                .build();
    }
}