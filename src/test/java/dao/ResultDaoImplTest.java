package dao;

import dao.impl.JsonFileDaoImpl;
import dao.impl.ResultDaoImpl;
import entity.Result;
import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ResultDaoImpl CRUD and cache Tests")
class ResultDaoImplTest {
    private JsonFileDao<Result> baseDao;
    private ResultDaoImpl resultDaoImpl;

    @BeforeEach
    void setUp() {
        baseDao = mock(JsonFileDaoImpl.class);
        when(baseDao.findAll()).thenReturn(new ArrayList<>());
        resultDaoImpl = new ResultDaoImpl(baseDao);
    }

    @Test
    @DisplayName("Should save and find result by id")
    void saveAndFindById() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        Result result = Result.builder().id(id).userId(UUID.randomUUID()).testId(UUID.randomUUID()).build();
        // ACT
        resultDaoImpl.save(result);
        Optional<Result> found = resultDaoImpl.findById(id);
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
        Result r1 = Result.builder().id(UUID.randomUUID()).userId(userId).testId(UUID.randomUUID()).build();
        Result r2 = Result.builder().id(UUID.randomUUID()).userId(userId).testId(UUID.randomUUID()).build();
        resultDaoImpl.save(r1);
        resultDaoImpl.save(r2);
        // ACT
        List<Result> results = resultDaoImpl.getAllResultsByUserId(userId);
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
        Result r1 = Result.builder().id(UUID.randomUUID()).userId(UUID.randomUUID()).testId(testId).build();
        Result r2 = Result.builder().id(UUID.randomUUID()).userId(UUID.randomUUID()).testId(testId).build();
        resultDaoImpl.save(r1);
        resultDaoImpl.save(r2);
        // ACT
        List<Result> results = resultDaoImpl.getAllResultsByTestId(testId);
        // ASSERT
        assertEquals(2, results.size());
        assertTrue(results.contains(r1));
        assertTrue(results.contains(r2));
    }

    @Test
    @DisplayName("Should return empty for non-existent id")
    void findByIdNotFound() {
        // ACT
        Optional<Result> found = resultDaoImpl.findById(UUID.randomUUID());
        // ASSERT
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should return correct count")
    void getCount() {
        // ARRANGE
        Result r1 = Result.builder().id(UUID.randomUUID()).userId(UUID.randomUUID()).testId(UUID.randomUUID()).build();
        Result r2 = Result.builder().id(UUID.randomUUID()).userId(UUID.randomUUID()).testId(UUID.randomUUID()).build();
        resultDaoImpl.save(r1);
        resultDaoImpl.save(r2);
        // ACT
        int count = resultDaoImpl.getCount();
        // ASSERT
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Should load results from baseDao on init")
    void loadResultsInCacheOnInit() {
        // ARRANGE
        UUID userId = UUID.randomUUID();
        UUID testId = UUID.randomUUID();
        Result r1 = Result.builder().id(UUID.randomUUID()).userId(userId).testId(testId).build();
        Result r2 = Result.builder().id(UUID.randomUUID()).userId(userId).testId(testId).build();
        List<Result> results = List.of(r1, r2);
        when(baseDao.findAll()).thenReturn(results);
        // ACT
        ResultDaoImpl dao = new ResultDaoImpl(baseDao);
        // ASSERT
        assertEquals(2, dao.getAllResultsByUserId(userId).size());
        assertEquals(2, dao.getAllResultsByTestId(testId).size());
        assertEquals(2, dao.getCount());
    }

    @Test
    @DisplayName("Should return empty list for unknown user or test id")
    void getAllResultsByUnknownId() {
        // ACT
        List<Result> byUser = resultDaoImpl.getAllResultsByUserId(UUID.randomUUID());
        List<Result> byTest = resultDaoImpl.getAllResultsByTestId(UUID.randomUUID());
        // ASSERT
        assertTrue(byUser.isEmpty());
        assertTrue(byTest.isEmpty());
    }
}
