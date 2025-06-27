package dao;

import exceptions.DataAccessException;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TestDao CRUD and cache Tests")
class TestDaoTest {
    private JsonFileDao<entity.Test> baseDao;
    private TestDao testDao;
    private String realPath;

    @BeforeEach
    void setUp() {
        baseDao = mock(JsonFileDao.class);
        realPath = System.getProperty("java.io.tmpdir");
        when(baseDao.findAll()).thenReturn(new ArrayList<>());
        testDao = new TestDao(baseDao, realPath);
    }

    @Test
    @DisplayName("Should save and find test by id")
    void saveAndFindById() {
        // ARRANGE
        entity.Test test = entity.Test.builder().id(UUID.randomUUID()).title("t1").build();
        // ACT
        testDao.save(test);
        entity.Test found = testDao.findById(test.getId());
        // ASSERT
        assertNotNull(found);
        assertEquals("t1", found.getTitle());
        verify(baseDao).writeAll(anyList());
    }

    @Test
    @DisplayName("Should save unique test and call saveToUniqueFile")
    void saveUniqueTest() {
        // ARRANGE
        entity.Test test = entity.Test.builder().id(UUID.randomUUID()).title("t2").build();
        // ACT
        testDao.saveUniqueTest(test);
        // ASSERT
        verify(baseDao).writeAll(anyList());
        verify(baseDao).saveToUniqueFile(eq(test), any(File.class), eq(test.getId().toString()));
    }

    @Test
    @DisplayName("Should return all tests")
    void findAll() {
        // ARRANGE
        entity.Test test1 = entity.Test.builder().id(UUID.randomUUID()).title("a").build();
        entity.Test test2 = entity.Test.builder().id(UUID.randomUUID()).title("b").build();
        when(baseDao.findAll()).thenReturn(List.of(test1, test2));
        TestDao dao = new TestDao(baseDao, realPath);
        // ACT
        List<entity.Test> all = dao.findAll();
        // ASSERT
        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("Should delete test by id and update cache/baseDao")
    void deleteById() {
        // ARRANGE
        entity.Test test = entity.Test.builder().id(UUID.randomUUID()).title("del").build();
        when(baseDao.findAll()).thenReturn(List.of(test));
        TestDao dao = new TestDao(baseDao, realPath);
        // ACT
        dao.deleteById(test.getId());
        // ASSERT
        verify(baseDao).writeAll(anyList());
        assertNull(dao.findById(test.getId()));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent test")
    void deleteByIdNotFound() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        // ACT & ASSERT
        assertThrows(DataAccessException.class, () -> testDao.deleteById(id));
    }

    @Test
    @DisplayName("Should check existence by title")
    void existByTitle() {
        // ARRANGE
        entity.Test test = entity.Test.builder().id(UUID.randomUUID()).title("exists").build();
        testDao.save(test);
        // ACT & ASSERT
        assertTrue(testDao.existByTitle("exists"));
        assertFalse(testDao.existByTitle("nope"));
    }

    @Test
    @DisplayName("Should handle null title in existByTitle")
    void existByTitleNull() {
        // ACT & ASSERT
        assertFalse(testDao.existByTitle(null));
    }
}
