package exceptions;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class AuthenticationException extends BusinessException {
    private final List<String> errors;

    public AuthenticationException(String message) {
        super(message);
        this.errors = Collections.singletonList(message);
    }
    public AuthenticationException(List<String> errors) {
        super(errors.isEmpty() ? "Authentication failed" : errors.getFirst());
        this.errors = Collections.unmodifiableList(errors);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
        this.errors = Collections.singletonList(message);
    }
}