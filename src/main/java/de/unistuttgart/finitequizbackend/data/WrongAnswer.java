package de.unistuttgart.finitequizbackend.data;

import java.util.UUID;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Embeddable
public class WrongAnswer {

    @NotNull(message = "UUID cannot be null")
    private UUID uuid;

    @NotNull(message = "Text cannot be null")
    @NotBlank(message = "Text cannot be blank")
    private String text;

    public WrongAnswer() {
    }

    public WrongAnswer(UUID uuid, String text) {
        this.uuid = uuid;
        this.text = text;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
