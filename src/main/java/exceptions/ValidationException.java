package exceptions;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * An exception that is thrown when data fails business validation.
 * This exception can store a list of all validation errors
 * so that they can be conveniently displayed to the user.
 */
@Getter
public class ValidationException extends RuntimeException {

    private final List<String> errors;

    /**
     * Constructor for a single error.
     */
    public ValidationException(String message) {
        super(message);
        this.errors = Collections.singletonList(message);
    }

    /**
     * Constructor for multiple errors.
     */
    public ValidationException(List<String> errors) {
        super(errors.isEmpty() ? "Validation failed" : errors.getFirst());
        this.errors = Collections.unmodifiableList(errors);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.errors = Collections.singletonList(message);
    }
}
