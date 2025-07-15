package dao;

import entity.Test;

import java.util.Optional;
import java.util.UUID;

public interface TestDAO extends GenericBaseDAO<Test,UUID> {

    /**
     * Checks if a test with the given title already exists.
     */
    boolean existByTitle(String title);

    /**
     * Finds a test by its ID, fetching associated details like creator and questions.
     */
    Optional<Test> findByIdWithDetails(UUID testId);
}