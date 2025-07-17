package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class TestSessionDTO {
    private final QuestionDTO currentQuestion;
    private final String roundedEndTime;
    private final ResultDTO result;
    private final boolean testTimeOut;
}
