package dao;

import entity.Result;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ResultDao {
    private final JsonFileDao<Result> baseDao;

    public ResultDao(JsonFileDao<Result> baseDao) {
        this.baseDao = baseDao;
    }

    /**
     * Сохраняет результат прохождения теста
     */
    public void save(Result result, String realPath) throws IOException {
        File directory = new File(realPath);
        baseDao.saveToUniqueFile(result, directory,String.valueOf(result.getId()));
        baseDao.add(result);
    }

    public List<Result> getAllResultsByUserId(UUID id) throws IOException {
       return baseDao.findAll().stream()
               .filter(result -> result.getUser_id().equals(id))
               .collect(Collectors.toList());
    }


    public Result findById(UUID resultId) throws IOException {
        return baseDao.findAll().stream()
                .filter(result -> result.getId().equals(resultId)).findFirst().orElse(null);
    }

    public List<Result> findAll() throws IOException {
        return baseDao.findAll();
    }
}
