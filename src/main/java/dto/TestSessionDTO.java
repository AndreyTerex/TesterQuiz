package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestSessionDTO {
    private QuestionDTO currentQuestion;
    private String roundedEndTime;
    private ResultDTO result;
    private boolean testTimeOut;
}
