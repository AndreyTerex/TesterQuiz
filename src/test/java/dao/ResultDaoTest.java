package dao;

import entity.Result;
import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ResultDao CRUD and cache Tests")
class ResultDaoTest {
    private JsonFileDao<Result> baseDao;
    private ResultDao resultDao;

    @BeforeEach
    void setUp() {
        baseDao = mock(JsonFileDao.class);
        when(baseDao.findAll()).thenReturn(new ArrayList<>());
        resultDao = new ResultDao(baseDao);
    }

    @Test
    @DisplayName("Should save and find result by id")
    void saveAndFindById() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        Result result = Result.builder().id(id).user_id(UUID.randomUUID()).test_id(UUID.randomUUID()).build();
        // ACT
        resultDao.save(result);
        Optional<Result> found = resultDao.findById(id);
        // ASSERT
        assertTrue(found.isPresent());
        assertEquals(id, found.get().getId());
        verify(baseDao).add(result);
    }

    @Test
    @DisplayName("Should return all results by user id")
    void getAllResultsByUserId() {
        // ARRANGE
        UUID userId = UUID.randomUUID();
        Result r1 = Result.builder().id(UUID.randomUUID()).user_id(userId).test_id(UUID.randomUUID()).build();
        Result r2 = Result.builder().id(UUID.randomUUID()).user_id(userId).test_id(UUID.randomUUID()).build();
        resultDao.save(r1);
        resultDao.save(r2);
        // ACT
        List<Result> results = resultDao.getAllResultsByUserId(userId);
        // ASSERT
        assertEquals(2, results.size());
        assertTrue(results.contains(r1));
        assertTrue(results.contains(r2));
    }

    @Test
    @DisplayName("Should return all results by test id")
    void getAllResultsByTestId() {
        // ARRANGE
        UUID testId = UUID.randomUUID();
        Result r1 = Result.builder().id(UUID.randomUUID()).user_id(UUID.randomUUID()).test_id(testId).build();
        Result r2 = Result.builder().id(UUID.randomUUID()).user_id(UUID.randomUUID()).test_id(testId).build();
        resultDao.save(r1);
        resultDao.save(r2);
        // ACT
        List<Result> results = resultDao.getAllResultsByTestId(testId);
        // ASSERT
        assertEquals(2, results.size());
        assertTrue(results.contains(r1));
        assertTrue(results.contains(r2));
    }

    @Test
    @DisplayName("Should return empty for non-existent id")
    void findByIdNotFound() {
        // ACT
        Optional<Result> found = resultDao.findById(UUID.randomUUID());
        // ASSERT
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should return correct count")
    void getCount() {
        // ARRANGE
        Result r1 = Result.builder().id(UUID.randomUUID()).user_id(UUID.randomUUID()).test_id(UUID.randomUUID()).build();
        Result r2 = Result.builder().id(UUID.randomUUID()).user_id(UUID.randomUUID()).test_id(UUID.randomUUID()).build();
        resultDao.save(r1);
        resultDao.save(r2);
        // ACT
        int count = resultDao.getCount();
        // ASSERT
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Should load results from baseDao on init")
    void loadResultsInCacheOnInit() {
        // ARRANGE
        UUID userId = UUID.randomUUID();
        UUID testId = UUID.randomUUID();
        Result r1 = Result.builder().id(UUID.randomUUID()).user_id(userId).test_id(testId).build();
        Result r2 = Result.builder().id(UUID.randomUUID()).user_id(userId).test_id(testId).build();
        List<Result> results = List.of(r1, r2);
        when(baseDao.findAll()).thenReturn(results);
        // ACT
        ResultDao dao = new ResultDao(baseDao);
        // ASSERT
        assertEquals(2, dao.getAllResultsByUserId(userId).size());
        assertEquals(2, dao.getAllResultsByTestId(testId).size());
        assertEquals(2, dao.getCount());
    }

    @Test
    @DisplayName("Should return empty list for unknown user or test id")
    void getAllResultsByUnknownId() {
        // ACT
        List<Result> byUser = resultDao.getAllResultsByUserId(UUID.randomUUID());
        List<Result> byTest = resultDao.getAllResultsByTestId(UUID.randomUUID());
        // ASSERT
        assertTrue(byUser.isEmpty());
        assertTrue(byTest.isEmpty());
    }
}
