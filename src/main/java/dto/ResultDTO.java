package dto;

import entity.Result;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultDTO {
    private UUID id;
    private UUID user_id;
    private UUID test_id;
    private Integer score;
    private String testTitle;
    private LocalDateTime date;
    private List<ResultAnswerDTO> resultAnswers;

    public Result toEntity() {
        return Result.builder()
                .id(id)
                .user_id(user_id)
                .test_id(test_id)
                .score(score)
                .date(date)
                .testTitle(testTitle)
                .resultAnswers(resultAnswers.stream()
                        .map(ResultAnswerDTO::toEntity)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .build();
    }
}
