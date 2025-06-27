package dao;

import entity.Result;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data access object for managing test result entities.
 */
public class ResultDao implements IResultDao {
    private static final Logger logger = LoggerFactory.getLogger(ResultDao.class);
    private final JsonFileDao<Result> baseDao;
    private Map<UUID, Result> resultMap;
    Map<UUID, List<Result>> resultsByUserIdMap;
    Map<UUID, List<Result>> resultsByTestIdMap;

    public ResultDao(JsonFileDao<Result> baseDao) {
        this.baseDao = baseDao;
        loadResultsInCache();
    }

    private void loadResultsInCache() {
        List<Result> all = baseDao.findAll();
        resultMap = new ConcurrentHashMap<>();
        resultsByUserIdMap = new ConcurrentHashMap<>();
        resultsByTestIdMap = new ConcurrentHashMap<>();
        for (Result result : all) {
            resultMap.put(result.getId(), result);
            resultsByUserIdMap.computeIfAbsent(result.getUser_id(), k -> new CopyOnWriteArrayList<>()).add(result);
            resultsByTestIdMap.computeIfAbsent(result.getTest_id(), k -> new CopyOnWriteArrayList<>()).add(result);
        }
        logger.info("Results cache refreshed");
    }


    /**
     * Saves a result and updates all caches.
     */
    public void save(Result result) {
        baseDao.add(result);
        resultMap.put(result.getId(), result);
        resultsByTestIdMap.computeIfAbsent(result.getTest_id(), k -> new CopyOnWriteArrayList<>()).add(result);
        resultsByUserIdMap.computeIfAbsent(result.getUser_id(), k -> new CopyOnWriteArrayList<>()).add(result);

    }

    /**
     * Returns all results for a given user id.
     */
    public List<Result> getAllResultsByUserId(UUID id) {
        return resultsByUserIdMap.getOrDefault(id, Collections.emptyList());
    }

    /**
     * Returns all results for a given test id.
     */
    public List<Result> getAllResultsByTestId(UUID id) {
        return resultsByTestIdMap.getOrDefault(id, Collections.emptyList());
    }


    /**
     * Finds a result by its id.
     */
    public Optional<Result> findById(UUID resultId) {
        return Optional.ofNullable(resultMap.get(resultId));
    }

    /**
     * Returns the total number of results.
     */
    public Integer getCount() {
        return resultMap.size();
    }
}
