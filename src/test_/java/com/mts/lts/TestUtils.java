package com.mts.lts;

import com.mts.lts.domain.Course;
import com.mts.lts.domain.Module;
import com.mts.lts.domain.Topic;
import com.mts.lts.domain.User;
import com.mts.lts.dto.ModuleDto;
import com.mts.lts.dto.TopicDto;
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
        assertEquals(expectedCourse.getModules(), actualCourse.getModules());
    }

    public static void assertEqualsCourse(Set<Course> expectedCours, Set<Course> actualCours) {
        assertEquals(expectedCours.size(), actualCours.size());
        List<Module> sortedExpectedCours = new LinkedList<>(expectedCours);
        List<Module> sortedActualCours = new LinkedList<>(actualCours);
        for (int i = 0; i < sortedExpectedCours.size(); i++) {
            assertEqualsCourse(sortedExpectedCours.get(i), sortedActualCours.get(i));
        }
    }

    public static void assertEqualsCourseDto(ModuleDto expectedModuleDto, ModuleDto actualModuleDto) {
        assertEquals(expectedModuleDto.getId(), actualModuleDto.getId());
        assertEquals(expectedModuleDto.getAuthor(), actualModuleDto.getAuthor());
        assertEquals(expectedModuleDto.getTitle(), actualModuleDto.getTitle());
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

    public static void assertEqualsLesson(Topic expectedTopic, Topic actualTopic) {
        assertEquals(expectedTopic.getId(), expectedTopic.getId());
        assertEquals(expectedTopic.getText(), expectedTopic.getText());
        assertEquals(expectedTopic.getTitle(), expectedTopic.getTitle());
        assertEqualsCourse(expectedTopic.getCourse(), expectedTopic.getCourse());
    }

    public static void assertEqualsLesson(List<Topic> expectedTopics, List<Topic> actualTopics) {
        assertEquals(expectedTopics.size(), actualTopics.size());
        for (int i = 0; i < expectedTopics.size(); i++) {
            assertEqualsLesson(expectedTopics.get(i), actualTopics.get(i));
        }
    }

    public static void assertEqualsLessonDto(TopicDto expectedTopicDto, TopicDto actualTopicDto) {
        assertEquals(expectedTopicDto.getId(), expectedTopicDto.getId());
        assertEquals(expectedTopicDto.getText(), expectedTopicDto.getText());
        assertEquals(expectedTopicDto.getTitle(), expectedTopicDto.getTitle());
        assertEquals(expectedTopicDto.getCourseId(), expectedTopicDto.getCourseId());
    }
}
