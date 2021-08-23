package com.mts.lts.service.exceptions;

public class TopicNotFoundException extends RuntimeException {

    private final Long lessonId;

    public TopicNotFoundException(Long lessonId) {
        super(ERROR_MESSAGE + lessonId);
        this.lessonId = lessonId;
    }

    public Long getLessonId() {
        return lessonId;
    }

    private static final String ERROR_MESSAGE = "Lesson not found. LessonId: ";
}
