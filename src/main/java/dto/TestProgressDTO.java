package dto;


import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class TestProgressDTO {
    private ResultDTO result;
    private QuestionDTO question;
    private String [] answers;
    private boolean isTestFinished;


}
