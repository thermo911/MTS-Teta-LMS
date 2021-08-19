package com.mts.lts.dao;

import com.mts.lts.domain.Course;
import com.mts.lts.domain.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByCourse(Course course);
}
