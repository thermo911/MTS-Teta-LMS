package com.mts.lts.service;

import com.mts.lts.domain.Course;
import com.mts.lts.domain.User;
import com.mts.lts.repository.CourseRepository;
import com.mts.lts.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CourseAssignServiceTest {

    private UserRepository userRepositoryMock;
    private CourseRepository courseRepositoryMock;
    private CourseAssignService courseAssignService;
    private static User user;
    private static Course course;

    private static final long userId = 2;
    private static final long courseId = 4;

    @BeforeAll
    static void setUp() {
        user = new User();
        user.setId(userId);
        course = new Course();
        course.setId(courseId);
    }

    @BeforeEach
    public void setUpEach() {
        userRepositoryMock = Mockito.mock(UserRepository.class);
        courseRepositoryMock = Mockito.mock(CourseRepository.class);
        Mockito.when(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(courseRepositoryMock.findById(courseId)).thenReturn(Optional.of(course));
        courseAssignService = new CourseAssignService(userRepositoryMock, courseRepositoryMock);
        user.setCourses(new HashSet<>());
        course.setUsers(new HashSet<>());
    }

    @Test
    public void assignToCourseNotAssignedTest() {
        assertFalse(course.getUsers().contains(user));
        courseAssignService.assignToCourse(userId, courseId);
        Mockito.verify(courseRepositoryMock, Mockito.times(1)).save(course);
        assertTrue(course.getUsers().contains(user));
    }

    @Test
    public void assignToCourseAlreadyAssignedTest() {
        courseAssignService.assignToCourse(userId, courseId);
        assertTrue(course.getUsers().contains(user));
        courseAssignService.assignToCourse(userId, courseId);
        Mockito.verify(courseRepositoryMock, Mockito.times(2)).save(course);
        assertTrue(course.getUsers().contains(user));
    }

    @Test
    public void unassignToCourseAssignedTest() {
        courseAssignService.assignToCourse(userId, courseId);
        assertTrue(course.getUsers().contains(user));
        courseAssignService.unassignToCourse(userId, courseId);
        Mockito.verify(courseRepositoryMock, Mockito.times(2)).save(course);
        assertFalse(course.getUsers().contains(user));
    }

    @Test
    public void unassignToCourseNotAssignedTest() {
        assertFalse(course.getUsers().contains(user));
        courseAssignService.unassignToCourse(userId, courseId);
        Mockito.verify(courseRepositoryMock, Mockito.times(1)).save(course);
        assertFalse(course.getUsers().contains(user));
    }
}
