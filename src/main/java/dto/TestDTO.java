package dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class TestDTO {
    @NotBlank(message = "title must not be blank")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters long.")
    @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я ]+$", message = "Title can only contain letters, numbers, and spaces.")
    private final String title;
    @NotBlank(message = "topic must not be blank")
    @Size(min = 3, max = 50, message = "Topic must be between 3 and 50 characters long.")
    @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я ]+$", message = "Topic can only contain letters, numbers, and spaces.")
    private final String topic;
    private final UUID id;
    private final UUID creatorId;
    private final List<QuestionDTO> questions;
}
