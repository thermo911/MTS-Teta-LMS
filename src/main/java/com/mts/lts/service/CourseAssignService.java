package com.mts.lts.service;

import com.mts.lts.dao.CourseRepository;
import com.mts.lts.domain.Course;
import com.mts.lts.domain.User;
import com.mts.lts.dao.UserRepository;
import com.mts.lts.service.exceptions.CourseNotFoundException;
import com.mts.lts.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CourseAssignService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public CourseAssignService(UserRepository userRepository,
                               CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public void assignToCourse(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        course.getUsers().add(user);
        user.getCourses().add(course);
        courseRepository.save(course);
    }

    public void unassignToCourse(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        user.getCourses().remove(course);
        course.getUsers().remove(user);
        courseRepository.save(course);
    }
}
