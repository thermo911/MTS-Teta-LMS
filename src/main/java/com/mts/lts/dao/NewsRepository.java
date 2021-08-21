package com.mts.lts.dao;

import com.mts.lts.domain.News;
import com.mts.lts.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByCreatedBy(User createdBy);
}
