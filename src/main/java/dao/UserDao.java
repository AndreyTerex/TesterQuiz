package dao;

import entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserDao implements IUserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    private final JsonFileDao<User> baseDao;
    private Map<String, User> userMap;

    public UserDao(JsonFileDao<User> baseDao) {
        this.baseDao = baseDao;
        loadUserCache();
    }


    private void loadUserCache(){
        List<User> allUsers = baseDao.findAll();
        userMap = new ConcurrentHashMap<>();
        for (User user : allUsers) {
            userMap.put(user.getUsername(), user);
        }
        logger.info("Users cache refreshed");

    }
    /**
     * Finds a user by username.
     */
    public Optional<User> findByUsername(String username) {
        if (username == null) { return Optional.empty(); }
        return Optional.ofNullable(userMap.get(username));
    }

    /**
     * Adds a new user to the system and updates the cache.
     */
    public void add(User build) {
        baseDao.add(build);
        userMap.put(build.getUsername(), build);
        logger.debug("User '{}' added to cache.", build.getUsername());
    }
}
