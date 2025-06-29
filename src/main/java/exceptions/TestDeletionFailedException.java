package exceptions;

public class TestDeletionFailedException extends RuntimeException {
    public TestDeletionFailedException(String message) {
        super(message);
    }

    public TestDeletionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
