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
 * The OverworldResultDTO.class contains all the info that is sent to the Overworld-backend
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class OverworldResultDTO {

    @NotNull(message = "game cannot be null")
    String game;

    @NotNull(message = "configurationId cannot be null")
    UUID configurationId;

    @Min(value = Constants.MIN_SCORE, message = "Score cannot be less than " + Constants.MIN_SCORE)
    @Max(value = Constants.MAX_SCORE, message = "Score cannot be higher than " + Constants.MAX_SCORE)
    long score;

    @NotNull(message = "user cannot be null")
    @NotBlank(message = "user cannot be blank")
    String userId;
}
