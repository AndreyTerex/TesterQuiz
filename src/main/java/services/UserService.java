package services;

import mappers.UserMapper;
import services.interfaces.UserServiceInterface;
import validators.ValidatorUserService;
import dao.UserDao;
import dto.UserDTO;
import entity.User;
import exceptions.AuthenticationException;
import exceptions.DataAccessException;
import exceptions.RegistrationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserService implements UserServiceInterface {
    private static final String USERNAME_ALREADY_EXISTS = "User with username '%s' already exists.";
    private static final String DEFAULT_ROLE = "USER";

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long BLOCK_TIME_MILLIS = 10 * 60 * 1000; // 10 minutes
    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private final Map<String, Long> blockedUsers = new ConcurrentHashMap<>();

    private final UserDao userDao;
    private final BCryptPasswordEncoder encoder;
    private final ValidatorUserService validatorUserService;
    private final UserMapper userMapper;

    public UserService(UserDao userDao, BCryptPasswordEncoder encoder, ValidatorUserService validatorUserService, UserMapper userMapper) {
        this.userDao = userDao;
        this.encoder = encoder;
        this.validatorUserService = validatorUserService;
        this.userMapper = userMapper;
    }

    /**
     * Registers a new user in the system.
     */
    public UserDTO registerUser(UserDTO userDTO, String password) throws DataAccessException {
        validatorUserService.validateUserDto(userDTO);
        validatorUserService.validatePassword(password);
        String username = userDTO.getUsername();
        Optional<User> userExist = userDao.findByUsername(username);
        if (userExist.isPresent()) {
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
        } catch (DataAccessException e) {
            throw new RegistrationException("Failed to register user", e);
        }
        return userMapper.toDTO(user);
    }

    /**
     * Authenticates a user by username and password.
     */
    public UserDTO login(String username, String password) throws DataAccessException {
       validatorUserService.validateUsernameAndPassword(username, password);
       checkBlocked(username);

        try {
            UserDTO userDTO = userDao.findByUsername(username)
                    .filter(u -> encoder.matches(password, u.getPassword()))
                    .map(userMapper::toDTO)
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
