package de.unistuttgart.finitequizbackend.data;

import de.unistuttgart.finitequizbackend.Constants;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

/**
 * The GameResultDTO.class contains all data that is saved after one finitequiz game
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class GameResultDTO {

    @Nullable
    private UUID id;

    @Min(
        value = Constants.MIN_QUESTION_COUNT,
        message = "cannot have less than " + Constants.MIN_QUESTION_COUNT + " questions"
    )
    @Max(
        value = Constants.MAX_QUESTION_COUNT,
        message = "cannot have more than " + Constants.MIN_QUESTION_COUNT + " questions"
    )
    private int questionCount;

    @Min(value = Constants.MIN_SCORE, message = "Score cannot be less than " + Constants.MIN_SCORE)
    @Max(value = Constants.MAX_SCORE, message = "Score cannot be higher than " + Constants.MAX_SCORE)
    private long score;

    @Valid
    private List<RoundResultDTO> correctAnsweredQuestions;

    @Valid
    private List<RoundResultDTO> wrongAnsweredQuestions;

    @NotNull(message = "configurationAsUUID cannot be null")
    private UUID configurationAsUUID;

    public GameResultDTO(
        final int questionCount,
        final long score,
        final List<RoundResultDTO> correctAnsweredQuestions,
        final List<RoundResultDTO> wrongAnsweredQuestions,
        final UUID configurationAsUUID
    ) {
        this.questionCount = questionCount;
        this.score = score;
        this.correctAnsweredQuestions = correctAnsweredQuestions;
        this.wrongAnsweredQuestions = wrongAnsweredQuestions;
        this.configurationAsUUID = configurationAsUUID;
    }

    public boolean equalsContent(final GameResultDTO other) {
        if (this == other) return true;
        if (other == null) return false;

        if (id != other.id) return false;
        if (questionCount != other.questionCount) return false;
        if (score != other.score) return false;
        if (!correctAnsweredQuestions.equals(other.correctAnsweredQuestions)) return false;
        if (!wrongAnsweredQuestions.equals(other.wrongAnsweredQuestions)) return false;
        return configurationAsUUID.equals(other.configurationAsUUID);
    }
}
