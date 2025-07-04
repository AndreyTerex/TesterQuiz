
package services;

import dao.UserDao;
import dto.UserDTO;
import entity.User;
import exceptions.AuthenticationException;
import exceptions.DataAccessException;
import exceptions.RegistrationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import validators.ValidatorUserService;
import validators.ValidatorUtil;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static testdata.TestDataBuilders.userDTO;
import static testdata.TestDataBuilders.userEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private ValidatorUserService validatorUserService;

    private UserService userService;

    @BeforeAll
    static void beforeAll() {
        ValidatorUtil.init();
    }

    @AfterAll
    static void afterAll() {
        ValidatorUtil.close();
    }

    @BeforeEach
    void setUp() {
        userService = new UserService(userDao, encoder, validatorUserService);
    }

    @Nested
    @DisplayName("Registration Tests")
    class RegisterUserTests {

        @Test
        @DisplayName("Should register user successfully with valid data")
        void validRegistration() throws DataAccessException {
            // ARRANGE
            String username = "tester";
            String password = "password123";
            UserDTO inputUserDTO = userDTO(null, username, null);

            doNothing().when(validatorUserService).validateUserDto(inputUserDTO);
            doNothing().when(validatorUserService).validatePassword(password);
            when(userDao.findByUsername(username)).thenReturn(Optional.empty());
            when(encoder.encode(password)).thenReturn("encoded_password_hash");
            doNothing().when(userDao).add(any(User.class));

            // ACT
            UserDTO result = userService.registerUser(inputUserDTO, password);

            // ASSERT
            assertNotNull(result);
            assertEquals(username, result.getUsername());
            assertEquals("USER", result.getRole());
            verify(validatorUserService).validateUserDto(inputUserDTO);
            verify(validatorUserService).validatePassword(password);
            verify(userDao).findByUsername(username);
            verify(encoder).encode(password);
            verify(userDao).add(any(User.class));
        }

        @Test
        @DisplayName("Should throw RegistrationException when username already exists")
        void registerUserUsernameExists() throws DataAccessException {
            // ARRANGE
            String username = "existingUser";
            String password = "password123";
            UserDTO inputUserDTO = userDTO(null, username, null);
            User existingUser = userEntity(UUID.randomUUID(), username, "pass", "USER");

            doNothing().when(validatorUserService).validateUserDto(inputUserDTO);
            doNothing().when(validatorUserService).validatePassword(password);
            when(userDao.findByUsername(username)).thenReturn(Optional.of(existingUser));

            // ACT & ASSERT
            RegistrationException exception = assertThrows(
                RegistrationException.class,
                () -> userService.registerUser(inputUserDTO, password)
            );

            assertTrue(exception.getMessage().contains(username));
            verify(validatorUserService).validateUserDto(inputUserDTO);
            verify(validatorUserService).validatePassword(password);
            verify(userDao).findByUsername(username);
            verify(userDao, never()).add(any(User.class));
        }

        @Test
        @DisplayName("Should throw RegistrationException when DAO error")
        void registerUserDaoError() throws DataAccessException {
            // ARRANGE
            String username = "tester";
            String password = "password123";
            UserDTO inputUserDTO = userDTO(null, username, null);

            doNothing().when(validatorUserService).validateUserDto(inputUserDTO);
            doNothing().when(validatorUserService).validatePassword(password);
            when(userDao.findByUsername(username)).thenReturn(Optional.empty());
            when(encoder.encode(password)).thenReturn("encoded_password_hash");
            doThrow(new DataAccessException("DB error")).when(userDao).add(any(User.class));

            // ACT & ASSERT
            assertThrows(
                RegistrationException.class,
                () -> userService.registerUser(inputUserDTO, password)
            );

            verify(validatorUserService).validateUserDto(inputUserDTO);
            verify(validatorUserService).validatePassword(password);
            verify(userDao).findByUsername(username);
            verify(encoder).encode(password);
            verify(userDao).add(any(User.class));
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully with valid credentials")
        void loginValidCredentials() throws DataAccessException {
            // ARRANGE
            String username = "tester";
            String rawPassword = "password123";
            String encodedPassword = "encoded_password_hash";
            User userFromDb = userEntity(UUID.randomUUID(), username, encodedPassword, "USER");

            doNothing().when(validatorUserService).validateUsernameAndPassword(username, rawPassword);
            when(userDao.findByUsername(username)).thenReturn(Optional.of(userFromDb));
            when(encoder.matches(rawPassword, encodedPassword)).thenReturn(true);

            // ACT
            UserDTO result = userService.login(username, rawPassword);

            // ASSERT
            assertNotNull(result);
            assertEquals(username, result.getUsername());
            assertEquals(userFromDb.getId(), result.getId());
            verify(validatorUserService).validateUsernameAndPassword(username, rawPassword);
            verify(userDao).findByUsername(username);
            verify(encoder).matches(rawPassword, encodedPassword);
        }

        @Test
        @DisplayName("Should throw AuthenticationException when user is not found")
        void loginUserNotFound() throws DataAccessException {
            // ARRANGE
            String username = "nonExistentUser";
            String password = "password123";
            doNothing().when(validatorUserService).validateUsernameAndPassword(username, password);
            when(userDao.findByUsername(username)).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThrows(
                AuthenticationException.class,
                () -> userService.login(username, password)
            );
            verify(validatorUserService).validateUsernameAndPassword(username, password);
            verify(userDao).findByUsername(username);
            verifyNoInteractions(encoder);
        }

        @Test
        @DisplayName("Should throw AuthenticationException for wrong password")
        void loginWrongPassword() throws DataAccessException {
            // ARRANGE
            String username = "tester";
            String wrongPassword = "wrongPassword";
            String encodedPassword = "encoded_password_hash";
            User userFromDb = userEntity(null, username, encodedPassword, null);

            doNothing().when(validatorUserService).validateUsernameAndPassword(username, wrongPassword);
            when(userDao.findByUsername(username)).thenReturn(Optional.of(userFromDb));
            when(encoder.matches(wrongPassword, encodedPassword)).thenReturn(false);

            // ACT & ASSERT
            assertThrows(
                AuthenticationException.class,
                () -> userService.login(username, wrongPassword)
            );
            verify(validatorUserService).validateUsernameAndPassword(username, wrongPassword);
            verify(userDao).findByUsername(username);
            verify(encoder).matches(wrongPassword, encodedPassword);
        }

        @Test
        @DisplayName("Should block user after 5 failed login attempts")
        void loginBlocksAfterMaxFailedAttempts() throws DataAccessException {
            // ARRANGE
            String username = "bruteUser";
            String wrongPassword = "wrong";
            String encodedPassword = "encoded_password_hash";
            User userFromDb = userEntity(UUID.randomUUID(), username, encodedPassword, "USER");

            doNothing().when(validatorUserService).validateUsernameAndPassword(username, wrongPassword);
            when(userDao.findByUsername(username)).thenReturn(Optional.of(userFromDb));
            when(encoder.matches(wrongPassword, encodedPassword)).thenReturn(false);

            // ACT & ASSERT
            for (int i = 0; i < 5; i++) {
                assertThrows(AuthenticationException.class, () -> userService.login(username, wrongPassword));
            }
            AuthenticationException ex = assertThrows(AuthenticationException.class, () -> userService.login(username, wrongPassword));
            assertTrue(ex.getMessage().toLowerCase().contains("temporarily locked"));
        }
    }
}
