package com.mts.lts.controller;

import com.mts.lts.domain.Course;
import com.mts.lts.domain.Lesson;
import com.mts.lts.dto.LessonDto;
import com.mts.lts.mapper.LessonMapper;
import com.mts.lts.service.CourseListerService;
import com.mts.lts.service.LessonListerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LessonControllerTest extends BaseControllerTest {

    @MockBean
    private LessonListerService lessonListerServiceMock;

    @Autowired
    public LessonControllerTest(WebApplicationContext context) {
        super(context);
    }

    @TestConfiguration
    public static class OverrideBean {

        @Bean
        @Autowired
        public LessonMapper getLessonMapper(
                LessonListerService lessonListerService,
                CourseListerService courseListerService
        ) {
            return new LessonMapper(lessonListerService, courseListerService);
        }
    }

    @Autowired
    private LessonMapper lessonMapper;

    private Lesson testLesson1;
    private Lesson testLesson2;
    private static final long testCourseId = 3;
    private static final long testLessonId1 = 1;
    private static final long testLessonId2 = 2;
    private static final String testTitle1 = "title1";
    private static final String testTitle2 = "title2";
    private static final String testText1 = "text1";
    private static final String testText2 = "text2";

    @BeforeEach
    public void setUpForEach() {
        Course course = new Course();
        course.setId(testCourseId);
        testLesson1 = new Lesson(testLessonId1, testTitle1, testText1, course);
        testLesson2 = new Lesson(testLessonId2, testTitle2, testText2, course);
        Mockito.when(lessonListerServiceMock.findById(testLessonId1)).thenReturn(testLesson1);
        Mockito.when(lessonListerServiceMock.findById(testLessonId2)).thenReturn(testLesson2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void newLessonFormTest() throws Exception {
        LessonDto expectedDto = new LessonDto(testCourseId);
        mockMvc.perform(
                get("/lesson/new")
                        .param("course_id", Long.toString(testCourseId))
        )
                .andExpect(ResultMatcher.matchAll(
                        view().name("edit_lesson"),
                        model().attribute("courseId", is(testCourseId)),
                        model().attribute("lessonDto", is(expectedDto))
                ));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void newLessonFormAccessTest() throws Exception {
        checkNoAccess(
                get("/lesson/new")
                        .param("course_id", Long.toString(testCourseId))
        );
    }

    @Test
    public void lessonFormTest() throws Exception {
        mockMvc.perform(
                get("/lesson/" + testLessonId1)
                        .param("course_id", Long.toString(testCourseId))
        )
                .andExpect(ResultMatcher.matchAll(
                        view().name("edit_lesson"),
                        model().attribute("courseId", is(testCourseId)),
                        model().attribute("lessonDto", allOf(
                                hasProperty("id", is(testLessonId1)),
                                hasProperty("courseId", is(testCourseId)),
                                hasProperty("title", is(testLesson1.getTitle())),
                                hasProperty("text", is(testLesson1.getText()))
                        ))
                ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void submitLessonFormTest() throws Exception {
        LessonDto lessonDto = lessonMapper.domainToDto(testLesson1);
        mockMvc.perform(
                post("/lesson")
                        .with(csrf())
                        .flashAttr("lessonDto", lessonDto)
        )
                .andExpect(redirectedUrl("/course/" + testCourseId));
        Mockito.verify(lessonListerServiceMock, Mockito.times(1)).save(testLesson1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void submitLessonFormInvalidTest() throws Exception {
        LessonDto lessonDto = lessonMapper.domainToDto(testLesson1);
        lessonDto.setText("");
        lessonDto.setTitle("");
        mockMvc.perform(
                post("/lesson")
                        .with(csrf())
                        .flashAttr("lessonDto", lessonDto)
        )
                .andExpect(ResultMatcher.matchAll(
                        view().name("edit_lesson"),
                        model().hasErrors(),
                        model().attributeHasFieldErrors("lessonDto", "title", "text")
                ));
        Mockito.verify(lessonListerServiceMock, Mockito.never()).save(any());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void submitLessonFormAccessTest() throws Exception {
        LessonDto lessonDto = lessonMapper.domainToDto(testLesson1);
        checkNoAccess(
                post("/lesson")
                        .with(csrf())
                        .flashAttr("lessonDto", lessonDto)
        );
        Mockito.verify(lessonListerServiceMock, Mockito.never()).save(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteLessonTest() throws Exception {
        mockMvc.perform(delete("/lesson/" + testLessonId1).with(csrf()))
                .andExpect(
                        redirectedUrl("/course/" + testCourseId)
                );
        Mockito.verify(lessonListerServiceMock, Mockito.times(1)).deleteById(testLessonId1);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void deleteLessonAccessTest() throws Exception {
        checkNoAccess(
                delete("/lesson/" + testLessonId1).with(csrf())
        );
        Mockito.verify(lessonListerServiceMock, Mockito.never()).deleteById(any());
    }
}
