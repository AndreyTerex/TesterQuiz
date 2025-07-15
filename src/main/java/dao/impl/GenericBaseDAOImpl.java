package dao.impl;

import dao.GenericBaseDAO;
import exceptions.DataAccessException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import util.HibernateSessionManager;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class GenericBaseDAOImpl<T, K extends Serializable> implements GenericBaseDAO<T, K> {
    private final Class<T> entityClass;

    public GenericBaseDAOImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        log.debug("Saving entity: {}", entity);
        execute(session -> session.persist(entity), "Failed to save entity: " + entity);
        log.debug("Entity saved successfully: {}", entity);
        return entity;
    }

    @Override
    public void update(T entity) {
        log.debug("Updating entity: {}", entity);
        T updatedEntity = executeWithResult(session -> session.merge(entity), "Failed to update entity: " + entity);
        log.debug("Entity updated successfully: {}", updatedEntity);
    }

    @Override
    public void delete(T entity) {
        log.debug("Deleting entity: {}", entity);
        execute(session -> session.remove(entity), "Failed to delete entity: " + entity);
        log.debug("Entity deleted successfully: {}", entity);
    }

    @Override
    public Optional<T> findById(K id) {
        log.debug("Finding {} with id: {}", entityClass.getSimpleName(), id);
        return executeWithResult(session -> Optional.ofNullable(session.find(entityClass, id)),
                "Failed to find entity of type " + entityClass.getSimpleName() + " with id: " + id);
    }

    @Override
    public List<T> findAll() {
        log.debug("Finding all entities of type {}", entityClass.getSimpleName());
        return executeWithResult(session -> session.createQuery("FROM " + entityClass.getName(), entityClass).list(),
                "Failed to find all entities of type " + entityClass.getSimpleName());
    }

    @Override
    public void deleteById(K id) {
        log.debug("Deleting entity by id : {}", id);
        findById(id).ifPresent(this::delete);
    }

    protected void execute(Consumer<Session> operation, String errorMessage) {
        try {
            Session session = HibernateSessionManager.getSession();
            operation.accept(session);
        } catch (Exception e) {
            log.error(errorMessage, e);
            throw new DataAccessException(errorMessage, e);
        }
    }

    protected <R> R executeWithResult(Function<Session, R> operation, String errorMessage) {
        try {
            Session session = HibernateSessionManager.getSession();
            return operation.apply(session);
        } catch (Exception e) {
            log.error(errorMessage, e);
            throw new DataAccessException(errorMessage, e);
        }
    }
}
