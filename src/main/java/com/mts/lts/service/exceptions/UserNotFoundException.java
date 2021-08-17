package com.mts.lts.service.exceptions;

public class UserNotFoundException extends RuntimeException {

    private final Long userid;

    public UserNotFoundException(Long userid) {
        super(ERROR_MESSAGE + userid);
        this.userid = userid;
    }

    public Long getUserId() {
        return userid;
    }

    private static final String ERROR_MESSAGE = "User not found. UserId: ";
}
