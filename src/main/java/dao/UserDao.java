package dao;

import entity.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> findByUsername(String username);

    void add(User user);
}
