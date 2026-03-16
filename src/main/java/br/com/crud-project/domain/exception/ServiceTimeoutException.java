package br.com.crud_project.domain.exception;

public class ServiceTimeoutException extends RuntimeException {
    public ServiceTimeoutException(String message) {
        super(message);
    }

    public ServiceTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
