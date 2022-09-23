package de.unistuttgart.finitequizbackend.data;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

/**
 * The ConfigurationDTO.class contains all data that has to be stored to configure a finitequiz game
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class ConfigurationDTO {

    @Nullable
    UUID id;

    @Valid
    Set<QuestionDTO> questions;

    public ConfigurationDTO(final Set<QuestionDTO> questions) {
        this.questions = questions;
    }

    public boolean equalsContent(final ConfigurationDTO other) {
        if (this == other) return true;
        if (other == null) return false;
        return Objects.equals(questions, other.questions);
    }
}
