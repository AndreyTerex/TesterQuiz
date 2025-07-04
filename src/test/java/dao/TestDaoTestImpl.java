package dao;

import dao.impl.JsonFileDaoImpl;
import dao.impl.TestDaoImpl;
import exceptions.DataAccessException;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TestDaoImpl CRUD and cache Tests")
class TestDaoTestImpl {
    private JsonFileDao<entity.Test> baseDao;
    private TestDaoImpl testDaoImpl;
    private String realPath;

    @BeforeEach
    void setUp() {
        baseDao = mock(JsonFileDaoImpl.class);
        realPath = System.getProperty("java.io.tmpdir");
        when(baseDao.findAll()).thenReturn(new ArrayList<>());
        testDaoImpl = new TestDaoImpl(baseDao, realPath);
    }

    @Test
    @DisplayName("Should save and find test by id")
    void saveAndFindById() {
        // ARRANGE
        entity.Test test = entity.Test.builder().id(UUID.randomUUID()).title("t1").build();
        // ACT
        testDaoImpl.save(test);
        entity.Test found = testDaoImpl.findById(test.getId());
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
        testDaoImpl.saveUniqueTest(test);
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
        TestDaoImpl dao = new TestDaoImpl(baseDao, realPath);
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
        TestDaoImpl dao = new TestDaoImpl(baseDao, realPath);
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
        assertThrows(DataAccessException.class, () -> testDaoImpl.deleteById(id));
    }

    @Test
    @DisplayName("Should check existence by title")
    void existByTitle() {
        // ARRANGE
        entity.Test test = entity.Test.builder().id(UUID.randomUUID()).title("exists").build();
        testDaoImpl.save(test);
        // ACT & ASSERT
        assertTrue(testDaoImpl.existByTitle("exists"));
        assertFalse(testDaoImpl.existByTitle("nope"));
    }

    @Test
    @DisplayName("Should handle null title in existByTitle")
    void existByTitleNull() {
        // ACT & ASSERT
        assertFalse(testDaoImpl.existByTitle(null));
    }
}
