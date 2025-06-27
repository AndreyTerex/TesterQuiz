package Services;

import validator.ValidatorUtil;
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
import java.util.concurrent.ConcurrentHashMap;

public class UserService {
    private static final String USERNAME_ALREADY_EXISTS = "User with username '%s' already exists.";
    private static final String DEFAULT_ROLE = "USER";
    private static final int MIN_PASSWORD_LENGTH = 8;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long BLOCK_TIME_MILLIS = 10 * 60 * 1000; // 10 минут
    private final java.util.Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private final java.util.Map<String, Long> blockedUsers = new ConcurrentHashMap<>();

    private final IUserDao userDao;
    private final BCryptPasswordEncoder encoder;

    public UserService(IUserDao userDao, BCryptPasswordEncoder encoder) {
        this.userDao = userDao;
        this.encoder = encoder;
    }

    /**
     * Registers a new user in the system.
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
     * Authenticates a user by username and password.
     */
    public UserDTO login(String username, String password) throws DataAccessException {
        if(username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new ValidationException("Username or password must not be blank");
        }

        checkBlocked(username);

        try {
            UserDTO userDTO = userDao.findByUsername(username)
                    .filter(u -> encoder.matches(password, u.getPassword()))
                    .map(User::toDTO)
                    .orElseThrow(() -> new AuthenticationException("Invalid username or password"));
            resetAttempts(username);
            return userDTO;
        } catch (AuthenticationException e) {
            recordFailedAttempt(username);
            throw e;
        }
    }

    private void checkBlocked(String username) {
        Long blockedUntil = blockedUsers.get(username);
        if (blockedUntil != null && blockedUntil > System.currentTimeMillis()) {
            throw new AuthenticationException("Account is temporarily locked due to too many failed login attempts. Try again later.");
        }
    }

    private void recordFailedAttempt(String username) {
        int attempts = failedAttempts.getOrDefault(username, 0) + 1;
        failedAttempts.put(username, attempts);
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            blockedUsers.put(username, System.currentTimeMillis() + BLOCK_TIME_MILLIS);
            failedAttempts.remove(username);
            throw new AuthenticationException("Account is temporarily locked due to too many failed login attempts. Try again later.");
        }
    }

    private void resetAttempts(String username) {
        failedAttempts.remove(username);
        blockedUsers.remove(username);
    }
}
