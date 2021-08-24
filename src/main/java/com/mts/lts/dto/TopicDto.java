package com.mts.lts.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@NoArgsConstructor
@Setter
@Getter
public class TopicDto {

    private Long id;

    @NotBlank(message = "Lesson title has to be filled")
    private String title;

    @NotBlank(message = "Lesson text has to be filled")
    private String text;

    private Long moduleId;


    public TopicDto(Long moduleId) {
        this.moduleId = moduleId;
    }

    public TopicDto(Long id, String title, String text, Long moduleId) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.moduleId = moduleId;
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
