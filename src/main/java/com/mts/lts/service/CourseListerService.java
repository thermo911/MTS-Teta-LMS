package com.mts.lts.service;

import com.mts.lts.domain.Course;
import com.mts.lts.repository.CourseRepository;
import com.mts.lts.service.exceptions.CourseNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseListerService {

    private final CourseRepository repository;

    @Autowired
    public CourseListerService(CourseRepository repository) {
        this.repository = repository;
    }

    public List<Course> findAll() {
        return repository.findAll();
    }

    public Course getOne(Long id) {
        return repository.getOne(id);
    }

    public Course findById(long courseId) {
        return repository.findById(courseId).orElseThrow(() -> new CourseNotFoundException(courseId));
    }

    public void save(Course course) {
        repository.save(course);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public List<Course> findByTitleWithPrefix(String prefix) {
        return repository.findByTitleLike(prefix + "%");
    }
}