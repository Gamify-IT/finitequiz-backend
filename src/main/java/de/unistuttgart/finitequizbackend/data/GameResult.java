package de.unistuttgart.finitequizbackend.data;

import de.unistuttgart.finitequizbackend.Constants;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.validation.annotation.Validated;

/**
 * The GameResult.class contains all data that is saved after one finitequiz game.
 */
@Entity
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class GameResult {

    /**
     * A unique identifier for the game result.
     */
    @Id
    @GeneratedValue(generator = "uuid")
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
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Valid
    private List<RoundResult> correctAnsweredQuestions;

    /**
     * A list of incorrectly answered questions and the selected answer.
     */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Valid
    private List<RoundResult> wrongAnsweredQuestions;

    /**
     * The ID of the configuration that was used for the game.
     */
    @NotNull(message = "configurationAsUUID cannot be null")
    private UUID configurationAsUUID;

    @NotNull(message = "playerId cannot be null")
    private String playerId;

    @NotNull(message = "playedTime cannot be null")
    @CreationTimestamp
    private Date playedTime = new Date();

    /**
     * The time spent in seconds on the game for this run.
     */
    private long timeSpent;

    /**
     * The reward-coins that the player achieved in the current round.
     */
    @Min(value = Constants.MIN_REWARDS, message = "Rewards cannot be less than " + Constants.MIN_REWARDS)
    @Max(value = Constants.MAX_REWARDS, message = "Rewards cannot be higher than " + Constants.MAX_REWARDS)
    private int rewards;


    public GameResult(
        final int questionCount,
        final long score,
        final long timeSpent,
        final int rewards,
        final List<RoundResult> correctAnsweredQuestions,
        final List<RoundResult> wrongAnsweredQuestions,
        final UUID configurationAsUUID,
        final String playerId
    ) {
        this.questionCount = questionCount;
        this.score = score;
        this.timeSpent = timeSpent;
        this.rewards = rewards;
        this.correctAnsweredQuestions = correctAnsweredQuestions;
        this.wrongAnsweredQuestions = wrongAnsweredQuestions;
        this.configurationAsUUID = configurationAsUUID;
        this.playerId = playerId;
    }
}
