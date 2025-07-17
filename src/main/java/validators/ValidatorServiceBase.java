package validators;

import exceptions.ValidationException;

public class ValidatorServiceBase {

    public  <T> T requireNonNullOrValidation(T obj, String message) {
        if (obj == null) throw new ValidationException(message);
        return obj;
    }
}
