package dao;

import entity.Result;

import java.io.File;
import java.io.IOException;

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
}
