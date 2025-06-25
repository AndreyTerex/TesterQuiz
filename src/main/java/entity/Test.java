package entity;

import dto.TestDTO;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Test {
    private String title;
    private String topic;
    private UUID id;
    private UUID creator_id;
    private List<Question> questions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Test)) return false;
        return Objects.equals(id, ((Test) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public TestDTO toDTO(){
        return TestDTO.builder()
                .title(title)
                .topic(topic)
                .id(id)
                .creator_id(creator_id)
                .questions(questions.stream()
                        .map(Question::toDTO)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .build();
    }
}

