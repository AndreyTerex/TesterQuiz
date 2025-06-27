package dto;

import entity.Answer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "answer text must not be blank")
    @Size(min = 1, max = 255, message = "Answer text must be between 1 and 255 characters long.")
    @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я ]+$", message = "Answer text can only contain letters, numbers, and spaces.")
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