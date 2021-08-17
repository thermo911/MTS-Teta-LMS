package com.mts.lts;

import com.mts.lts.domain.Course;
import com.mts.lts.domain.Lesson;
import com.mts.lts.domain.User;
import com.mts.lts.dto.CourseDto;
import com.mts.lts.dto.LessonDto;
import com.mts.lts.dto.UserDto;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {

    private TestUtils() {
    }

    public static void assertEqualsCourse(Course expectedCourse, Course actualCourse) {
        assertEquals(expectedCourse.getId(), actualCourse.getId());
        assertEquals(expectedCourse.getAuthor(), actualCourse.getAuthor());
        assertEquals(expectedCourse.getTitle(), actualCourse.getTitle());
        assertEquals(expectedCourse.getUsers(), actualCourse.getUsers());
        assertEquals(expectedCourse.getLessons(), actualCourse.getLessons());
    }

    public static void assertEqualsCourse(Set<Course> expectedCourses, Set<Course> actualCourses) {
        assertEquals(expectedCourses.size(), actualCourses.size());
        List<Course> sortedExpectedCourses = new LinkedList<>(expectedCourses);
        List<Course> sortedActualCourses = new LinkedList<>(actualCourses);
        for (int i = 0; i < sortedExpectedCourses.size(); i++) {
            assertEqualsCourse(sortedExpectedCourses.get(i), sortedActualCourses.get(i));
        }
    }

    public static void assertEqualsCourseDto(CourseDto expectedCourseDto, CourseDto actualCourseDto) {
        assertEquals(expectedCourseDto.getId(), actualCourseDto.getId());
        assertEquals(expectedCourseDto.getAuthor(), actualCourseDto.getAuthor());
        assertEquals(expectedCourseDto.getTitle(), actualCourseDto.getTitle());
    }

    public static void assertEqualsUser(User expectedUser, User actualUser) {
        assertEquals(expectedUser.getId(), expectedUser.getId());
        assertEquals(expectedUser.getUsername(), expectedUser.getUsername());
        assertEquals(expectedUser.getPassword(), expectedUser.getPassword());
        assertEquals(expectedUser.getCourses(), expectedUser.getCourses());
        assertEquals(expectedUser.getRoles(), expectedUser.getRoles());
    }

    public static void assertEqualsUser(Set<User> expectedUsers, Set<User> actualUsers) {
        assertEquals(expectedUsers.size(), actualUsers.size());
        List<User> sortedExpectedUsers = new LinkedList<>(expectedUsers);
        List<User> sortedActualUsers = new LinkedList<>(actualUsers);
        for (int i = 0; i < sortedExpectedUsers.size(); i++) {
            assertEqualsUser(sortedExpectedUsers.get(i), sortedActualUsers.get(i));
        }
    }

    public static void assertEqualsUserDto(UserDto expectedUserDto, UserDto actualUserDto) {
        assertEquals(expectedUserDto.getId(), expectedUserDto.getId());
        assertEquals(expectedUserDto.getUsername(), expectedUserDto.getUsername());
        assertEquals(expectedUserDto.getPassword(), expectedUserDto.getPassword());
        assertEquals(expectedUserDto.getRoles(), expectedUserDto.getRoles());
    }

    public static void assertEqualsLesson(Lesson expectedLesson, Lesson actualLesson) {
        assertEquals(expectedLesson.getId(), expectedLesson.getId());
        assertEquals(expectedLesson.getText(), expectedLesson.getText());
        assertEquals(expectedLesson.getTitle(), expectedLesson.getTitle());
        assertEqualsCourse(expectedLesson.getCourse(), expectedLesson.getCourse());
    }

    public static void assertEqualsLesson(List<Lesson> expectedLessons, List<Lesson> actualLessons) {
        assertEquals(expectedLessons.size(), actualLessons.size());
        for (int i = 0; i < expectedLessons.size(); i++) {
            assertEqualsLesson(expectedLessons.get(i), actualLessons.get(i));
        }
    }

    public static void assertEqualsLessonDto(LessonDto expectedLessonDto, LessonDto actualLessonDto) {
        assertEquals(expectedLessonDto.getId(), expectedLessonDto.getId());
        assertEquals(expectedLessonDto.getText(), expectedLessonDto.getText());
        assertEquals(expectedLessonDto.getTitle(), expectedLessonDto.getTitle());
        assertEquals(expectedLessonDto.getCourseId(), expectedLessonDto.getCourseId());
    }
}
