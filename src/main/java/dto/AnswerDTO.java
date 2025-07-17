package dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class AnswerDTO {
    private final UUID id;
    private final Integer version;
    @NotBlank(message = "answer text must not be blank")
    @Size(min = 1, max = 255, message = "Answer text must be between 1 and 255 characters long.")
    @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я ]+$", message = "Answer text can only contain letters, numbers, and spaces.")
    private final String answerText;
    private final boolean correct;
}
