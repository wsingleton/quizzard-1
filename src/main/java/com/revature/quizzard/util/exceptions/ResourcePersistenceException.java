package com.revature.quizzard.util.exceptions;

public class ResourcePersistenceException extends RuntimeException {

    public ResourcePersistenceException(Throwable cause) {
        super("The provided resource could not be persisted to the datasource.", cause);
    }

    public ResourcePersistenceException(String msg, Throwable cause) {
        super(msg, cause);
    }

}