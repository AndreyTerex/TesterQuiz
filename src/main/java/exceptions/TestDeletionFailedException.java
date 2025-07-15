package exceptions;

public class TestDeletionFailedException extends BusinessException {
    public TestDeletionFailedException(String message) {
        super(message);
    }

    public TestDeletionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
