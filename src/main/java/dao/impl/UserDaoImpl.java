package dao.impl;

import dao.JsonFileDao;
import dao.UserDao;
import entity.User;
import lombok.extern.slf4j.Slf4j;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class UserDaoImpl implements UserDao {
    private final JsonFileDao<User> baseDao;
    private final Map<String, User> userMap;

    public UserDaoImpl(JsonFileDao<User> baseDao) {
        this.baseDao = baseDao;
        userMap = new ConcurrentHashMap<>();
        loadUserCache();
    }


    private void loadUserCache() {
        List<User> allUsers = baseDao.findAll();
        for (User user : allUsers) {
            userMap.put(user.getUsername(), user);
        }
        log.info("Users cache refreshed");

    }

    /**
     * Finds a user by username.
     */
    public Optional<User> findByUsername(String username) {
        log.debug("Attempting to find user by username: {}", username);
        if (username == null) {
            log.warn("Attempt to find user with null username");
            return Optional.empty();
        }
        Optional<User> user = Optional.ofNullable(userMap.get(username));
        user.ifPresent(u -> log.debug("User found: {}", username));
        return user;
    }

    /**
     * Adds a new user to the system and updates the cache.
     */
    public void add(User build) {
        baseDao.add(build);
        userMap.put(build.getUsername(), build);
        log.debug("User '{}' added to cache.", build.getUsername());
    }
}
