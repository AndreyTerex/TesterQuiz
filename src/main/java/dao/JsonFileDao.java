package dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JsonFileDao<T> {
    private final ObjectMapper objectMapper;
    private final Class<T> type;
    private final String rootName;
    private final File file;

    public JsonFileDao(Class<T> type, String rootName, File file, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.type = type;
        this.rootName = rootName;
        this.file = file;
        ensureFileExists();
    }

    /**
     * Проверяет существование файла и создает его при необходимости
     */
    private void ensureFileExists() {
        if (!file.exists()) {
            try {
                File parent = file.getParentFile();

                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                ObjectNode root = objectMapper.createObjectNode();
                root.putArray(rootName);
                objectMapper.writeValue(file, root);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create file: " + file.getPath(), e);
            }
        }
    }

    /**
     * Получает все объекты из JSON файла
     */
    public List<T> findAll() throws IOException {
        if (!file.exists()) {
            return new ArrayList<>();
        }
        JsonNode tree = objectMapper.readTree(file);
        JsonNode arrNode = tree.get(rootName);
        if (arrNode == null || !arrNode.isArray()) {
            return new ArrayList<>();
        }
        return objectMapper.convertValue(arrNode, objectMapper.getTypeFactory().constructCollectionType(List.class, type));
    }

    /**
     * Записывает весь список объектов в JSON файл
     */
    public void writeAll(List<T> list) throws IOException {
        ObjectNode root = objectMapper.createObjectNode();
        root.set(rootName, objectMapper.valueToTree(list));
        objectMapper.writeValue(file, root);
    }

    /**
     * Добавляет новый объект в JSON файл
     */
    public boolean add(T t) {
        try {
            List<T> list = findAll();
            list.add(t);
            writeAll(list);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Обновляет объект по ID в JSON файле
     */
    public boolean updateById(T newObject, String fieldName, Object fieldValue) throws IOException {
        List<T> list = findAll();
        list.removeIf(item -> {
            try {
                Object value = item.getClass().getDeclaredField(fieldName).get(item);
                return Objects.equals(value, fieldValue);
            } catch (Exception e) {
                return false;
            }
        });
        list.add(newObject);
        writeAll(list);
        return true;
    }

    /**
     * Сохраняет объект в уникальный файл
     */
    public boolean saveToUniqueFile(T object, File directory, String filenamePrefix) throws IOException {
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String filename = filenamePrefix + ".json";
        File output = new File(directory, filename);
        objectMapper.writeValue(output, object);
        return output.exists();
    }

    /**
     * Удаляет уникальный файл
     */
    public boolean deleteUniqueFile(File file) {
        return file.exists() && file.delete();
    }
}
