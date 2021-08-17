package com.mts.lts.controller;

import com.mts.lts.domain.Course;
import com.mts.lts.domain.Lesson;
import com.mts.lts.domain.User;
import com.mts.lts.dto.CourseDto;
import com.mts.lts.dto.LessonDto;
import com.mts.lts.dto.UserDto;
import com.mts.lts.mapper.CourseMapper;
import com.mts.lts.mapper.LessonMapper;
import com.mts.lts.mapper.UserMapper;
import com.mts.lts.service.CourseAssignService;
import com.mts.lts.service.CourseListerService;
import com.mts.lts.service.LessonListerService;
import com.mts.lts.service.UserListerService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CourseControllerTest extends BaseControllerTest {

    @MockBean
    private LessonListerService lessonListerService;
    @MockBean
    private CourseListerService courseListerService;
    @MockBean
    private UserListerService userListerService;
    @MockBean
    private CourseAssignService courseAssignService;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    public CourseControllerTest(WebApplicationContext context) {
        super(context);
    }

    @TestConfiguration
    public static class OverrideBean {

        @Bean
        @Autowired
        public CourseMapper getCourseMapper(
                CourseListerService courseListerService
        ) {
            return new CourseMapper(courseListerService);
        }

        @Bean
        @Autowired
        public LessonMapper getLessonMapper(
                LessonListerService lessonListerService,
                CourseListerService courseListerService
        ) {
            return new LessonMapper(lessonListerService, courseListerService);
        }

        @Bean
        @Autowired
        public UserMapper getUserMapper(
                UserListerService userListerService,
                PasswordEncoder passwordEncoder
        ) {
            return new UserMapper(userListerService, passwordEncoder);
        }
    }

    @Autowired
    private CourseMapper courseMapper;

    private Course testCourse1;
    private Course testCourse2;
    private Lesson testLesson;
    private static final long testCourseId1 = 1;
    private static final long testCourseId2 = 2;
    private static final long testUserId1 = 10;
    private static final long testUserId2 = 11;
    private static final String testUsername1 = "username1";
    private static final String testUsername2 = "username2";
    private static final String testTitle1 = "title1";
    private static final String testTitle2 = "title2";
    private static final String testAuthor1 = "text1";
    private static final String testAuthor2 = "text2";
    private static final String testLessonTitle = "lessonTitle";
    private static final long testLessonId = 6;

    @BeforeEach
    public void setUpForEach() {
        User user1 = new User();
        user1.setUsername(testUsername1);
        user1.setId(testUserId1);
        User user2 = new User();
        user2.setUsername(testUsername2);
        user2.setId(testUserId2);
        testCourse1 = new Course(testTitle1, testAuthor1);
        testLesson = new Lesson(testLessonId, testLessonTitle, "", testCourse1);
        testCourse1.setId(testCourseId1);
        testCourse1.setLessons(Collections.singletonList(testLesson));
        testCourse1.setUsers(Collections.singleton(user1));
        testCourse2 = new Course(testTitle2, testAuthor2);
        testCourse2.setId(testCourseId2);
        user1.setCourses(Collections.singleton(testCourse1));
        Mockito.when(courseListerService.getOne(testCourseId1)).thenReturn(testCourse1);
        Mockito.when(courseListerService.getOne(testCourseId2)).thenReturn(testCourse2);
        Mockito.when(courseListerService.findById(testCourseId1)).thenReturn(testCourse1);
        Mockito.when(courseListerService.findById(testCourseId2)).thenReturn(testCourse2);
        Mockito.when(courseListerService.findByTitleWithPrefix(""))
                .thenReturn(Arrays.asList(testCourse1, testCourse2));
        Mockito.when(courseListerService.findByTitleWithPrefix(
                testTitle1.substring(0, testTitle1.length() - 1))
        ).thenReturn(Arrays.asList(testCourse1, testCourse2));
        Mockito.when(courseListerService.findByTitleWithPrefix(testTitle1))
                .thenReturn(Collections.singletonList(testCourse1));
        Mockito.when(userListerService.findByUsername(testUsername1)).thenReturn(user1);
        Mockito.when(userListerService.findNotAssignedToCourse(testCourseId2))
                .thenReturn(Collections.singletonList(user1));
        Mockito.when(userListerService.findByUsername(testUsername1)).thenReturn(user1);
        Mockito.when(userListerService.findByUsername(testUsername2)).thenReturn(user2);
        Mockito.when(lessonListerService.findByCourseIdWithoutText(testCourseId1))
                .thenReturn(Collections.singletonList(testLesson));
        Mockito.when(passwordEncoder.encode("")).thenReturn("");
    }

    @Test
    public void courseTableNoUserTest() throws Exception {
        mockMvc.perform(get("/course"))
                .andExpect(ResultMatcher.matchAll(
                        view().name("course_list"),
                        model().attribute("activePage", is("courses")),
                        model().attribute("courses", hasSize(2)),
                        model().attribute("courses", containsInAnyOrder(
                                courseMapper.domainToDto(testCourse1),
                                courseMapper.domainToDto(testCourse2)
                        ))
                ));
    }

    @Test
    @WithMockUser(username = testUsername1)
    public void courseTableWithUserTest() throws Exception {
        mockMvc.perform(get("/course"))
                .andExpect(ResultMatcher.matchAll(
                        view().name("course_list"),
                        model().attribute("activePage", is("courses")),
                        model().attribute("courses", hasSize(2)),
                        model().attribute("courses", containsInAnyOrder(
                                courseMapper.domainToDto(testCourse1),
                                courseMapper.domainToDto(testCourse2)
                        )),
                        model().attribute("userCourses", hasSize(1)),
                        model().attribute("userCourses", containsInAnyOrder(
                                courseMapper.domainToDto(testCourse1))
                        )
                ));
    }

    @Test
    public void courseTablePrefixTest() throws Exception {
        mockMvc.perform(
                get("/course")
                        .param("titlePrefix", testTitle1.substring(0, testTitle1.length() - 1))
        )
                .andExpect(ResultMatcher.matchAll(
                        view().name("course_list"),
                        model().attribute("activePage", is("courses")),
                        model().attribute("courses", hasSize(2)),
                        model().attribute("courses", containsInAnyOrder(
                                courseMapper.domainToDto(testCourse1),
                                courseMapper.domainToDto(testCourse2)
                        ))
                ));

        mockMvc.perform(
                get("/course")
                        .param("titlePrefix", testTitle1)
        )
                .andExpect(ResultMatcher.matchAll(
                        view().name("course_list"),
                        model().attribute("activePage", is("courses")),
                        model().attribute("courses", hasSize(1)),
                        model().attribute("courses", containsInAnyOrder(
                                courseMapper.domainToDto(testCourse1)
                        ))
                ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void newCourseFormTest() throws Exception {
        CourseDto courseDto = new CourseDto();
        mockMvc.perform(get("/course/new"))
                .andExpect(ResultMatcher.matchAll(
                        view().name("edit_course"),
                        model().attribute("courseDto", is(courseDto))
                ));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void newCourseFormAccessTest() throws Exception {
        checkNoAccess(get("/course/new"));
    }

    @Test
    public void updateCourseFormTest() throws Exception {
        LessonDto lessonDto = new LessonDto(testLessonId, testLessonTitle, "", testCourseId1);
        UserDto userDto = new UserDto(testUserId1, testUsername1, "");
        mockMvc.perform(get("/course/" + testCourseId1))
                .andExpect(ResultMatcher.matchAll(
                        view().name("edit_course"),
                        model().attribute("courseDto", is(courseMapper.domainToDto(testCourse1))),
                        model().attribute("lessons", hasSize(1)),
                        model().attribute("lessons", containsInAnyOrder(lessonDto)),
                        model().attribute("users", hasSize(1)),
                        model().attribute("users", containsInAnyOrder(userDto))
                ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void assignUserFormTest() throws Exception {
        UserDto userDto = new UserDto(testUserId1, testUsername1, "");
        mockMvc.perform(get("/course/" + testCourseId2 + "/assign"))
                .andExpect(ResultMatcher.matchAll(
                        view().name("assign_course"),
                        model().attribute("courseId", is(testCourseId2)),
                        model().attribute("users", hasSize(1)),
                        model().attribute("users", containsInAnyOrder(userDto))
                ));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void assignUserFormAccessTest() throws Exception {
        checkNoAccess(get("/course/" + testCourseId1 + "/assign"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void submitCourseFormTest() throws Exception {
        CourseDto courseDto = courseMapper.domainToDto(testCourse1);
        mockMvc.perform(
                post("/course")
                        .with(csrf())
                        .flashAttr("courseDto", courseDto)
        )
                .andExpect(
                        redirectedUrl("/course")
                );
        Mockito.verify(courseListerService, Mockito.times(1)).save(testCourse1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void submitCourseFormInvalidTest() throws Exception {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(testCourseId1);
        courseDto.setTitle("");
        courseDto.setAuthor("");
        mockMvc.perform(
                post("/course")
                        .with(csrf())
                        .flashAttr("courseDto", courseDto)
        )
                .andExpect(ResultMatcher.matchAll(
                        view().name("edit_course"),
                        model().hasErrors(),
                        model().attributeHasFieldErrors("courseDto", "author", "title")
                ));
        Mockito.verify(courseListerService, Mockito.never()).save(any());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void submitCourseFormAccessTest() throws Exception {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(testCourseId1);
        checkNoAccess(
                post("/course")
                        .with(csrf())
                        .flashAttr("courseDto", courseDto)
        );
        Mockito.verify(courseListerService, Mockito.never()).save(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void submitAssignUserFormAdminTest() throws Exception {
        mockMvc.perform(
                post("/course/" + testCourseId2 + "/assign")
                        .with(csrf())
                        .param("userId", Long.toString(testUserId1))
        )
                .andExpect(
                        redirectedUrl("/course/" + testCourseId2)
                );
        Mockito.verify(courseAssignService, Mockito.times(1))
                .assignToCourse(testUserId1, testCourseId2);
    }

    @Test
    @WithMockUser(roles = "STUDENT", username = testUsername1)
    public void submitAssignUserFormMeTest() throws Exception {
        mockMvc.perform(
                post("/course/" + testCourseId2 + "/assign")
                        .with(csrf())
        )
                .andExpect(
                        redirectedUrl("/course")
                );
        Mockito.verify(courseAssignService, Mockito.times(1))
                .assignToCourse(testUserId1, testCourseId2);
    }

    @Test
    @WithMockUser(roles = "STUDENT", username = testUsername2)
    public void submitAssignUserFormNotMeTest() throws Exception {
        checkNoAccess(
                post("/course/" + testCourseId2 + "/assign")
                        .with(csrf())
                        .param("userId", Long.toString(testUserId1))
        );
        Mockito.verify(courseAssignService, Mockito.never())
                .assignToCourse(testUserId2, testCourseId2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void unassignUserAdminTest() throws Exception {
        mockMvc.perform(
                delete("/course/" + testCourseId2 + "/unassign/" + testUserId1)
                        .with(csrf())
        )
                .andExpect(
                        redirectedUrl("/course/" + testCourseId2)
                );
        Mockito.verify(courseAssignService, Mockito.times(1))
                .unassignToCourse(testUserId1, testCourseId2);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void unassignUserAdminAccessTest() throws Exception {
        checkNoAccess(
                delete("/course/" + testCourseId2 + "/unassign/" + testUserId1)
                        .with(csrf())
        );
        Mockito.verify(courseAssignService, Mockito.never())
                .unassignToCourse(testUserId1, testCourseId2);
    }

    @Test
    @WithMockUser(roles = "STUDENT", username = testUsername1)
    public void unassignUserMeTest() throws Exception {
        mockMvc.perform(
                delete("/course/" + testCourseId2 + "/unassign").with(csrf())
        )
                .andExpect(
                        redirectedUrl("/course")
                );
        Mockito.verify(courseAssignService, Mockito.times(1))
                .unassignToCourse(testUserId1, testCourseId2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteCourseTest() throws Exception {
        mockMvc.perform(
                delete("/course/" + testCourseId1).with(csrf())
        )
                .andExpect(
                        redirectedUrl("/course")
                );
        Mockito.verify(courseListerService, Mockito.times(1))
                .deleteById(testCourseId1);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void deleteCourseAccessTest() throws Exception {
        checkNoAccess(delete("/course/" + testCourseId1).with(csrf()));
        Mockito.verify(courseListerService, Mockito.never())
                .deleteById(testCourseId1);
    }
}
