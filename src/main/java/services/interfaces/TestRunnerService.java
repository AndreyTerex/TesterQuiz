package services.interfaces;

import dto.TestProgressDTO;
import dto.TestSessionDTO;
import dto.UserDTO;

import java.util.UUID;

/**
 * Service for managing the process of a user taking a test.
 */
public interface TestRunnerService {

    /** Starts a test session for a user, providing the first question and end time. */
    TestSessionDTO startTest(UUID id, UserDTO userDTO);

    /** Checks if the test time, provided as an ISO-formatted string, has ended. */
    boolean checkTimeIsEnded(String endTime);

    /** Processes the user's answers to the current question and provides the next one, or finishes the test. */
    TestProgressDTO nextQuestion(TestProgressDTO testProgressDTO);
}
