package Services;

import Listener.ValidatorUtil;
import dao.IUserDao;
import dto.UserDTO;
import entity.User;
import exceptions.AuthenticationException;
import exceptions.DataAccessException;
import exceptions.RegistrationException;
import exceptions.ValidationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private static final String USERNAME_ALREADY_EXISTS = "User with username '%s' already exists.";
    private static final String DEFAULT_ROLE = "USER";
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final IUserDao userDao;
    private final BCryptPasswordEncoder encoder;

    public UserService(IUserDao userDao, BCryptPasswordEncoder encoder) {
        this.userDao = userDao;
        this.encoder = encoder;
    }

    /**
     * Регистрирует нового пользователя в системе
     */
    public UserDTO registerUser(UserDTO userDTO, String password) throws DataAccessException {
        ValidatorUtil.validate(userDTO);

        if (password == null || password.length() < MIN_PASSWORD_LENGTH || password.isBlank()) {
            throw new ValidationException(List.of("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long and not blank."));
        }
        String username = userDTO.getUsername();
        Optional<User> existingUser = userDao.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new RegistrationException(String.format(USERNAME_ALREADY_EXISTS, username));
        }

        User user = User.builder()
                .username(username)
                .password(encoder.encode(password))
                .id(UUID.randomUUID())
                .role(DEFAULT_ROLE)
                .build();
        try {
            userDao.add(user);
        }catch (DataAccessException e){
            throw new RegistrationException("Failed to register user");
        }
        return user.toDTO();
    }

    /**
     * Выполняет авторизацию пользователя
     */
    public UserDTO login(String username, String password) throws DataAccessException {
        return userDao.findByUsername(username)
                .filter(u -> encoder.matches(password, u.getPassword()))
                .map(User::toDTO)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));
    }
}
