package com.mts.lts.service;

import com.mts.lts.domain.Course;
import com.mts.lts.domain.User;
import com.mts.lts.dao.UserRepository;
import com.mts.lts.service.exceptions.UserNotFoundException;
import com.mts.lts.validation.unique.FieldValueExists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class UserListerService implements FieldValueExists {

    private final UserRepository userRepository;
    private final CourseAssignService courseAssignService;

    @Autowired
    public UserListerService(
            UserRepository userRepository,
            CourseAssignService courseAssignService
    ) {
        this.userRepository = userRepository;
        this.courseAssignService = courseAssignService;
    }

    public List<User> findNotAssignedToCourse(Long courseId) {
        return userRepository.findNotAssignedToCourse(courseId);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Set<User> findByCourseId(Long courseId) {
        return userRepository.findByCourseId(courseId);
    }

    public User getOne(Long id) {
        return userRepository.getOne(id);
    }

    public User findById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void deleteById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        for (Course course : new LinkedList<>(user.getCourses())) { // Course
            courseAssignService.unassignToCourse(id, course.getId());
        }
        userRepository.deleteById(id);
    }

    public User findByEmail(String username) {
        return userRepository.findUserByEmail(username) // Course
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));
    }

    @Override
    public boolean fieldValueExists(Object value, String fieldName)
            throws UnsupportedOperationException {
        Assert.notNull(fieldName, "Field name must be provided for unique value validation");

        if (!fieldName.equals("username")) {
            throw new UnsupportedOperationException("Field name not supported");
        }

        if (value == null) {
            return false;
        }

        return userRepository.existsByEmail((String) value);
    }
}
