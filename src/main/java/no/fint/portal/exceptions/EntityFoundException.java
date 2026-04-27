package no.fint.portal.exceptions;

public class EntityFoundException extends RuntimeException {
    public EntityFoundException(String message) {
        super(message);
    }

    public EntityFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
