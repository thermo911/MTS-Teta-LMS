package com.mts.lts.service;

import com.mts.lts.dao.CourseRepository;
import com.mts.lts.domain.Course;
import com.mts.lts.domain.Module;
import com.mts.lts.domain.User;
import com.mts.lts.dao.ModuleRepository;
import com.mts.lts.dao.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ModuleAssignServiceTest {

    private UserRepository userRepositoryMock;
    private CourseRepository courseRepositoryRepositoryMock;
    private CourseAssignService courseAssignService;
    private static User user;
    private static Course module;

    private static final long userId = 2;
    private static final long courseId = 4;

    @BeforeAll
    static void setUp() {
        user = new User();
        user.setId(userId);
        module = new Course();
        module.setId(courseId);
    }

    @BeforeEach
    public void setUpEach() {
        userRepositoryMock = Mockito.mock(UserRepository.class);
        courseRepositoryRepositoryMock = Mockito.mock(CourseRepository.class);
        Mockito.when(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(courseRepositoryRepositoryMock.findById(courseId)).thenReturn(Optional.of(module));
        courseAssignService = new CourseAssignService(userRepositoryMock, courseRepositoryRepositoryMock);
        user.setCourses(new HashSet<>());
        module.setUsers(new HashSet<>());
    }

    @Test
    public void assignToCourseNotAssignedTest() {
        assertFalse(module.getUsers().contains(user));
        courseAssignService.assignToCourse(userId, courseId);
        Mockito.verify(courseRepositoryRepositoryMock, Mockito.times(1)).save(module);
        assertTrue(module.getUsers().contains(user));
    }

    @Test
    public void assignToCourseAlreadyAssignedTest() {
        courseAssignService.assignToCourse(userId, courseId);
        assertTrue(module.getUsers().contains(user));
        courseAssignService.assignToCourse(userId, courseId);
        Mockito.verify(courseRepositoryRepositoryMock, Mockito.times(2)).save(module);
        assertTrue(module.getUsers().contains(user));
    }

    @Test
    public void unassignToCourseAssignedTest() {
        courseAssignService.assignToCourse(userId, courseId);
        assertTrue(module.getUsers().contains(user));
        courseAssignService.unassignToCourse(userId, courseId);
        Mockito.verify(courseRepositoryRepositoryMock, Mockito.times(2)).save(module);
        assertFalse(module.getUsers().contains(user));
    }

    @Test
    public void unassignToCourseNotAssignedTest() {
        assertFalse(module.getUsers().contains(user));
        courseAssignService.unassignToCourse(userId, courseId);
        Mockito.verify(courseRepositoryRepositoryMock, Mockito.times(1)).save(module);
        assertFalse(module.getUsers().contains(user));
    }
}
