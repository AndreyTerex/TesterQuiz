package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestStatsDTO {
    private String testTitle;
    private Integer totalPassed;
    private Integer totalQuestions;
    private Integer maxScore;
    private LocalDateTime lastPassed;
}
