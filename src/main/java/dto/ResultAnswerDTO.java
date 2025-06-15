package dto;

import entity.Answer;
import entity.Question;
import entity.ResultAnswer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultAnswerDTO {
    private QuestionDTO question;
    private List<AnswerDTO> selectedAnswers;

    public ResultAnswer toEntity(){
        return ResultAnswer.builder()
                .question(question.toEntity())
                .selectedAnswers(selectedAnswers.stream().map(AnswerDTO::toEntity).collect(Collectors.toList()))
                .build();
    }
}
