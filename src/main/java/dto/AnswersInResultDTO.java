package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class AnswersInResultDTO {
    private final UUID id;
    private final UUID resultId;
    private final QuestionDTO question;
    private final List<AnswerDTO> selectedAnswers;
}
