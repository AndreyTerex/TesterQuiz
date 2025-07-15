package services.interfaces;

import dto.ResultDTO;
import dto.TestStatsDTO;
import entity.Result;
import entity.Test;
import entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing and analyzing test results.
 */
public interface ResultService {

    /** Creates a preliminary Result entity when a user starts a test. */
    Result buildStartResultObject(Test test, User user, LocalDateTime startTime);

    /** Builds the final Result entity from a DTO and persists it. */
    void buildAndSaveFinalResult(ResultDTO resultDTO);

    /** Calculates and sets the final score for a given Result entity. */
    Result calculateScoreResult(Result result);

    /** Gets all test results for a specific user, sorted by date descending. */
    List<ResultDTO> getAllResultsByUserId(UUID id);

    /** Gathers and calculates statistics across all tests. */
    List<TestStatsDTO> getStats();

    /** Counts the total number of test attempts made across all tests. */
    Long countAttempts();

    /** Finds a single test result by ID, including detailed information. */
    ResultDTO findByIdWithDetails(UUID resultId);
}
