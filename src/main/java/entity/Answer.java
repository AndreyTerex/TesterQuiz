package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;
import lombok.ToString;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "answers")
@SoftDelete
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "answer_text")
    private String answerText;

    private boolean correct;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "question_id")
    @ToString.Exclude
    private Question question;

    @Version
    private Integer version;
}
