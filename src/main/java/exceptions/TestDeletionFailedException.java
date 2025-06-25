package exceptions;

public class TestDeletionFailedException extends RuntimeException {
    public TestDeletionFailedException(String message) {
        super(message);
    }
}
