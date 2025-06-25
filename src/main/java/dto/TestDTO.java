package dto;

import entity.Test;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestDTO {
    @NotBlank(message = "title must not be blank")
    private String title;
    @NotBlank(message = "topic must not be blank")
    private String topic;
    private UUID id;
    private UUID creator_id;
    private List<QuestionDTO> questions;


    public Test toEntity(){
        return Test.builder()
                .title(title)
                .topic(topic)
                .id(id)
                .creator_id(creator_id)
                .questions(questions.stream()
                        .map(QuestionDTO::toEntity)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .build();
    }
}
