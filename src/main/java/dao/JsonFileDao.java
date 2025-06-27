package dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import exceptions.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JsonFileDao<T> {
    private static final Logger logger = LoggerFactory.getLogger(JsonFileDao.class);
    private final ObjectMapper objectMapper;
    private final Class<T> type;
    private final String rootName;
    private final File file;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private List<T> cache;

    public JsonFileDao(Class<T> type, String rootName, File file, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.type = type;
        this.rootName = rootName;
        this.file = file;
        ensureFileExists();
    }

    private void ensureFileExists() {
        lock.writeLock().lock();
        try {
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
                    logger.error("Failed to create initial file: {}", file.getAbsolutePath(), e);
                    throw new DataAccessException("Failed to create file: " + file.getPath(), e);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Returns all objects from the storage.
     */
    public List<T> findAll() {
        lock.readLock().lock();
        try {
            if (cache != null) {
                return new ArrayList<>(cache);
            }
            if (!file.exists()) {
                return new ArrayList<>();
            }
            JsonNode tree = objectMapper.readTree(file);
            JsonNode arrNode = tree.get(rootName);
            if (arrNode == null || !arrNode.isArray()) {
                return new ArrayList<>();
            }
            cache = objectMapper.convertValue(arrNode, objectMapper.getTypeFactory().constructCollectionType(List.class, type));
            return new ArrayList<>(cache);
        } catch (IOException e) {
            logger.error("Failed to read items from file: {}", file.getAbsolutePath(), e);
            throw new DataAccessException("Failed to read from file: " + file.getPath(), e);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Writes all objects to the storage.
     */
    public void writeAll(List<T> list) {
        lock.writeLock().lock();
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.set(rootName, objectMapper.valueToTree(list));
            objectMapper.writeValue(file, root);
            cache = null;
        } catch (IOException e) {
            logger.error("Failed to write items to file: {}", file.getAbsolutePath(), e);
            throw new DataAccessException("Failed to write to file: " + file.getPath(), e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Adds a new object to the storage.
     */
    public void add(T t) {
        lock.writeLock().lock();
        try {
            List<T> list = findAll();
            list.add(t);
            writeAll(list);
        } finally {
            lock.writeLock().unlock();
        }
    }


    /**
     * Saves an object to a unique file in the given directory.
     */
    public void saveToUniqueFile(T object, File directory, String filenamePrefix) {
        lock.writeLock().lock();
        try {
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String filename = filenamePrefix + ".json";
            File output = new File(directory, filename);
            objectMapper.writeValue(output, object);
        } catch (IOException e) {
            logger.error("Failed to save to unique file: {}", filenamePrefix, e);
            throw new DataAccessException("Failed to save to unique file: " + filenamePrefix, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Deletes a unique file from the storage.
     */
    public void deleteUniqueFile(File file) {
        lock.writeLock().lock();
        try {
            if (!file.exists()) {
                return;
            }
            if (!file.delete()) {
                logger.error("Failed to delete file: {}", file.getAbsolutePath());
                throw new IOException("Failed to delete file: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Error during file deletion: {}", file.getAbsolutePath(), e);
            throw new DataAccessException("Error during file deletion: " + file.getAbsolutePath(), e);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
