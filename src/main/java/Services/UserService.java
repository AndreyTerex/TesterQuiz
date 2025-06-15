package Services;

import dao.UserDao;
import dto.UserDTO;
import entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final UserDao userDao;
    private final BCryptPasswordEncoder encoder;


    public UserService(UserDao userDao, BCryptPasswordEncoder encoder) {
        this.userDao = userDao;
        this.encoder = encoder;
    }

    /**
     * Регистрирует нового пользователя в системе
     */
    public UserDTO registerUser(UserDTO userDTO, String password) throws IOException {
        String username = userDTO.getUsername();
        try {
            Optional<User> existingUser = userDao.findByUsername(username);
            if (existingUser.isPresent()) {
                return null;
            }

            User user = User.builder()
                    .username(username)
                    .password(encoder.encode(password))
                    .id(UUID.randomUUID())
                    .role("USER")
                    .build();
            boolean success = userDao.add(user);
            if (success) {
                return UserDTO.builder().username(user.getUsername()).role(user.getRole()).id(user.getId()).build();
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error registering user", e);
        }
    }



    /**
     * Выполняет авторизацию пользователя
     */
    public UserDTO login(String username, String password) throws IOException {
        Optional<User> user = userDao.findByUsername(username);
        if (user.isPresent() && encoder.matches(password, user.get().getPassword())) {
            return UserDTO.builder().username(user.get().getUsername()).role(user.get().getRole()).id(user.get().getId()).build();
        } else {
            return null;
        }
    }

    /**
     * Находит пользователя по идентификатору
     */
    public User findById(String string) throws IOException {
        return userDao.findById(UUID.fromString(string));
    }
}


