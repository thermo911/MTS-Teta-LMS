package com.mts.lts.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
public class ModuleDto {

    private Long id;

//    @NotBlank(message = "Course author has to be filled")
    private String author;

    @NotBlank(message = "Course title has to be filled")
    private String title;

    private Long courseId;

    private boolean hasCoverImage;



    public ModuleDto(
            Long id,
            String author,
            String title
    ) {
        this.id = id;
        this.author = author;
        this.title = title;
    }

    public ModuleDto(Long courseId) {
        this.courseId = courseId;
    }

    public ModuleDto(
            Long id,
            String author,
            String title,
            boolean hasCoverImage
    ) {
        this(id, author, title);
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
        ModuleDto moduleDto = (ModuleDto) o;
        return Objects.equals(id, moduleDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
