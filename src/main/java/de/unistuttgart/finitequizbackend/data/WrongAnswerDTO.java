package de.unistuttgart.finitequizbackend.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class WrongAnswerDTO {


    @NotNull(message = "UUID cannot be null")
    UUID uuid;


    @NotNull(message = "Text cannot be null")
    @NotBlank(message = "Text cannot be blank")
    String text;
}
