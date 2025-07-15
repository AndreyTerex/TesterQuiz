package services;

import dao.UserDAO;
import exceptions.ValidationException;
import mappers.UserMapper;
import services.interfaces.UserService;
import validators.ValidatorUserService;
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

public class UserServiceImpl implements UserService {
    private static final String USERNAME_ALREADY_EXISTS = "User with username '%s' already exists.";
    private static final String DEFAULT_ROLE = "USER";

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long BLOCK_TIME_MILLIS = 10 * 60 * 1000; // 10 minutes
    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private final Map<String, Long> blockedUsers = new ConcurrentHashMap<>();

    private final UserDAO userDao;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ValidatorUserService validatorUserService;
    private final UserMapper userMapper;

    public UserServiceImpl(UserDAO userDao, BCryptPasswordEncoder passwordEncoder, ValidatorUserService validatorUserService, UserMapper userMapper) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.validatorUserService = validatorUserService;
        this.userMapper = userMapper;
    }

    public UserDTO registerUser(UserDTO userDTO, String password) throws DataAccessException {
        validatorUserService.validateUserDto(userDTO);
        validatorUserService.validatePassword(password);
        String username = userDTO.getUsername();

        userDao.findByUsername(username).ifPresent(u -> {
            throw new RegistrationException(String.format(USERNAME_ALREADY_EXISTS, username));
        });

        User userToSave = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(DEFAULT_ROLE)
                .build();

        try {
            User savedUser = userDao.save(userToSave);
            return userMapper.toDTO(savedUser);
        } catch (DataAccessException e) {
            throw new RegistrationException("Failed to register user", e);
        }
    }

    public UserDTO login(String username, String password) throws DataAccessException {
        validatorUserService.validateUsernameAndPassword(username, password);
        checkBlocked(username);

        Optional<User> userOptional = userDao.findByUsername(username);

        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
            resetAttempts(username);
            return userMapper.toDTO(userOptional.get());
        } else {
            recordFailedAttempt(username);
            throw new AuthenticationException("Invalid username or password");
        }
    }

    @Override
    public User findUserById(UUID userId) throws DataAccessException {
        return userDao.findById(userId).orElseThrow(() -> new ValidationException("User not found with id: " + userId));
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
