package entity;

import dto.ResultAnswerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultAnswer {
    private Question question;
    private List<Answer> selectedAnswers;


    public ResultAnswerDTO toDTO() {
        return ResultAnswerDTO.builder()
                .question(question.toDTO())
                .selectedAnswers(selectedAnswers.stream().map(Answer::toDTO).toList())
                .build();
    }
}
