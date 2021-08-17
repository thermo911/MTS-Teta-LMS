package com.mts.lts.repository;

import com.mts.lts.domain.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Query("from Lesson lesson join lesson.course course where course.id = :courseId")
    List<Lesson> findByCourseId(@Param("courseId") Long courseId);

    @Query("select new com.dmitriijarosh.portal.domain.Lesson(lesson.id, lesson.title, lesson.course)"
            + " from Lesson lesson join lesson.course course"
            + " where course.id = :courseId")
    List<Lesson> findByCourseIdWithoutText(@Param("courseId") Long courseId);
}
