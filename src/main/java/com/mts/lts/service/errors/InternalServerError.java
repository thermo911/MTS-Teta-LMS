package com.mts.lts.service.errors;

public class InternalServerError extends Error {

    private final Exception nestedException;

    public InternalServerError(Exception nestedException) {
        super(ERROR_MESSAGE + nestedException.getMessage());
        this.nestedException = nestedException;
    }

    public Exception getNestedException() {
        return nestedException;
    }

    private static final String ERROR_MESSAGE = "InternalServerError happened because of the next exception: ";
}
