package dto;

import entity.ResultAnswer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Builder
public class ResultAnswerDTO {
    private final QuestionDTO question;
    private final List<AnswerDTO> selectedAnswers;

    public ResultAnswer toEntity() {
        return ResultAnswer.builder()
                .question(question.toEntity())
                .selectedAnswers(selectedAnswers.stream()
                        .map(AnswerDTO::toEntity)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .build();
    }
}
