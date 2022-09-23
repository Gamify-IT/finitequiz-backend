package de.unistuttgart.finitequizbackend.data;

import de.unistuttgart.finitequizbackend.Constants;
import java.time.LocalDateTime;
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
import org.springframework.validation.annotation.Validated;

/**
 * The GameResult.class contains all data that is saved after one finitequiz game
 */
@Entity
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class GameResult {

    @Id
    @GeneratedValue(generator = "uuid")
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

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Valid
    private List<RoundResult> correctAnsweredQuestions;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Valid
    private List<RoundResult> wrongAnsweredQuestions;

    @NotNull(message = "configurationAsUUID cannot be null")
    private UUID configurationAsUUID;

    @NotNull(message = "playerId cannot be null")
    private String playerId;

    @NotNull(message = "playedTime cannot be null")
    private LocalDateTime playedTime;

    public GameResult(
        final int questionCount,
        final long score,
        final List<RoundResult> correctAnsweredQuestions,
        final List<RoundResult> wrongAnsweredQuestions,
        final UUID configurationAsUUID,
        final String playerId
    ) {
        this.questionCount = questionCount;
        this.score = score;
        this.correctAnsweredQuestions = correctAnsweredQuestions;
        this.wrongAnsweredQuestions = wrongAnsweredQuestions;
        this.configurationAsUUID = configurationAsUUID;
        this.playerId = playerId;
        this.playedTime = LocalDateTime.now();
    }
}
