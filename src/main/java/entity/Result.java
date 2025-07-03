package entity;

import dto.ResultDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {
    private UUID id;
    private UUID userId;
    private UUID testId;
    private String testTitle;
    private Integer score;
    private LocalDateTime date;
    private List<ResultAnswer> resultAnswers;


    public ResultDTO toDTO() {
        return ResultDTO.builder()
                .id(id)
                .userId(userId)
                .testId(testId)
                .score(score)
                .date(date)
                .testTitle(testTitle)
                .resultAnswers(resultAnswers.stream()
                        .map(ResultAnswer::toDTO)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .build();
    }
}
