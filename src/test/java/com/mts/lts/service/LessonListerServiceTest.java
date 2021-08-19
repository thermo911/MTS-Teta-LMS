package com.mts.lts.service;

import com.mts.lts.domain.Course;
import com.mts.lts.domain.Lesson;
import com.mts.lts.dao.CourseRepository;
import com.mts.lts.dao.LessonRepository;
import com.mts.lts.service.exceptions.LessonNotFoundException;
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
import static com.mts.lts.TestUtils.assertEqualsLesson;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@AutoConfigureTestDatabase
public class LessonListerServiceTest {

    @Autowired
    private LessonListerService lessonListerService;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private CourseRepository courseRepository;

    private Lesson testLesson1;
    private Lesson testLesson2;
    private static final String testCourseAuthor = "author";
    private static final String testCourseTitle = "title";
    private static final String testText1 = "text1";
    private static final String testText2 = "text2";
    private static final String testTitle1 = "title1";
    private static final String testTitle2 = "title2";

    @BeforeEach
    void setUpForEach() {
        lessonRepository.deleteAll();
        courseRepository.deleteAll();
        Course course1 = courseRepository.save(
                new Course(
                        testCourseAuthor,
                        testCourseTitle,
                        Collections.emptyList(),
                        Collections.emptySet()
                )
        );
        Course course2 = courseRepository.save(
                new Course(
                        testCourseAuthor,
                        testCourseTitle,
                        Collections.emptyList(),
                        Collections.emptySet()
                )
        );
        testLesson1 = new Lesson(testTitle1, testText1, course1);
        testLesson2 = new Lesson(testTitle2, testText2, course2);
    }

    @Test
    @Transactional
    public void findByIdSuccessTest() {
        Lesson savedLesson = lessonRepository.save(testLesson1);
        lessonRepository.save(testLesson2);
        Lesson res = lessonListerService.findById(savedLesson.getId());
        assertEqualsLesson(res, testLesson1);
    }

    @Test
    public void findByIdFailTest() {
        assertThrows(
                LessonNotFoundException.class,
                () -> {
                    Lesson savedLesson = lessonRepository.save(testLesson2);
                    lessonListerService.findById(savedLesson.getId() + 1);
                }
        );
    }

    @Test
    @Transactional
    public void saveNewTest() {
        assertTrue(lessonRepository.findAll().isEmpty());
        lessonListerService.save(testLesson2);
        assertEquals(lessonRepository.findAll().size(), 1);
        assertEqualsLesson(lessonRepository.findAll().get(0), testLesson2);
    }

    @Test
    @Transactional
    public void saveUpdateTest() {
        Lesson savedLesson = lessonRepository.save(testLesson2);
        assertTrue(lessonRepository.findById(savedLesson.getId()).isPresent());
        testLesson2.setTitle(testTitle1);
        lessonListerService.save(testLesson2);
        assertTrue(lessonRepository.findById(savedLesson.getId()).isPresent());
        assertEqualsLesson(lessonRepository.findById(savedLesson.getId()).get(), testLesson2);
    }


    @Test
    public void deleteByIdExistsTest() {
        Lesson savedLesson = lessonRepository.save(testLesson2);
        assertTrue(lessonRepository.findById(savedLesson.getId()).isPresent());
        lessonListerService.deleteById(savedLesson.getId());
        assertFalse(lessonRepository.findById(savedLesson.getId()).isPresent());
    }

    @Test
    @Transactional
    public void findByCourseIdTest() {
        Lesson savedLesson = lessonRepository.save(testLesson1);
        assertTrue(lessonRepository.findById(savedLesson.getId()).isPresent());
        List<Lesson> res = lessonListerService.findByCourseId(savedLesson.getCourse().getId());
        assertEquals(res.size(), 1);
        assertEqualsLesson(res.get(0), testLesson1);
    }

    @Test
    @Transactional
    public void findByCourseIdWithoutTextTest() {
        Lesson savedLesson = lessonRepository.save(testLesson1);
        assertTrue(lessonRepository.findById(savedLesson.getId()).isPresent());
        List<Lesson> res = lessonListerService
                .findByCourseIdWithoutText(savedLesson.getCourse().getId());
        assertEquals(res.size(), 1);
        Lesson resLesson = res.get(0);
        assertEquals(resLesson.getTitle(), testLesson1.getTitle());
        assertEqualsCourse(resLesson.getCourse(), testLesson1.getCourse());
        assertNull(resLesson.getText());
    }

}
