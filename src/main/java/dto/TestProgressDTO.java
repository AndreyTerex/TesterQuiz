package dto;


import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class TestProgressDTO {
    private final ResultDTO result;
    private final QuestionDTO question;
    private final String[] answers;
    private final boolean isTestFinished;


}
