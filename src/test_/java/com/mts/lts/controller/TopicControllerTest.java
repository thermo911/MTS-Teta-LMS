package com.mts.lts.controller;

import com.mts.lts.domain.Module;
import com.mts.lts.domain.Topic;
import com.mts.lts.dto.TopicDto;
import com.mts.lts.mapper.TopicMapper;
import com.mts.lts.service.ModuleListerService;
import com.mts.lts.service.TopicListerService;
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
public class TopicControllerTest extends BaseControllerTest {

    @MockBean
    private TopicListerService topicListerServiceMock;

    @Autowired
    public TopicControllerTest(WebApplicationContext context) {
        super(context);
    }

    @TestConfiguration
    public static class OverrideBean {

        @Bean
        @Autowired
        public TopicMapper getLessonMapper(
                TopicListerService topicListerService,
                ModuleListerService moduleListerService
        ) {
            return new TopicMapper(topicListerService, moduleListerService);
        }
    }

    @Autowired
    private TopicMapper topicMapper;

    private Topic testTopic1;
    private Topic testTopic2;
    private static final long testCourseId = 3;
    private static final long testLessonId1 = 1;
    private static final long testLessonId2 = 2;
    private static final String testTitle1 = "title1";
    private static final String testTitle2 = "title2";
    private static final String testText1 = "text1";
    private static final String testText2 = "text2";

    @BeforeEach
    public void setUpForEach() {
        Module module = new Module();
        module.setId(testCourseId);
        testTopic1 = new Topic(testLessonId1, testTitle1, testText1, module);
        testTopic2 = new Topic(testLessonId2, testTitle2, testText2, module);
        Mockito.when(topicListerServiceMock.findById(testLessonId1)).thenReturn(testTopic1);
        Mockito.when(topicListerServiceMock.findById(testLessonId2)).thenReturn(testTopic2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void newLessonFormTest() throws Exception {
        TopicDto expectedDto = new TopicDto(testCourseId);
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
                                hasProperty("title", is(testTopic1.getTitle())),
                                hasProperty("text", is(testTopic1.getText()))
                        ))
                ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void submitLessonFormTest() throws Exception {
        TopicDto topicDto = topicMapper.domainToDto(testTopic1);
        mockMvc.perform(
                post("/lesson")
                        .with(csrf())
                        .flashAttr("lessonDto", topicDto)
        )
                .andExpect(redirectedUrl("/course/" + testCourseId));
        Mockito.verify(topicListerServiceMock, Mockito.times(1)).save(testTopic1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void submitLessonFormInvalidTest() throws Exception {
        TopicDto topicDto = topicMapper.domainToDto(testTopic1);
        topicDto.setText("");
        topicDto.setTitle("");
        mockMvc.perform(
                post("/lesson")
                        .with(csrf())
                        .flashAttr("lessonDto", topicDto)
        )
                .andExpect(ResultMatcher.matchAll(
                        view().name("edit_lesson"),
                        model().hasErrors(),
                        model().attributeHasFieldErrors("lessonDto", "title", "text")
                ));
        Mockito.verify(topicListerServiceMock, Mockito.never()).save(any());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void submitLessonFormAccessTest() throws Exception {
        TopicDto topicDto = topicMapper.domainToDto(testTopic1);
        checkNoAccess(
                post("/lesson")
                        .with(csrf())
                        .flashAttr("lessonDto", topicDto)
        );
        Mockito.verify(topicListerServiceMock, Mockito.never()).save(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteLessonTest() throws Exception {
        mockMvc.perform(delete("/lesson/" + testLessonId1).with(csrf()))
                .andExpect(
                        redirectedUrl("/course/" + testCourseId)
                );
        Mockito.verify(topicListerServiceMock, Mockito.times(1)).deleteById(testLessonId1);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void deleteLessonAccessTest() throws Exception {
        checkNoAccess(
                delete("/lesson/" + testLessonId1).with(csrf())
        );
        Mockito.verify(topicListerServiceMock, Mockito.never()).deleteById(any());
    }
}
