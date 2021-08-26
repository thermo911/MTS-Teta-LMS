package com.mts.lts.service;

import com.mts.lts.dao.CourseRepository;
import com.mts.lts.domain.Course;
import com.mts.lts.service.exceptions.CourseNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseListerService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseListerService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course getOne(Long id) {
        return courseRepository.getOne(id);
    }

    public Course findById(long courseId) {
        return courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException(courseId));
    }

    public void save(Course course) {
        courseRepository.save(course);
    }

    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }

    public List<Course> findByTitleWithPrefix(String prefix) {
        return courseRepository.findByTitleLike(prefix + "%");
    }
}