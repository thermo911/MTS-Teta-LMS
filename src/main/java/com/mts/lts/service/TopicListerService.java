package com.mts.lts.service;

import com.mts.lts.domain.Topic;
import com.mts.lts.dao.TopicRepository;
import com.mts.lts.service.exceptions.TopicNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TopicListerService {

    private final TopicRepository topicRepository;

    @Autowired
    public TopicListerService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public List<Topic> findByModuleId(Long moduleId) {
        return topicRepository.findByModuleId(moduleId);
    }

    public Topic findById(Long id) {
        return topicRepository.findById(id).orElseThrow(() -> new TopicNotFoundException(id));
    }

    public List<Topic> findByCourseIdWithoutText(Long moduleId) {
        return topicRepository.findByModuleIdWithoutText(moduleId);
    }

    public void save(Topic topic) {
        topicRepository.save(topic);
    }

    public void deleteById(Long id) {
        topicRepository.deleteById(id);
    }
}
