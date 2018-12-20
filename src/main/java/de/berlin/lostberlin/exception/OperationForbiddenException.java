package de.berlin.lostberlin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class OperationForbiddenException extends RuntimeException {

    private String resourceName;
    private String resourceId;

    public OperationForbiddenException() {

    }

    public OperationForbiddenException(String message) {
        super(message);
    }

    public OperationForbiddenException(String message, String resourceName, String id) {
        super(message);
        this.resourceName = resourceName;
        this.resourceId = id;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " " + resourceName + " " + resourceId;
    }

}
