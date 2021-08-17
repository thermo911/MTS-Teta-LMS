package com.mts.lts.service.exceptions;

public class CourseNotFoundException extends RuntimeException {

    private final Long courseId;

    public CourseNotFoundException(Long courseId) {
        super(ERROR_MESSAGE + courseId);
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return courseId;
    }

    private static final String ERROR_MESSAGE = "Course not found. CourseId: ";
}
