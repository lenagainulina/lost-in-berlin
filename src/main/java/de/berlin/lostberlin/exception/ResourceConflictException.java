package de.berlin.lostberlin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ResourceConflictException extends RuntimeException {
    private String resourceName;
    private String resourceId;

    public ResourceConflictException() {

    }

    public ResourceConflictException(String message) {
        super(message);
    }

    public ResourceConflictException(String message, String resourceName, String id) {
        super(message);
        this.resourceName = resourceName;
        this.resourceId = id;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " " + resourceName + " " + resourceId;
    }
}