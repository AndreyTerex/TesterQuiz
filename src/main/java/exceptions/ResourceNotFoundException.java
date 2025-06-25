package exceptions;

// Это исключение можно использовать, когда ресурс (тест, пользователь и т.д.) не найден в базе данных.
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
