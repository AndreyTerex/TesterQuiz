package Services;

import validator.ValidatorUtil;
import dao.IUserDao;
import dto.UserDTO;
import entity.User;
import exceptions.AuthenticationException;
import exceptions.DataAccessException;
import exceptions.RegistrationException;
import exceptions.ValidationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static testdata.TestDataBuilders.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private IUserDao userDao;

    @Mock
    private BCryptPasswordEncoder encoder;

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
        userService = new UserService(userDao, encoder);
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

            when(userDao.findByUsername(username)).thenReturn(Optional.empty());
            when(encoder.encode(password)).thenReturn("encoded_password_hash");
            doNothing().when(userDao).add(any(User.class));

            // ACT
            UserDTO result = userService.registerUser(inputUserDTO, password);

            // ASSERT
            assertNotNull(result);
            assertEquals(username, result.getUsername());
            assertEquals("USER", result.getRole());
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
            User existingUser = User.builder().username(username).id(UUID.randomUUID()).build();

            when(userDao.findByUsername(username)).thenReturn(Optional.of(existingUser));

            // ACT & ASSERT
            RegistrationException exception = assertThrows(
                RegistrationException.class,
                () -> userService.registerUser(inputUserDTO, password)
            );

            assertTrue(exception.getMessage().contains(username));
            verify(userDao).findByUsername(username);
            verify(userDao, never()).add(any(User.class));
        }

        @Test
        @DisplayName("Should throw ValidationException for invalid password")
        void registerUserInvalidPassword() {
            // ARRANGE
            UserDTO inputUserDTO = userDTO(null, "tester", null);

            // ACT & ASSERT
            assertThrows(ValidationException.class, () -> userService.registerUser(inputUserDTO, "1234567"));
            assertThrows(ValidationException.class, () -> userService.registerUser(inputUserDTO, null));
            assertThrows(ValidationException.class, () -> userService.registerUser(inputUserDTO, "   "));

            verifyNoInteractions(userDao);
            verifyNoInteractions(encoder);
        }

        @Test
        @DisplayName("Should throw ValidationException for null UserDTO")
        void registerUserNullUserDTO() {
            assertThrows(
                ValidationException.class,
                () -> userService.registerUser(null, "password123")
            );

            verifyNoInteractions(userDao);
            verifyNoInteractions(encoder);
        }

        @Test
        @DisplayName("Should throw ValidationException for invalid username")
        void registerUserInvalidUsername() {
            // ARRANGE
            String validPassword = "password123";

            // ACT & ASSERT
            UserDTO nullUsernameDTO = userDTO(null, null, null);
            assertThrows(ValidationException.class, () -> userService.registerUser(nullUsernameDTO, validPassword));

            UserDTO emptyUsernameDTO = userDTO(null, "", null);
            assertThrows(ValidationException.class, () -> userService.registerUser(emptyUsernameDTO, validPassword));

            UserDTO blankUsernameDTO = userDTO(null, "   ", null);
            assertThrows(ValidationException.class, () -> userService.registerUser(blankUsernameDTO, validPassword));

            verifyNoInteractions(userDao);
            verifyNoInteractions(encoder);
        }

        @Test
        @DisplayName("Should throw RegistrationException when DAO error")
        void registerUserDaoError() throws DataAccessException {
            // ARRANGE
            String username = "tester";
            String password = "password123";
            UserDTO inputUserDTO = userDTO(null, username, null);

            when(userDao.findByUsername(username)).thenReturn(Optional.empty());
            when(encoder.encode(password)).thenReturn("encoded_password_hash");
            doThrow(new DataAccessException("DB error")).when(userDao).add(any(User.class));

            // ACT & ASSERT
            assertThrows(
                RegistrationException.class,
                () -> userService.registerUser(inputUserDTO, password)
            );

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

            when(userDao.findByUsername(username)).thenReturn(Optional.of(userFromDb));
            when(encoder.matches(rawPassword, encodedPassword)).thenReturn(true);

            // ACT
            UserDTO result = userService.login(username, rawPassword);

            // ASSERT
            assertNotNull(result);
            assertEquals(username, result.getUsername());
            assertEquals(userFromDb.getId(), result.getId());
            verify(userDao).findByUsername(username);
            verify(encoder).matches(rawPassword, encodedPassword);
        }

        @Test
        @DisplayName("Should throw AuthenticationException when user is not found")
        void loginUserNotFound() throws DataAccessException {
            // ARRANGE
            when(userDao.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThrows(
                AuthenticationException.class,
                () -> userService.login("nonExistentUser", "password123")
            );

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

            when(userDao.findByUsername(username)).thenReturn(Optional.of(userFromDb));
            when(encoder.matches(wrongPassword, encodedPassword)).thenReturn(false);

            // ACT & ASSERT
            assertThrows(
                AuthenticationException.class,
                () -> userService.login(username, wrongPassword)
            );

            verify(userDao).findByUsername(username);
            verify(encoder).matches(wrongPassword, encodedPassword);
        }

        @Test
        @DisplayName("Should throw ValidationException for invalid input")
        void loginInvalidInput() {
            // ARRANGE & ACT & ASSERT
            assertThrows(ValidationException.class, () -> userService.login(null, "password"));
            assertThrows(ValidationException.class, () -> userService.login("   ", "password"));
            assertThrows(ValidationException.class, () -> userService.login("user", null));
            assertThrows(ValidationException.class, () -> userService.login("user", "   "));

            verifyNoInteractions(userDao);
            verifyNoInteractions(encoder);
        }

        @Test
        @DisplayName("Should block user after 5 failed login attempts")
        void loginBlocksAfterMaxFailedAttempts() throws DataAccessException {
            // ARRANGE
            String username = "bruteUser";
            String wrongPassword = "wrong";
            String encodedPassword = "encoded_password_hash";
            User userFromDb = userEntity(UUID.randomUUID(), username, encodedPassword, "USER");

            when(userDao.findByUsername(username)).thenReturn(Optional.of(userFromDb));
            when(encoder.matches(wrongPassword, encodedPassword)).thenReturn(false);

            // ACT & // ASSERT
            for (int i = 0; i < 5; i++) {
                assertThrows(AuthenticationException.class, () -> userService.login(username, wrongPassword));
            }
            AuthenticationException ex = assertThrows(AuthenticationException.class, () -> userService.login(username, wrongPassword));
            assertTrue(ex.getMessage().toLowerCase().contains("temporarily locked"));
        }

        @Test
        @DisplayName("Should reset failed attempts after successful login")
        void loginResetsFailedAttemptsAfterSuccess() throws DataAccessException {
            // ARRANGE
            String username = "resetUser";
            String wrongPassword = "wrong";
            String correctPassword = "correct";
            String encodedPassword = "encoded_password_hash";
            User userFromDb = userEntity(UUID.randomUUID(), username, encodedPassword, "USER");

            when(userDao.findByUsername(username)).thenReturn(Optional.of(userFromDb));
            when(encoder.matches(wrongPassword, encodedPassword)).thenReturn(false);
            when(encoder.matches(correctPassword, encodedPassword)).thenReturn(true);

            // ACT & // ASSERT
            for (int i = 0; i < 4; i++) {
                assertThrows(AuthenticationException.class, () -> userService.login(username, wrongPassword));
            }
            UserDTO dto = userService.login(username, correctPassword);
            assertNotNull(dto);
            assertEquals(username, dto.getUsername());

            for (int i = 0; i < 4; i++) {
                assertThrows(AuthenticationException.class, () -> userService.login(username, wrongPassword));
            }
        }

        @Test
        @DisplayName("Should throw AuthenticationException with lock message if user is blocked")
        void loginThrowsIfUserBlocked() throws DataAccessException {
            // ARRANGE
            String username = "blockedUser";
            String wrongPassword = "wrong";
            String encodedPassword = "encoded_password_hash";
            User userFromDb = userEntity(UUID.randomUUID(), username, encodedPassword, "USER");

            when(userDao.findByUsername(username)).thenReturn(Optional.of(userFromDb));
            when(encoder.matches(wrongPassword, encodedPassword)).thenReturn(false);

            // ACT & // ASSERT
            for (int i = 0; i < 5; i++) {
                assertThrows(AuthenticationException.class, () -> userService.login(username, wrongPassword));
            }
            AuthenticationException ex = assertThrows(AuthenticationException.class, () -> userService.login(username, wrongPassword));
            assertTrue(ex.getMessage().toLowerCase().contains("temporarily locked"));
        }
    }
}
