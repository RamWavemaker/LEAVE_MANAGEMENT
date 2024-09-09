package exceptions;

public class ResultSetProcessingException extends RuntimeException {
  public ResultSetProcessingException(String message, Throwable cause) {
    super(message, cause);
  }

  public ResultSetProcessingException(String message) {
        super(message);
    }

}
