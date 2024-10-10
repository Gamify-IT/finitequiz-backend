package de.unistuttgart.finitequizbackend.data;

import de.unistuttgart.finitequizbackend.Constants;
import java.util.UUID;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

/**
 * The OverworldResultDTO.class contains all the info that is sent to the Overworld-backend.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class OverworldResultDTO {

    /**
     * The name of the minigame. In this case "FINITEQUIZ".
     */
    @NotNull(message = "game cannot be null")
    final String game = "FINITEQUIZ";

    /**
     * The ID of the configuration that was used for the game.
     */
    @NotNull(message = "configurationId cannot be null")
    UUID configurationId;

    /**
     * The score achieved in the game.
     */
    @Min(value = Constants.MIN_SCORE, message = "Score cannot be less than " + Constants.MIN_SCORE)
    @Max(value = Constants.MAX_SCORE, message = "Score cannot be higher than " + Constants.MAX_SCORE)
    long score;

    /**
     * The ID of the user that played the game.
     */
    @NotNull(message = "user cannot be null")
    @NotBlank(message = "user cannot be blank")
    String userId;

    /**
     * The reward-coins that the player achieved in the current round.
     */
    @Min(value = Constants.MIN_REWARDS, message = "Rewards cannot be less than " + Constants.MIN_REWARDS)
    @Max(value = Constants.MAX_REWARDS, message = "Rewards cannot be higher than " + Constants.MAX_REWARDS)
    int rewards;
}
