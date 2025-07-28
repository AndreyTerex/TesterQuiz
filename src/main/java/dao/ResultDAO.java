package dao;

import entity.Result;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResultDAO extends GenericBaseDAO<Result,UUID> {

    /**
     * Retrieves all test results for a specific user.
     */
    List<Result> getAllResultsByUserId(UUID id);

    /**
     * Counts the total number of test results.
     */
    Long getCount();

    /**
     * Finds a result by its ID, fetching associated details like user, test, and answers.
     */
    Optional<Result> findByIdWithDetails(UUID resultId);

    List<Result> findAllWithDetails();
}
