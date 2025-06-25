package dao;

import entity.Test;

import java.util.List;
import java.util.UUID;

public interface ITestDao {
    void saveUniqueTest(Test test);
    List<Test> findAll();
    void deleteById(UUID testId);
    Test findById(UUID uuid);
    boolean existByTitle(String title);
    void save(Test test);
}
