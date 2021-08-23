package com.mts.lts.mapper;

import com.mts.lts.domain.Module;
import com.mts.lts.domain.Topic;
import com.mts.lts.dto.TopicDto;
import com.mts.lts.service.ModuleListerService;
import com.mts.lts.service.TopicListerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TopicMapper extends AbstractMapper<TopicDto, Topic, TopicListerService> {

    private final ModuleListerService moduleListerService;

    @Autowired
    public TopicMapper(
            TopicListerService topicListerService,
            ModuleListerService moduleListerService
    ) {
        super(topicListerService);
        this.moduleListerService = moduleListerService;
    }

    @Override
    public Topic dtoToDomain(TopicDto entityDto) {
        Long lessonDtoId = entityDto.getId();
        Topic topic;
        if (lessonDtoId != null) {
            topic = entityService.findById(lessonDtoId);
        } else {
            topic = new Topic();
            Module module = moduleListerService.getOne(entityDto.getCourseId());
            topic.setModule(module);
        }
        topic.setTitle(entityDto.getTitle());
        topic.setText(entityDto.getText());

        return topic;
    }

    @Override
    public TopicDto domainToDto(Topic entity) {
        return new TopicDto(
                entity.getId(),
                entity.getTitle(),
                entity.getText(),
                entity.getModule().getId()
        );
    }
}
