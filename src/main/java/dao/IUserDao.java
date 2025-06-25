package dao;

import entity.User;
import java.util.Optional;

public interface IUserDao {
    Optional<User> findByUsername(String username);
    void add(User user);
}
