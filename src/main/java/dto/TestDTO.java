package dto;

import entity.Test;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters long.")
    @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я ]+$", message = "Title can only contain letters, numbers, and spaces.")
    private String title;
    @NotBlank(message = "topic must not be blank")
    @Size(min = 3, max = 50, message = "Topic must be between 3 and 50 characters long.")
    @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я ]+$", message = "Topic can only contain letters, numbers, and spaces.")
    private String topic;
    private UUID id;
    private UUID creatorId;
    private List<QuestionDTO> questions;


    public Test toEntity() {
        return Test.builder()
                .title(title)
                .topic(topic)
                .id(id)
                .creatorId(creatorId)
                .questions(questions.stream()
                        .map(QuestionDTO::toEntity)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .build();
    }
}
