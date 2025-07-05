package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ResultAnswerDTO {
    private final QuestionDTO question;
    private final List<AnswerDTO> selectedAnswers;
}
