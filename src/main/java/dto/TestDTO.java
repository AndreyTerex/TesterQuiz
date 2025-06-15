package dto;

import entity.Test;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestDTO {
    private String title;
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
                .questions(questions.stream().map(QuestionDTO::toEntity).collect(Collectors.toList()))
                .build();
    }
}
