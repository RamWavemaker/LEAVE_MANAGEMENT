package exceptions;

public class EmptyEmployeeIdsException extends RuntimeException {
    public EmptyEmployeeIdsException(String message) {
        super(message);
    }
}
