package dao;

import entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserDAO extends GenericBaseDAO<User, UUID> {

    /**
     * Finds a user by their unique username.
     */
    Optional<User> findByUsername(String username);

}
