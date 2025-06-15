package entity;

import dto.ResultDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {
    private UUID id;
    private UUID user_id;
    private UUID test_id;
    private Integer score;
    private LocalDateTime date;
    private List<ResultAnswer> resultAnswers;


    public ResultDTO toDTO() {
        return ResultDTO.builder()
                .id(id)
                .user_id(user_id)
                .test_id(test_id)
                .score(score)
                .date(date)
                .resultAnswers(resultAnswers.stream()
                        .map(ResultAnswer::toDTO)
                        .toList())
                .build();
    }
}
