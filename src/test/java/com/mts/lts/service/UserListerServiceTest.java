package com.mts.lts.service;

import com.mts.lts.Constants;
import com.mts.lts.domain.Course;
import com.mts.lts.domain.Role;
import com.mts.lts.domain.User;
import com.mts.lts.repository.CourseRepository;
import com.mts.lts.repository.RoleRepository;
import com.mts.lts.repository.UserRepository;
import com.mts.lts.service.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mts.lts.TestUtils.assertEqualsUser;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@AutoConfigureTestDatabase
public class UserListerServiceTest {

    @Autowired
    private UserListerService userListerService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CourseAssignService courseAssignService;

    private User testUser1;
    private User testUser2;
    private long testCourseId;
    private static final String testUsername1 = "username1";
    private static final String testUsername2 = "username2";
    private static final String testPassword1 = "password1";
    private static final String testPassword2 = "password2";

    @BeforeEach
    void setUpForEach() {
        userRepository.deleteAll();
        courseRepository.deleteAll();
        roleRepository.deleteAll();
        List<Role> roles = roleRepository.findAll();
        Role role = roleRepository.save(new Role(Constants.STUDENT_ROLE));
        testUser1 = new User(testUsername1);
        testUser1.setPassword(testPassword1);
        testUser1.setRoles(new HashSet<>(Collections.singleton(role)));
        testUser2 = new User(testUsername2);
        testUser2.setPassword(testPassword2);
        testUser2.setRoles(new HashSet<>(Collections.singleton(role)));
        Course course = courseRepository.save(
                new Course(
                        "author",
                        "title",
                        Collections.emptyList(),
                        new HashSet<>()
                )
        );
        testCourseId = course.getId();
    }

    @Test
    public void findAllTest() {
        userRepository.save(testUser1);
        userRepository.save(testUser2);
        List<User> res = userListerService.findAll();
        assertEquals(res.size(), 2);
    }

    @Test
    @Transactional
    public void getOneSuccessTest() {
        User savedUser = userRepository.save(testUser1);
        userRepository.save(testUser2);
        User res = userListerService.getOne(savedUser.getId());
        assertEqualsUser(res, testUser1);
    }

    @Test
    @Transactional
    public void findByIdSuccessTest() {
        User savedUser = userRepository.save(testUser1);
        userRepository.save(testUser2);
        User res = userListerService.findById(savedUser.getId());
        assertEqualsUser(res, testUser1);
    }

    @Test
    public void findByIdFailTest() {
        assertThrows(
                UserNotFoundException.class,
                () -> {
                    User savedUser = userRepository.save(testUser2);
                    userListerService.findById(savedUser.getId() + 1);
                }
        );
    }

    @Test
    @Transactional
    public void saveNewTest() {
        assertTrue(userRepository.findAll().isEmpty());
        userListerService.save(testUser2);
        assertEquals(userRepository.findAll().size(), 1);
        assertEqualsUser(userRepository.findAll().get(0), testUser2);
    }

    @Test
    @Transactional
    public void saveUpdateTest() {
        User savedUser = userRepository.save(testUser2);
        assertTrue(userRepository.findById(savedUser.getId()).isPresent());
        testUser2.setPassword(testPassword1);
        userListerService.save(testUser2);
        assertTrue(userRepository.findById(savedUser.getId()).isPresent());
        assertEqualsUser(userRepository.findById(savedUser.getId()).get(), testUser2);
    }

    @Test
    @Transactional
    public void deleteByIdExistsTest() {
        User savedUser = userRepository.save(testUser2);
        courseAssignService.assignToCourse(savedUser.getId(), testCourseId);
        assertTrue(userRepository.findById(savedUser.getId()).isPresent());
        userListerService.deleteById(savedUser.getId());
        assertFalse(userRepository.findById(savedUser.getId()).isPresent());
    }

    @Test
    @Transactional
    public void findByUsernameSuccessTest() {
        userRepository.save(testUser1);
        userRepository.save(testUser2);
        User res = userListerService.findByUsername(testUsername1);
        assertEqualsUser(res, testUser1);
    }

    @Test
    public void findByUsernameFailTest() {
        userRepository.save(testUser2);
        assertThrows(
                UsernameNotFoundException.class,
                () -> userListerService.findByUsername(testUsername1)
        );
    }

    @Test
    @Transactional
    public void findByCourseIdSuccessTest() {
        User savedUser = userRepository.save(testUser1);
        userRepository.save(testUser2);
        courseAssignService.assignToCourse(savedUser.getId(), testCourseId);
        Set<User> res = userListerService.findByCourseId(testCourseId);
        assertEquals(res.size(), 1);
        assertEqualsUser((User) res.toArray()[0], testUser1);
    }

    @Test
    public void findByCourseIdFailTest() {
        userRepository.save(testUser1);
        userRepository.save(testUser2);
        Set<User> res = userListerService.findByCourseId(testCourseId + 1);
        assertTrue(res.isEmpty());
    }

    @Test
    @Transactional
    public void findNotAssignedToCourseTest() {
        User savedUser = userRepository.save(testUser1);
        userRepository.save(testUser2);
        courseAssignService.assignToCourse(savedUser.getId(), testCourseId);
        List<User> res = userListerService.findNotAssignedToCourse(testCourseId);
        assertEquals(res.size(), 1);
        assertEqualsUser((User) res.toArray()[0], testUser2);
    }
}
