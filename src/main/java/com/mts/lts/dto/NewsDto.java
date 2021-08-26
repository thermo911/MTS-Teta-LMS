package com.mts.lts.dto;

import com.mts.lts.domain.Role;
import com.mts.lts.domain.Tag;
import com.mts.lts.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewsDto {
    private Long id;

    private String title;

    private String text;

    private String updatedAt;

    private User updatedBy;

    private Set<Tag> tags;

    public NewsDto(Long id, String title, String text, Set<Tag> tags, User updatedBy) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.tags = tags;
        this.updatedBy = updatedBy;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NewsDto news = (NewsDto) o;
        return Objects.equals(id, news.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}




