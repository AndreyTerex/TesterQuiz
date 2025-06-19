package dto;

import entity.Answer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerDTO {
    private UUID id;
    private String answer_text;
    private boolean isCorrect;

    public Answer toEntity(){
        return Answer.builder()
                .id(id)
                .answer_text(answer_text)
                .isCorrect(isCorrect)
                .build();
    }
}