package dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ResultDTO {
    private final UUID id;
    @NotNull(message = "User ID cannot be null.")
    private final UUID userId;
    @NotNull(message = "Test ID cannot be null.")
    private final UUID testId;
    @NotNull(message = "Score cannot be null.")
    @Min(value = 0, message = "Score cannot be negative.")
    private final Integer score;
    @NotBlank(message = "Test title cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я ]+$", message = "Test title can only contain letters, numbers, and spaces.")
    private final String testTitle;
    @PastOrPresent(message = "Date cannot be in the future.")
    private final LocalDateTime date;
    private final List<ResultAnswerDTO> resultAnswers;
}
