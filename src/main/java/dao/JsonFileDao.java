package dao;

import java.io.File;
import java.util.List;

public interface JsonFileDao<T> {
    List<T> findAll();

    void writeAll(List<T> list);

    void add(T t);

    void saveToUniqueFile(T object, File directory, String filenamePrefix);

    void deleteUniqueFile(File file);
}
