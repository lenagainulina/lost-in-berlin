package de.berlin.lostberlin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLIntegrityConstraintViolationException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityNotUniqueException extends RuntimeException {
    public EntityNotUniqueException() {
    }

    public EntityNotUniqueException(String reason) {
        super(reason);
    }

}
