package dao;

import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("UserDao CRUD and cache Tests")
class UserDaoTest {
    private JsonFileDao<User> baseDao;
    private UserDao userDao;

    @BeforeEach
    void setUp() {
        baseDao = mock(JsonFileDao.class);
        when(baseDao.findAll()).thenReturn(new ArrayList<>());
        userDao = new UserDao(baseDao);
    }

    @Test
    @DisplayName("Should add and find user by username (cache and baseDao)")
    void addAndFindByUsername() {
        // ARRANGE
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .password("pass")
                .role("USER")
                .build();
        // ACT
        userDao.add(user);
        Optional<User> found = userDao.findByUsername("testuser");
        // ASSERT
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        verify(baseDao).add(user);
    }

    @Test
    @DisplayName("Should return empty for non-existent user")
    void findByUsernameNotFound() {
        // ACT
        Optional<User> found = userDao.findByUsername("nouser");
        // ASSERT
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should load users from baseDao on init")
    void loadUserCacheOnInit() {
        // ARRANGE
        User user1 = User.builder().id(UUID.randomUUID()).username("u1").build();
        User user2 = User.builder().id(UUID.randomUUID()).username("u2").build();
        List<User> users = List.of(user1, user2);
        when(baseDao.findAll()).thenReturn(users);
        // ACT
        UserDao dao = new UserDao(baseDao);
        // ASSERT
        assertTrue(dao.findByUsername("u1").isPresent());
        assertTrue(dao.findByUsername("u2").isPresent());
    }

    @Test
    @DisplayName("Should overwrite user with same username")
    void addDuplicateUsernameOverwrites() {
        // ARRANGE
        User user1 = User.builder().id(UUID.randomUUID()).username("dup").password("1").role("USER").build();
        User user2 = User.builder().id(UUID.randomUUID()).username("dup").password("2").role("ADMIN").build();
        // ACT
        userDao.add(user1);
        userDao.add(user2);
        Optional<User> found = userDao.findByUsername("dup");
        // ASSERT
        assertTrue(found.isPresent());
        assertEquals("2", found.get().getPassword());
        assertEquals("ADMIN", found.get().getRole());
    }

    @Test
    @DisplayName("Should not add null user")
    void addNullUser() {
        // ACT & ASSERT
        assertThrows(NullPointerException.class, () -> userDao.add(null));
    }

    @Test
    @DisplayName("Should return empty for null username")
    void findByNullUsername() {
        // ACT
        Optional<User> found = userDao.findByUsername(null);
        // ASSERT
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should not add user with null username")
    void addUserWithNullUsername() {
        // ARRANGE
        User user = User.builder().id(UUID.randomUUID()).username(null).password("p").role("USER").build();
        // ACT & ASSERT
        assertThrows(NullPointerException.class, () -> userDao.add(user));
    }
}
