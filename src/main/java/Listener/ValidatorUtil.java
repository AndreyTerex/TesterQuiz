package Listener;

import exceptions.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidatorUtil {

    private static ValidatorFactory validatorFactory;

    private ValidatorUtil() {}

    public static void init() {
        if (validatorFactory == null) {
            validatorFactory = Validation.buildDefaultValidatorFactory();
        }
    }

    public static Validator getValidator() {
        if (validatorFactory == null) {
            throw new IllegalStateException("ValidatorFactory has not been initialized.");
        }
        return validatorFactory.getValidator();
    }

    public static void close() {
        if (validatorFactory != null) {
            validatorFactory.close();
        }
    }

    public static <T> void validate(T dto) {
        Validator validator = getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            List<String> errorMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList());
            throw new ValidationException(errorMessages);
        }
    }
}
