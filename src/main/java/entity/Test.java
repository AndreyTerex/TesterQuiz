package entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Test {
    private String title;
    private String topic;
    private UUID id;
    private UUID creatorId;
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
}
