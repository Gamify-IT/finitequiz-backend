package de.unistuttgart.finitequizbackend.data;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.*;
import javax.validation.Valid;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;

/**
 * The Configuration.class contains all data that has to be stored to configure a finitequiz game.
 */
@Entity
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class Configuration {

    /**
     * A unique identifier for the configuration.
     */
    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    /**
     * A list of questions that are used in the finitequiz game.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Valid
    Set<Question> questions;

    public Configuration(final Set<Question> questions) {
        this.questions = questions;
    }

    /**
     * Add a new question to the configuration.
     * @param question the question to add
     */
    public void addQuestion(final Question question) {
        this.questions.add(question);
    }

    /**
     * Remove a question from the configuration.
     * @param question the question to remove
     */
    public void removeQuestion(final Question question) {
        this.questions.remove(question);
    }

    @Override
    public Configuration clone() {
        return new Configuration(
            this.questions.stream().map(question -> question = question.clone()).collect(Collectors.toSet())
        );
    }
}
