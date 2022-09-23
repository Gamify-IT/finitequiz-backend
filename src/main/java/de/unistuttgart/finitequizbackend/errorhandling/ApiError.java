package de.unistuttgart.finitequizbackend.errorhandling;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public final class ApiError {

    private final HttpStatus status;
    private final List<String> errors;

    public ApiError(final HttpStatus status, final List<String> errors) {
        this.status = status;
        this.errors = errors;
    }
}
