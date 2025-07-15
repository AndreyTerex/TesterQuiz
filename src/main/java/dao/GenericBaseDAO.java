package dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface GenericBaseDAO<T, K extends Serializable>{

    T save(T entity);
    void update(T entity);
    void delete(T entity);
    Optional<T> findById(K id);

    void deleteById(K id);
    List<T> findAll();
    }
