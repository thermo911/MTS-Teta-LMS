package com.mts.lts.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class ModuleTreeDto {
    private Long id;

    @NotBlank(message = "Course title has to be filled")
    private String title;

    private List<TopicItemDto> topics;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicItemDto{
        private Long id;
        private String title;
    }

    public ModuleTreeDto(Long id, String title, List<TopicItemDto> topics) {
        this.id = id;
        this.title = title;
        this.topics = topics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ModuleTreeDto moduleDto = (ModuleTreeDto) o;
        return Objects.equals(id, moduleDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
