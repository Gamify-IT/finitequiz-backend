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
 * The GameResultDTO.class contains all data that is saved after one finitequiz game.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class GameResultDTO {

    /**
     * A unique identifier for the game result.
     */
    @Nullable
    private UUID id;

    /**
     * The total number of answered questions.
     */
    @Min(
        value = Constants.MIN_QUESTION_COUNT,
        message = "cannot have less than " + Constants.MIN_QUESTION_COUNT + " questions"
    )
    @Max(
        value = Constants.MAX_QUESTION_COUNT,
        message = "cannot have more than " + Constants.MIN_QUESTION_COUNT + " questions"
    )
    private int questionCount;

    /**
     * The score achieved in the game.
     */
    @Min(value = Constants.MIN_SCORE, message = "Score cannot be less than " + Constants.MIN_SCORE)
    @Max(value = Constants.MAX_SCORE, message = "Score cannot be higher than " + Constants.MAX_SCORE)
    private long score;

    /**
     * A list of correctly answered questions and the selected answer.
     */
    @Valid
    private List<RoundResultDTO> correctAnsweredQuestions;

    /**
     * A list of incorrectly answered questions and the selected answer.
     */
    @Valid
    private List<RoundResultDTO> wrongAnsweredQuestions;

    /**
     * The ID of the configuration used for this game.
     */
    @NotNull(message = "configurationAsUUID cannot be null")
    private UUID configurationAsUUID;

    /**
     * The time spent in seconds on the game for this run.
     */
    private long timeSpent;

    public GameResultDTO(
        final int questionCount,
        final long score,
        final long timeSpent,
        final List<RoundResultDTO> correctAnsweredQuestions,
        final List<RoundResultDTO> wrongAnsweredQuestions,
        final UUID configurationAsUUID
    ) {
        this.questionCount = questionCount;
        this.score = score;
        this.timeSpent = timeSpent;
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
