package dao.impl;

import dao.UserDAO;
import entity.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

@Slf4j
public class UserDAOImpl extends GenericBaseDAOImpl<User, UUID> implements UserDAO {
    public UserDAOImpl() {
        super(User.class);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        log.debug("Searching for user with username: '{}'", username);
        String hql = "FROM User u WHERE u.username = :username";

        Optional<User> userOptional = executeWithResult(session ->
                        session.createQuery(hql, User.class)
                                .setParameter("username", username)
                                .uniqueResultOptional(),
                "Failed to find user by username: " + username
        );

        if (userOptional.isPresent()) {
            log.debug("User with username '{}' found.", username);
        } else {
            log.debug("User with username '{}' not found.", username);
        }

        return userOptional;
    }
}
