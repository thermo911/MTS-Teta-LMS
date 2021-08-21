package com.mts.lts.service;

import com.mts.lts.domain.Course;
import com.mts.lts.dao.CourseRepository;
import com.mts.lts.service.exceptions.CourseNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

import static com.mts.lts.TestUtils.assertEqualsCourse;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@AutoConfigureTestDatabase
public class CourseListerServiceTest {

    @Autowired
    private CourseListerService courseListerService;
    @Autowired
    private CourseRepository courseRepository;

    private Course testCourse1;
    private Course testCourse2;
    private static final String testAuthor1 = "author1";
    private static final String testAuthor2 = "author2";
    private static final String testTitle1 = "title1";
    private static final String testTitle2 = "title2";

    @BeforeEach
    void setUpForEach() {
        testCourse1 = new Course(testAuthor1, testTitle1, Collections.emptyList(),
                Collections.emptySet());
        testCourse2 = new Course(testAuthor2, testTitle2, Collections.emptyList(),
                Collections.emptySet());
        courseRepository.deleteAll();
    }

    @Test
    public void findAllTest() {
        courseRepository.save(testCourse1);
        courseRepository.save(testCourse2);
        List<Course> res = courseListerService.findAll();
        assertEquals(res.size(), 2);
    }

    @Test
    @Transactional
    public void getOneSuccessTest() {
        Course savedCourse = courseRepository.save(testCourse1);
        courseRepository.save(testCourse2);
        Course res = courseListerService.getOne(savedCourse.getId());
        assertEqualsCourse(res, testCourse1);
    }

    @Test
    @Transactional
    public void findByIdSuccessTest() {
        Course savedCourse = courseRepository.save(testCourse1);
        courseRepository.save(testCourse2);
        Course res = courseListerService.findById(savedCourse.getId());
        assertEqualsCourse(res, testCourse1);
    }

    @Test
    public void findByIdFailTest() {
        assertThrows(
                CourseNotFoundException.class,
                () -> {
                    Course savedCourse = courseRepository.save(testCourse2);
                    courseListerService.findById(savedCourse.getId() + 1);
                }
        );
    }

    @Test
    @Transactional
    public void saveNewTest() {
        assertTrue(courseRepository.findAll().isEmpty());
        courseListerService.save(testCourse2);
        assertEquals(courseRepository.findAll().size(), 1);
        assertEqualsCourse(courseRepository.findAll().get(0), testCourse2);
    }

    @Test
    @Transactional
    public void saveUpdateTest() {
        Course savedCourse = courseRepository.save(testCourse2);
        assertTrue(courseRepository.findById(savedCourse.getId()).isPresent());
        testCourse2.setTitle(testTitle1);
        courseListerService.save(testCourse2);
        assertTrue(courseRepository.findById(savedCourse.getId()).isPresent());
        assertEqualsCourse(courseRepository.findById(savedCourse.getId()).get(), testCourse2);
    }


    @Test
    public void deleteByIdExistsTest() {
        Course savedCourse = courseRepository.save(testCourse2);
        assertTrue(courseRepository.findById(savedCourse.getId()).isPresent());
        courseListerService.deleteById(savedCourse.getId());
        assertFalse(courseRepository.findById(savedCourse.getId()).isPresent());
    }

    @Test
    public void findByTitleWithPrefixTest() {
        courseRepository.save(testCourse1);
        courseRepository.save(testCourse2);
        List<Course> res1 = courseListerService
                .findByTitleWithPrefix(testTitle1.substring(0, testTitle1.length() - 1));
        assertEquals(res1.size(), 2);
        List<Course> res2 = courseListerService.findByTitleWithPrefix(testTitle1);
        assertEquals(res2.size(), 1);
    }
}