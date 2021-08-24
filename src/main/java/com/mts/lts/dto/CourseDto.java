package com.mts.lts.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class CourseDto {

    private Long id;

    @NotBlank(message = "Course author has to be filled")
    private String author;

    @NotBlank(message = "Course title has to be filled")
    private String title;

    private boolean hasCoverImage;


    public CourseDto(
            Long id,
            String author,
            String title
    ) {
        this.id = id;
        this.author = author;
        this.title = title;
    }

    public CourseDto(
            Long id,
            String author,
            String title,
            boolean hasCoverImage
    ) {
        this(id, author, title);
        this.hasCoverImage = hasCoverImage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getHasCoverImage() {
        return hasCoverImage;
    }

    public void setHasCoverImage(boolean hasCoverImage) {
        this.hasCoverImage = hasCoverImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CourseDto courseDto = (CourseDto) o;
        return Objects.equals(id, courseDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
