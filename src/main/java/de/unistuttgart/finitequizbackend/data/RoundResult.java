package de.unistuttgart.finitequizbackend.data;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;

/**
 * The RoundResult.class contains the round result related information
 */
@Entity
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class RoundResult {

    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    @NotNull(message = "question cannot be null")
    @ManyToOne
    @Valid
    Question question;

    @NotNull(message = "answer cannot be null")
    @NotBlank(message = "answer cannot be blank")
    String answer;

    public RoundResult(final Question question, final String answer) {
        this.question = question;
        this.answer = answer;
    }
}
