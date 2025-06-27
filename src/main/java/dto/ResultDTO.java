package dto;

import entity.Result;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultDTO {
    private UUID id;
    @NotNull(message = "User ID cannot be null.")
    private UUID user_id;
    @NotNull(message = "Test ID cannot be null.")
    private UUID test_id;
    @NotNull(message = "Score cannot be null.")
    @Min(value = 0, message = "Score cannot be negative.")
    private Integer score;
    @NotBlank(message = "Test title cannot be blank.")
    @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я ]+$", message = "Test title can only contain letters, numbers, and spaces.")
    private String testTitle;
    @PastOrPresent(message = "Date cannot be in the future.")
    private LocalDateTime date;
    private List<ResultAnswerDTO> resultAnswers;

    public Result toEntity() {
        return Result.builder()
                .id(id)
                .user_id(user_id)
                .test_id(test_id)
                .score(score)
                .date(date)
                .testTitle(testTitle)
                .resultAnswers(resultAnswers.stream()
                        .map(ResultAnswerDTO::toEntity)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .build();
    }
}
