package services;

import dao.UserDAO;
import dto.UserDTO;
import entity.User;
import exceptions.AuthenticationException;
import exceptions.DataAccessException;
import exceptions.RegistrationException;
import exceptions.ValidationException;
import mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import validators.ValidatorUserService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserDAO userDao;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private ValidatorUserService validatorUserService;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userDao, passwordEncoder, validatorUserService, userMapper);
    }

    @Test
    @DisplayName("registerUser registers user successfully")
    void registerUser_success() {
        // ARRANGE
        UserDTO userDTO = UserDTO.builder().username("user").build();
        User user = User.builder().username("user").password("encoded").role("USER").build();
        User savedUser = User.builder().username("user").password("encoded").role("USER").build();
        UserDTO savedUserDTO = UserDTO.builder().username("user").build();
        doNothing().when(validatorUserService).validateUserDto(userDTO);
        doNothing().when(validatorUserService).validatePassword("pass");
        when(userDao.findByUsername("user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userDao.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDTO(savedUser)).thenReturn(savedUserDTO);
        // ACT
        UserDTO result = userService.registerUser(userDTO, "pass");
        // ASSERT
        assertEquals(savedUserDTO, result);
    }

    @Test
    @DisplayName("registerUser throws RegistrationException if username exists")
    void registerUser_throwsIfUsernameExists() {
        // ARRANGE
        UserDTO userDTO = UserDTO.builder().username("user").build();
        doNothing().when(validatorUserService).validateUserDto(userDTO);
        doNothing().when(validatorUserService).validatePassword("pass");
        when(userDao.findByUsername("user")).thenReturn(Optional.of(User.builder().build()));
        // ACT & ASSERT
        assertThrows(RegistrationException.class, () -> userService.registerUser(userDTO, "pass"));
    }

    @Test
    @DisplayName("registerUser throws RegistrationException on DataAccessException")
    void registerUser_throwsOnDataAccess() {
        // ARRANGE
        UserDTO userDTO = UserDTO.builder().username("user").build();
        doNothing().when(validatorUserService).validateUserDto(userDTO);
        doNothing().when(validatorUserService).validatePassword("pass");
        when(userDao.findByUsername("user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userDao.save(any(User.class))).thenThrow(new DataAccessException("fail"));
        // ACT & ASSERT
        assertThrows(RegistrationException.class, () -> userService.registerUser(userDTO, "pass"));
    }

    @Test
    @DisplayName("login returns UserDTO on successful login")
    void login_success() {
        // ARRANGE
        String username = "user";
        String password = "pass";
        String encoded = "encoded";
        User user = User.builder().username(username).password(encoded).build();
        UserDTO userDTO = UserDTO.builder().username(username).build();
        doNothing().when(validatorUserService).validateUsernameAndPassword(username, password);
        when(userDao.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encoded)).thenReturn(true);
        when(userMapper.toDTO(user)).thenReturn(userDTO);
        // ACT
        UserDTO result = userService.login(username, password);
        // ASSERT
        assertEquals(userDTO, result);
    }

    @Test
    @DisplayName("login throws AuthenticationException if password is invalid")
    void login_throwsIfInvalidPassword() {
        // ARRANGE
        String username = "user";
        String password = "pass";
        String encoded = "encoded";
        User user = User.builder().username(username).password(encoded).build();
        doNothing().when(validatorUserService).validateUsernameAndPassword(username, password);
        when(userDao.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encoded)).thenReturn(false);
        // ACT & ASSERT
        assertThrows(AuthenticationException.class, () -> userService.login(username, password));
    }

    @Test
    @DisplayName("login throws AuthenticationException if user not found")
    void login_throwsIfUserNotFound() {
        // ARRANGE
        String username = "user";
        String password = "pass";
        doNothing().when(validatorUserService).validateUsernameAndPassword(username, password);
        when(userDao.findByUsername(username)).thenReturn(Optional.empty());
        // ACT & ASSERT
        assertThrows(AuthenticationException.class, () -> userService.login(username, password));
    }

    @Test
    @DisplayName("login throws AuthenticationException if user is blocked")
    void login_throwsIfBlocked() {
        // ARRANGE
        String username = "user";
        String password = "pass";
        String encoded = "encoded";
        User user = User.builder().username(username).password(encoded).build();
        doNothing().when(validatorUserService).validateUsernameAndPassword(username, password);
        when(userDao.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encoded)).thenReturn(false);

        for (int i = 0; i < 5; i++) {
            try {
                userService.login(username, password);
            } catch (AuthenticationException ignored) {}
        }

        // ACT & ASSERT: теперь пользователь заблокирован
        assertThrows(AuthenticationException.class, () -> userService.login(username, password));
    }

    @Test
    @DisplayName("login throws AuthenticationException after too many failed attempts")
    void login_throwsIfTooManyAttempts() {
        // ARRANGE
        String username = "user";
        String password = "pass";
        String encoded = "encoded";
        User user = User.builder().username(username).password(encoded).build();
        doNothing().when(validatorUserService).validateUsernameAndPassword(username, password);
        when(userDao.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encoded)).thenReturn(false);
        userService = spy(userService);
        for (int i = 0; i < 4; i++) {
            try { userService.login(username, "wrong"); } catch (Exception ignored) {}
        }
        // ACT & ASSERT
        assertThrows(AuthenticationException.class, () -> userService.login(username, password));
    }

    @Test
    @DisplayName("login throws ValidationException if input is invalid")
    void login_throwsIfInvalidInput() {
        // ARRANGE
        String username = "user";
        String password = "pass";
        doThrow(new ValidationException("fail")).when(validatorUserService).validateUsernameAndPassword(username, password);
        // ACT & ASSERT
        assertThrows(ValidationException.class, () -> userService.login(username, password));
    }

    @Test
    @DisplayName("findUserById returns User if found")
    void findUserById_success() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        User user = User.builder().id(id).build();
        when(userDao.findById(id)).thenReturn(Optional.of(user));
        // ACT
        User result = userService.findUserById(id);
        // ASSERT
        assertEquals(user, result);
    }

    @Test
    @DisplayName("findUserById throws ValidationException if user not found")
    void findUserById_throwsIfNotFound() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        when(userDao.findById(id)).thenReturn(Optional.empty());
        // ACT & ASSERT
        assertThrows(ValidationException.class, () -> userService.findUserById(id));
    }
}
