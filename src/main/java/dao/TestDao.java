package dao;

import entity.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


public class TestDao {
    private final JsonFileDao<Test> baseDao;

    public TestDao(JsonFileDao<Test> baseDao) {
        this.baseDao = baseDao;
    }

    /**
     * Сохраняет новый тест в базовый файл базы данных.
     */
    public boolean saveNewTestToBaseFile(Test build) throws IOException {
        if(existsByTitle(build.getTitle())){
            return false;
        }
        return baseDao.add(build);
    }
    /**
     * Удаляет старую версию теста и сохраняет новую в базовый файл.
     */
    public boolean removeOldVersionOfTestAndAddToBaseFile(Test build) throws IOException {
        List<Test> list = baseDao.findAll();
        list.removeIf(t -> t.getId().equals(build.getId()));
        list.add(build);
        baseDao.writeAll(list);
        return baseDao.findAll().contains(build);
    }

    /**
     * Сохраняет тест в базу данных и создает уникальный файл для теста.
     */
    public boolean saveUniqueTest(Test build, String realPath) throws IOException {
        File directory = new File(realPath);
        return removeOldVersionOfTestAndAddToBaseFile(build) && baseDao.saveToUniqueFile(build, directory, String.valueOf(build.getId()));
    }

    /**
     * Получает список всех тестов из базы данных.
     */
    public List<Test> findAll() throws IOException {
        return baseDao.findAll();
    }

    /**
     * Удаляет тест из базы данных и соответствующий уникальный файл.
     */
    public boolean deleteFromBaseFileAndUniqueFile(UUID testId, String realPath) throws IOException {
        List<Test> tests = baseDao.findAll();
        Test testToDelete = tests.stream()
                .filter(t -> t.getId().equals(testId))
                .findFirst()
                .orElse(null);

        if (testToDelete == null) {
            return false;
        }

        tests.remove(testToDelete);
        baseDao.writeAll(tests);

        if (realPath == null) {
            return false;
        }

        File directory = new File(realPath);
        File uniqueFile = new File(directory, testToDelete.getId().toString() + ".json");

        if (!uniqueFile.exists()) {
            System.out.println("File does not exist, skipping deletion: " + uniqueFile.getPath());
            return true;
        }

        return baseDao.deleteUniqueFile(uniqueFile);
    }

    /**
     * Находит тест по его уникальному идентификатору.
     */
    public Test findById(UUID uuid) throws IOException {
        List<Test> allTests = baseDao.findAll();
        return allTests.stream()
                .filter(t -> t.getId().equals(uuid))
                .findFirst()
                .orElse(null);
    }
    /**
     * Проверяет, существует ли тест с указанным названием.
     */
    public boolean existsByTitle(String title) throws IOException {
        return findAll().stream()
                .anyMatch(test -> test.getTitle().equals(title));
    }
}
