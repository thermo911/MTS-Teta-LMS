package com.mts.lts.dto;

import com.mts.lts.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
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

    private Date updatedAt;

    private Set<Tag> tags;

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

