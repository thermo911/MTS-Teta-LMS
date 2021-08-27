package com.mts.lts.service;

import com.mts.lts.domain.Module;
import com.mts.lts.domain.Topic;
import com.mts.lts.dao.ModuleRepository;
import com.mts.lts.dao.TopicRepository;
import com.mts.lts.service.exceptions.TopicNotFoundException;
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
public class TopicListerServiceTest {

    @Autowired
    private TopicListerService topicListerService;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private ModuleRepository moduleRepository;

    private Topic testTopic1;
    private Topic testTopic2;
    private static final String testCourseAuthor = "author";
    private static final String testCourseTitle = "title";
    private static final String testText1 = "text1";
    private static final String testText2 = "text2";
    private static final String testTitle1 = "title1";
    private static final String testTitle2 = "title2";

    @BeforeEach
    void setUpForEach() {
        topicRepository.deleteAll();
        moduleRepository.deleteAll();
        Module module1 = moduleRepository.save(
                new Module(
                        testCourseAuthor,
                        testCourseTitle,
                        Collections.emptyList(),
                        Collections.emptySet()
                )
        );
        Module module2 = moduleRepository.save(
                new Module(
                        testCourseAuthor,
                        testCourseTitle,
                        Collections.emptyList(),
                        Collections.emptySet()
                )
        );
        testTopic1 = new Topic(testTitle1, testText1, module1);
        testTopic2 = new Topic(testTitle2, testText2, module2);
    }

    @Test
    @Transactional
    public void findByIdSuccessTest() {
        Topic savedTopic = topicRepository.save(testTopic1);
        topicRepository.save(testTopic2);
        Topic res = topicListerService.findById(savedTopic.getId());
        assertEqualsLesson(res, testTopic1);
    }

    @Test
    public void findByIdFailTest() {
        assertThrows(
                TopicNotFoundException.class,
                () -> {
                    Topic savedTopic = topicRepository.save(testTopic2);
                    topicListerService.findById(savedTopic.getId() + 1);
                }
        );
    }

    @Test
    @Transactional
    public void saveNewTest() {
        assertTrue(topicRepository.findAll().isEmpty());
        topicListerService.save(testTopic2);
        assertEquals(topicRepository.findAll().size(), 1);
        assertEqualsLesson(topicRepository.findAll().get(0), testTopic2);
    }

    @Test
    @Transactional
    public void saveUpdateTest() {
        Topic savedTopic = topicRepository.save(testTopic2);
        assertTrue(topicRepository.findById(savedTopic.getId()).isPresent());
        testTopic2.setTitle(testTitle1);
        topicListerService.save(testTopic2);
        assertTrue(topicRepository.findById(savedTopic.getId()).isPresent());
        assertEqualsLesson(topicRepository.findById(savedTopic.getId()).get(), testTopic2);
    }


    @Test
    public void deleteByIdExistsTest() {
        Topic savedTopic = topicRepository.save(testTopic2);
        assertTrue(topicRepository.findById(savedTopic.getId()).isPresent());
        topicListerService.deleteById(savedTopic.getId());
        assertFalse(topicRepository.findById(savedTopic.getId()).isPresent());
    }

    @Test
    @Transactional
    public void findByCourseIdTest() {
        Topic savedTopic = topicRepository.save(testTopic1);
        assertTrue(topicRepository.findById(savedTopic.getId()).isPresent());
        List<Topic> res = topicListerService.findByModuleId(savedTopic.getModule().getId());
        assertEquals(res.size(), 1);
        assertEqualsLesson(res.get(0), testTopic1);
    }

    @Test
    @Transactional
    public void findByCourseIdWithoutTextTest() {
        Topic savedTopic = topicRepository.save(testTopic1);
        assertTrue(topicRepository.findById(savedTopic.getId()).isPresent());
        List<Topic> res = topicListerService
                .findByCourseIdWithoutText(savedTopic.getModule().getId());
        assertEquals(res.size(), 1);
        Topic resTopic = res.get(0);
        assertEquals(resTopic.getTitle(), testTopic1.getTitle());
        assertEqualsCourse(resTopic.getModule(), testTopic1.getModule());
        assertNull(resTopic.getText());
    }

}
