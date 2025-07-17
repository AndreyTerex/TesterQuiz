package dto;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


@Data
@Builder(toBuilder = true)
public class TestProgressDTO {
    @NotNull(message = "Result not found")
    private final ResultDTO result;
    @NotNull(message = "Question not found")
    private final QuestionDTO question;
    @NotNull(message = "Answers are not selected")
    private final String[] answers;
    private final boolean isTestFinished;


}
