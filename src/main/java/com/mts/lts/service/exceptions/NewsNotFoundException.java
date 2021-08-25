package com.mts.lts.service.exceptions;

public class NewsNotFoundException extends RuntimeException {
    private final Long newsId;

    public NewsNotFoundException(Long newsId) {
        super(ERROR_MESSAGE + newsId);
        this.newsId = newsId;
    }

    public Long getNewsId() {
        return newsId;
    }

    private static final String ERROR_MESSAGE = "News not found. NewsId: ";
}
