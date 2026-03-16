package br.com.crud_project.domain.exception;

public class SystemOverloadException extends RuntimeException {
    public SystemOverloadException(String message) {
        super(message);
    }
}
