package dao;

import entity.User;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class UserDao {
    private final JsonFileDao<User> baseDao;

    public UserDao(JsonFileDao<User> baseDao) {
        this.baseDao = baseDao;
    }

    /**
     * Находит пользователя по имени пользователя
     */
    public Optional<User> findByUsername(String username) throws IOException {
        return baseDao.findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    /**
     * Добавляет нового пользователя в систему
     */
    public boolean add(User build) throws IOException {
            return baseDao.add(build);
    }

    /**
     * Находит пользователя по уникальному идентификатору
     */
    public User findById(UUID uuid) throws IOException {
        return baseDao.findAll().stream()
                .filter(user -> user.getId().equals(uuid))
                .findFirst()
                .orElse(null);
    }
}
