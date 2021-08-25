package com.mts.lts.dao;

import com.mts.lts.domain.News;
import com.mts.lts.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByName(String tag);
}
