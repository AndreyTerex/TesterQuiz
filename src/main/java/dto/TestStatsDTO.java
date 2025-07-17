package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


import java.time.LocalDateTime;


@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class TestStatsDTO {
    private final String testTitle;
    private final Long totalPassed;
    private final Integer totalQuestions;
    private final Integer maxScore;
    private final LocalDateTime lastPassed;
}
