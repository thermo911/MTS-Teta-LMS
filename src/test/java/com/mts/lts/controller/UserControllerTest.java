package com.mts.lts.controller;

import com.mts.lts.Constants;
import com.mts.lts.domain.Role;
import com.mts.lts.domain.User;
import com.mts.lts.dto.UserDto;
import com.mts.lts.mapper.UserMapper;
import com.mts.lts.service.RoleListerService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerTest extends BaseControllerTest {

    @MockBean
    private RoleListerService roleListerServiceMock;
    @MockBean
    private UserListerService userListerServiceMock;
    @MockBean
    private PasswordEncoder passwordEncoderMock;

    @Autowired
    public UserControllerTest(WebApplicationContext context) {
        super(context);
    }

    @TestConfiguration
    public static class OverrideBean {

        @Bean
        @Autowired
        public UserMapper getUserMapper(UserListerService userListerService,
                                        PasswordEncoder passwordEncoder) {
            return new UserMapper(userListerService, passwordEncoder);
        }
    }

    @Autowired
    private UserMapper userMapper;

    private User testUser1;
    private User testUser2;
    private Role testStudentRole;
    private Role testAdminRole;
    private long testCourseId;
    private static final long testUserId1 = 1;
    private static final long testUserId2 = 2;
    private static final String testUsername1 = "username1";
    private static final String testUsername2 = "username2";
    private static final String testPassword1 = "password1";
    private static final String testPassword2 = "password2";

    @BeforeEach
    public void setUpForEach() {
        testStudentRole = new Role(Constants.STUDENT_ROLE);
        testAdminRole = new Role(Constants.ADMIN_ROLE);
        testUser1 = new User(testUserId1, testUsername1, testPassword1);
        testUser1.setRoles(Collections.singleton(testStudentRole));
        testUser2 = new User(testUserId2, testUsername2, testPassword2);
        testUser2.setRoles(Collections.singleton(testStudentRole));
        Mockito.when(passwordEncoderMock.encode(testPassword1)).thenReturn(testPassword1);
        Mockito.when(userListerServiceMock.findByUsername(testUsername1)).thenReturn(testUser1);
        Mockito.when(userListerServiceMock.findByUsername(testUsername2)).thenReturn(testUser2);
        Mockito.when(userListerServiceMock.getOne(testUserId1)).thenReturn(testUser1);
        Mockito.when(userListerServiceMock.getOne(testUserId2)).thenReturn(testUser2);
        Mockito.when(roleListerServiceMock.findAll())
                .thenReturn(Arrays.asList(testAdminRole, testStudentRole));
    }

    @Test
    public void newUserFormTest() throws Exception {
        mockMvc.perform(get("/user/new"))
                .andExpect(ResultMatcher.matchAll(
                        view().name("create_user"),
                        model().attribute("user", allOf(
                                hasProperty("username", emptyOrNullString()),
                                hasProperty("password", emptyOrNullString()),
                                hasProperty("roles", empty())
                        )),
                        model().attribute("roles", containsInAnyOrder(testAdminRole, testStudentRole))
                ));
    }

    @Test
    @WithMockUser(roles = "STUDENT", username = testUsername1)
    public void editUserFormTest() throws Exception {
        mockMvc.perform(get("/user/me"))
                .andExpect(ResultMatcher.matchAll(
                        view().name("edit_user"),
                        model().attribute("user", allOf(
                                hasProperty("username", is(testUsername1)),
                                hasProperty("password", emptyOrNullString()),
                                hasProperty("roles", is(testUser1.getRoles()))
                        )),
                        model().attribute("roles", containsInAnyOrder(testAdminRole, testStudentRole))
                ));
    }

    @Test
    @WithMockUser(roles = "STUDENT", username = testUsername1)
    public void postMeUserFormTest() throws Exception {
        UserDto userDto = userMapper.domainToDto(testUser1);
        userDto.setPassword(testPassword1);
        mockMvc.perform(post("/user")
                .with(csrf())
                .flashAttr("user", userDto)
        )
                .andExpect(redirectedUrl("/course"));
        Mockito.verify(userListerServiceMock, Mockito.times(1)).save(testUser1);
    }

    @Test
    @WithMockUser(roles = "STUDENT", username = testUsername1)
    public void postMeUserFormWrongRoleTest() throws Exception {
        UserDto userDto = userMapper.domainToDto(testUser1);
        userDto.setPassword(testPassword1);
        userDto.setRoles(Collections.singleton(testAdminRole));
        mockMvc.perform(
                post("/user")
                        .with(csrf())
                        .flashAttr("user", userDto)
        )
                .andExpect(redirectedUrl("/course"));
        Mockito.verify(userListerServiceMock, Mockito.times(1)).save(testUser1);
    }

    @Test
    @WithMockUser(roles = "STUDENT", username = testUsername1)
    public void postNotMeUserFormTest() throws Exception {
        UserDto userDto = userMapper.domainToDto(testUser2);
        userDto.setPassword(testPassword2);
        checkNoAccess(
                post("/user")
                        .with(csrf())
                        .flashAttr("user", userDto)
        );
        Mockito.verify(userListerServiceMock, Mockito.never()).save(any());
    }

    @Test
    @WithMockUser(roles = "STUDENT", username = testUsername1)
    public void postMeUserFormInvalidTest() throws Exception {
        UserDto userDto = userMapper.domainToDto(testUser1);
        userDto.setUsername("");
        userDto.setPassword("");
        mockMvc.perform(
                post("/user")
                        .with(csrf())
                        .flashAttr("user", userDto)
        )
                .andExpect(ResultMatcher.matchAll(
                        view().name("edit_user"),
                        model().hasErrors(),
                        model().attributeHasFieldErrors("user", "username")
                ));
    }

    @Test
    @WithMockUser(roles = "STUDENT", username = testUsername1)
    public void postNewUserFormInvalidTest() throws Exception {
        UserDto userDto = new UserDto(null, "", "");
        userDto.setUsername("");
        mockMvc.perform(
                post("/user")
                        .with(csrf())
                        .flashAttr("user", userDto)
        )
                .andExpect(ResultMatcher.matchAll(
                        view().name("create_user"),
                        model().hasErrors(),
                        model().attributeHasFieldErrors("user", "username", "password")
                ));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = testUsername1)
    public void postAdminUserFormTest() throws Exception {
        UserDto userDto = userMapper.domainToDto(testUser1);
        userDto.setPassword(testPassword1);
        userDto.setRoles(Collections.singleton(testAdminRole));
        mockMvc.perform(
                post("/user")
                        .with(csrf())
                        .flashAttr("user", userDto)
        )
                .andExpect(redirectedUrl("/admin/user"));
        testUser1.setRoles(Collections.singleton(testAdminRole));
        Mockito.verify(userListerServiceMock, Mockito.times(1)).save(testUser1);
    }
}
