package com.mts.lts.dao;

import com.mts.lts.domain.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByTitleLike(String title);

    @Query("from Module module join module.course course where course.id = :courseId")
    List<Module> findByCourseId(@Param("courseId") Long courseId);

}
