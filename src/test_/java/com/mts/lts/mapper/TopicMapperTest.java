package com.mts.lts.mapper;

import com.mts.lts.domain.Module;
import com.mts.lts.domain.Topic;
import com.mts.lts.dto.TopicDto;
import com.mts.lts.service.ModuleListerService;
import com.mts.lts.service.TopicListerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Component
public class TopicMapperTest {

    private TopicListerService topicListerServiceMock;
    private ModuleListerService moduleListerServiceMock;
    private TopicMapper topicMapper;
    private static Topic topic;
    private static TopicDto topicDto;
    private static Module module;

    private static final long courseId = 2;
    private static final long lessonId = 3;
    private static final String testText = "text";
    private static final String testTitle = "title";

    @BeforeAll
    public static void setUp() {
        topic = new Topic();
        topicDto = new TopicDto(lessonId, testTitle, testText, courseId);
        topic.setId(lessonId);
        module = new Module();
        module.setId(courseId);
        topic.setCourse(module);
        topic.setText(testText);
        topic.setTitle(testTitle);
    }

    @BeforeEach
    public void setUpForEach() {
        topicListerServiceMock = Mockito.mock(TopicListerService.class);
        Mockito.when(topicListerServiceMock.findById(lessonId)).thenReturn(topic);
        moduleListerServiceMock = Mockito.mock(ModuleListerService.class);
        Mockito.when(moduleListerServiceMock.getOne(courseId)).thenReturn(module);
        topicMapper = new TopicMapper(topicListerServiceMock, moduleListerServiceMock);
    }

    @Test
    public void dtoToDomainExistsTest() {
        Topic convertedTopic = topicMapper.dtoToDomain(topicDto);
        assertEquals(convertedTopic, topic);
    }

    @Test
    public void dtoToDomainNewTest() {
        TopicDto newTopicDto = new TopicDto(null, testTitle, testText, courseId);
        Topic convertedTopic = topicMapper.dtoToDomain(newTopicDto);
        assertEquals(convertedTopic.getText(), testText);
        assertEquals(convertedTopic.getTitle(), testTitle);
        assertEquals(convertedTopic.getCourse().getId(), courseId);
        assertNotEquals(convertedTopic.getId(), topic.getId());
    }

    @Test
    public void domainToDtoTest() {
        TopicDto convertedTopicDto = topicMapper.domainToDto(topic);
        assertEquals(convertedTopicDto, topicDto);
    }
}
