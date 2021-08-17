package com.mts.lts.mapper;

import com.mts.lts.domain.Course;
import com.mts.lts.domain.Lesson;
import com.mts.lts.dto.LessonDto;
import com.mts.lts.service.CourseListerService;
import com.mts.lts.service.LessonListerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Component
public class LessonMapperTest {

    private LessonListerService lessonListerServiceMock;
    private CourseListerService courseListerServiceMock;
    private LessonMapper lessonMapper;
    private static Lesson lesson;
    private static LessonDto lessonDto;
    private static Course course;

    private static final long courseId = 2;
    private static final long lessonId = 3;
    private static final String testText = "text";
    private static final String testTitle = "title";

    @BeforeAll
    public static void setUp() {
        lesson = new Lesson();
        lessonDto = new LessonDto(lessonId, testTitle, testText, courseId);
        lesson.setId(lessonId);
        course = new Course();
        course.setId(courseId);
        lesson.setCourse(course);
        lesson.setText(testText);
        lesson.setTitle(testTitle);
    }

    @BeforeEach
    public void setUpForEach() {
        lessonListerServiceMock = Mockito.mock(LessonListerService.class);
        Mockito.when(lessonListerServiceMock.findById(lessonId)).thenReturn(lesson);
        courseListerServiceMock = Mockito.mock(CourseListerService.class);
        Mockito.when(courseListerServiceMock.getOne(courseId)).thenReturn(course);
        lessonMapper = new LessonMapper(lessonListerServiceMock, courseListerServiceMock);
    }

    @Test
    public void dtoToDomainExistsTest() {
        Lesson convertedLesson = lessonMapper.dtoToDomain(lessonDto);
        assertEquals(convertedLesson, lesson);
    }

    @Test
    public void dtoToDomainNewTest() {
        LessonDto newLessonDto = new LessonDto(null, testTitle, testText, courseId);
        Lesson convertedLesson = lessonMapper.dtoToDomain(newLessonDto);
        assertEquals(convertedLesson.getText(), testText);
        assertEquals(convertedLesson.getTitle(), testTitle);
        assertEquals(convertedLesson.getCourse().getId(), courseId);
        assertNotEquals(convertedLesson.getId(), lesson.getId());
    }

    @Test
    public void domainToDtoTest() {
        LessonDto convertedLessonDto = lessonMapper.domainToDto(lesson);
        assertEquals(convertedLessonDto, lessonDto);
    }
}
