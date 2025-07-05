package entity;

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
    private UUID userId;
    private UUID testId;
    private String testTitle;
    private Integer score;
    private LocalDateTime date;
    private List<ResultAnswer> resultAnswers;
}
