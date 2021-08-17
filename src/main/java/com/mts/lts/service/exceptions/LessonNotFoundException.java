package com.mts.lts.service.exceptions;

public class LessonNotFoundException extends RuntimeException {

    private final Long lessonId;

    public LessonNotFoundException(Long lessonId) {
        super(ERROR_MESSAGE + lessonId);
        this.lessonId = lessonId;
    }

    public Long getLessonId() {
        return lessonId;
    }

    private static final String ERROR_MESSAGE = "Lesson not found. LessonId: ";
}
