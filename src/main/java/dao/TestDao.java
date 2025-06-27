package dao;

import entity.Test;
import exceptions.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TestDao implements ITestDao {
    private static final Logger logger = LoggerFactory.getLogger(TestDao.class);
    private final JsonFileDao<Test> baseDao;
    private final String realPath;
    private Map<UUID,Test> testsMap;

    public TestDao(JsonFileDao<Test> baseDao, String realPath) {
        this.baseDao = baseDao;
        this.realPath = realPath;
        loadTestCache();
    }
    
    private void loadTestCache() {
        List<Test> allTests = baseDao.findAll();
        testsMap = new ConcurrentHashMap<>();
        for(Test test : allTests){
            testsMap.put(test.getId(),test);
        }
        logger.info("Tests cache refreshed");
    }

    /**
     * Saves or updates a test in the cache and base storage.
     */
    public void save(Test build) {
        testsMap.put(build.getId(),build);
        baseDao.writeAll(new ArrayList<>(testsMap.values()));
    }

    /**
     * Saves a test and writes it to a unique file.
     */
    public void saveUniqueTest(Test test) {
        save(test);

        File directory = new File(realPath);
        baseDao.saveToUniqueFile(test, directory, String.valueOf(test.getId()));
        logger.info("Test {} saved to unique file.", test.getId());
    }

    /**
     * Returns all tests from the cache.
     */
    public List<Test> findAll() {
        return new ArrayList<>(testsMap.values());
    }

    /**
     * Deletes a test by id and removes its unique file if exists.
     */
    public void deleteById(UUID testId) {
        Test testToDelete = findById(testId);
        if (testToDelete == null) {
            logger.warn("Attempted to delete a non-existent test with id: {}", testId);
            throw new DataAccessException("Test with id " + testId + " does not exist.");
        }

        if (realPath != null) {
            File uniqueFile = getUniqueFile(testToDelete);
            try {
                if (uniqueFile.exists()) {
                    baseDao.deleteUniqueFile(uniqueFile);
                }
            } catch (Exception e) {
                logger.error("Failed to delete unique file for test {}. Aborting delete operation.", testId, e);
                throw new DataAccessException("Failed to delete unique file for test " + testId, e);
            }
        }
        testsMap.remove(testId);
        baseDao.writeAll(new ArrayList<>(testsMap.values()));
        logger.info("Test {} deleted successfully from the main list.", testId);
    }

    private File getUniqueFile(Test test) {
        return new File(realPath, test.getId().toString() + ".json");
    }

    /**
     * Finds a test by its id.
     */
    public Test findById(UUID uuid) {
        return testsMap.get(uuid);
    }

    /**
     * Checks if a test with the given title exists.
     */
    @Override
    public boolean existByTitle(String title) {
        return testsMap.values().stream().anyMatch(test -> test.getTitle().equals(title));
    }
}
