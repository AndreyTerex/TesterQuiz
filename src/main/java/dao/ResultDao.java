package dao;

import dto.ResultDTO;
import entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
            resultsByUserIdMap.computeIfAbsent(result.getUser_id(), _ -> new CopyOnWriteArrayList<>()).add(result);
            resultsByTestIdMap.computeIfAbsent(result.getTest_id(), _ -> new CopyOnWriteArrayList<>()).add(result);
        }
        logger.info("Results cache refreshed");
    }


    public void save(Result result) {
        baseDao.add(result);
        resultMap.put(result.getId(), result);
        resultsByTestIdMap.computeIfAbsent(result.getTest_id(), _ -> new CopyOnWriteArrayList<>()).add(result);
        resultsByUserIdMap.computeIfAbsent(result.getUser_id(), _ -> new CopyOnWriteArrayList<>()).add(result);

    }

    public List<Result> getAllResultsByUserId(UUID id) {
        return resultsByUserIdMap.getOrDefault(id, Collections.emptyList());
    }

    public List<Result> getAllResultsByTestId(UUID id) {
        return resultsByTestIdMap.getOrDefault(id, Collections.emptyList());
    }


    public Optional<ResultDTO> findById(UUID resultId) {
        return Optional.ofNullable(resultMap.get(resultId))
                .map(Result::toDTO);
    }

    public Integer getCount() {
        return resultMap.size();
    }
}
