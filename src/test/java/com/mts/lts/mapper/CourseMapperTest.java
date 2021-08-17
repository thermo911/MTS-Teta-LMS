package com.mts.lts.mapper;

import com.mts.lts.domain.Course;
import com.mts.lts.dto.CourseDto;
import com.mts.lts.service.CourseListerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

import static com.mts.lts.TestUtils.assertEqualsCourse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Component
public class CourseMapperTest {

    private CourseListerService courseListerServiceMock;
    private CourseMapper courseMapper;
    private static Course course;
    private static CourseDto courseDto;

    private static final long courseId = 2;
    private static final String testAuthor = "author";
    private static final String testTitle = "title";

    @BeforeAll
    public static void setUp() {
        course = new Course();
        courseDto = new CourseDto(courseId, testAuthor, testTitle);
        course.setId(courseId);
        course.setAuthor(testAuthor);
        course.setTitle(testTitle);
    }

    @BeforeEach
    public void setUpForEach() {
        courseListerServiceMock = Mockito.mock(CourseListerService.class);
        Mockito.when(courseListerServiceMock.getOne(courseId)).thenReturn(course);
        courseMapper = new CourseMapper(courseListerServiceMock);
    }

    @Test
    @Transactional
    public void dtoToDomainExistsTest() {
        Course convertedCourse = courseMapper.dtoToDomain(courseDto);
        assertEqualsCourse(convertedCourse, course);
    }

    @Test
    public void dtoToDomainNewTest() {
        CourseDto newCourseDto = new CourseDto(null, testAuthor, testTitle);
        Course convertedCourse = courseMapper.dtoToDomain(newCourseDto);
        assertEquals(convertedCourse.getAuthor(), testAuthor);
        assertEquals(convertedCourse.getTitle(), testTitle);
        assertNotEquals(convertedCourse.getId(), course.getId());
    }

    @Test
    public void domainToDtoTest() {
        CourseDto convertedCourseDto = courseMapper.domainToDto(course);
        assertEquals(convertedCourseDto, courseDto);
    }
}
