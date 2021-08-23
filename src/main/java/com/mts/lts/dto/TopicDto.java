package com.mts.lts.dto;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class TopicDto {

    private Long id;

    @NotBlank(message = "Lesson title has to be filled")
    private String title;

    @NotBlank(message = "Lesson text has to be filled")
    private String text;

    private Long courseId;

    public TopicDto() {
    }

    public TopicDto(Long courseId) {
        this.courseId = courseId;
    }

    public TopicDto(Long id, String title, String text, Long courseId) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.courseId = courseId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TopicDto topicDto = (TopicDto) o;
        return Objects.equals(id, topicDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
