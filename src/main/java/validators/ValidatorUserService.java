package validators;

import dto.UserDTO;
import exceptions.ValidationException;
import util.ValidatorUtil;


public class ValidatorUserService extends ValidatorServiceBase {
    private static final int MIN_PASSWORD_LENGTH = 8;
    public void validateUserDto(UserDTO userDTO) {
        ValidatorUtil.validate(userDTO);
    }
    public void validatePassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.");
        }

        String allowedCharsPattern = "^[a-zA-Zа-яА-ЯёЁ\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]+$";

        if (!password.matches(allowedCharsPattern)) {
            throw new ValidationException("Password contains invalid characters. Only letters, numbers, and special characters are allowed.");
        }
    }

    public void validateUsernameAndPassword(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new ValidationException("Username or password must not be blank");
        }
    }

}
