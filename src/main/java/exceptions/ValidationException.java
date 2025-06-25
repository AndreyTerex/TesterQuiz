package exceptions;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * Исключение, которое выбрасывается, когда данные не проходят бизнес-валидацию.
 * Это исключение способно хранить в себе список всех ошибок валидации,
 * чтобы их можно было удобно отобразить пользователю.
 */
@Getter
public class ValidationException extends RuntimeException {

    private final List<String> errors;

    /**
     * Конструктор для одной ошибки.
     */
    public ValidationException(String message) {
        super(message);
        this.errors = Collections.singletonList(message);
    }

    /**
     * Конструктор для нескольких ошибок.
     */
    public ValidationException(List<String> errors) {
        super(errors.isEmpty() ? "Validation failed" : errors.getFirst());
        this.errors = Collections.unmodifiableList(errors);
    }

}
