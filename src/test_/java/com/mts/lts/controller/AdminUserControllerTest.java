package com.mts.lts.controller;

import com.mts.lts.Constants;
import com.mts.lts.domain.Role;
import com.mts.lts.domain.User;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AdminUserControllerTest extends BaseControllerTest {

    @MockBean
    private RoleListerService roleListerServiceMock;
    @MockBean
    private UserListerService userListerServiceMock;

    @Autowired
    public AdminUserControllerTest(WebApplicationContext context) {
        super(context);
    }

    @TestConfiguration
    public static class OverrideBean {

        @Bean
        @Autowired
        public UserMapper getUserMapper(UserListerService userListerService) {
            return new UserMapper(userListerService, new BCryptPasswordEncoder());
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
        Mockito.when(userListerServiceMock.getOne(testUserId1)).thenReturn(testUser1);
        Mockito.when(userListerServiceMock.getOne(testUserId2)).thenReturn(testUser2);
        Mockito.when(userListerServiceMock.findAll()).thenReturn(Arrays.asList(testUser1, testUser2));
        Mockito.when(roleListerServiceMock.findAll())
                .thenReturn(Arrays.asList(testAdminRole, testStudentRole));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void userFormTest() throws Exception {
        mockMvc.perform(get("/admin/user/" + testUserId1))
                .andExpect(ResultMatcher.matchAll(
                        view().name("edit_user"),
                        model().attribute("user", allOf(
                                hasProperty("username", is(testUsername1)),
                                hasProperty("password", is("")),
                                hasProperty("roles", contains(testStudentRole))
                        )),
                        model().attribute("roles", containsInAnyOrder(testAdminRole, testStudentRole))
                ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void userTableTest() throws Exception {
        mockMvc.perform(get("/admin/user"))
                .andExpect(ResultMatcher.matchAll(
                        view().name("user_list"),
                        model().attribute("users", containsInAnyOrder(
                                userMapper.domainToDto(testUser1),
                                userMapper.domainToDto(testUser2)
                        )),
                        model().attribute("activePage", is("users"))
                ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/admin/user/" + testUserId1).with(csrf()))
                .andExpect(ResultMatcher.matchAll(
                        redirectedUrl("/admin/user")
                ));
        Mockito.verify(userListerServiceMock, Mockito.times(1)).deleteById(testUserId1);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void userTableAccessTest() throws Exception {
        checkNoAccess(get("/admin/user"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void userFormAccessTest() throws Exception {
        checkNoAccess(get("/admin/user/" + testUserId1));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void deleteUserAccessTest() throws Exception {
        checkNoAccess(delete("/admin/user/" + testUserId1).with(csrf()));
        Mockito.verify(userListerServiceMock, Mockito.never()).deleteById(any());
    }
}
