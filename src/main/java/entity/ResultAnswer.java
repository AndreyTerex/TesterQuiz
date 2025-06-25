package entity;

import dto.ResultAnswerDTO;

import java.util.ArrayList;
import java.util.stream.Collectors;
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
                .selectedAnswers(selectedAnswers.stream()
                        .map(Answer::toDTO)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .build();
    }
}
