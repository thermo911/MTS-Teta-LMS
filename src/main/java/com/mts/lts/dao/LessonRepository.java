package com.mts.lts.dao;

import com.mts.lts.domain.Lesson;
import com.mts.lts.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByTopic(Topic topic);
}
