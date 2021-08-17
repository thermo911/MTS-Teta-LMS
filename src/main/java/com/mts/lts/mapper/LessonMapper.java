package com.mts.lts.mapper;

import com.mts.lts.domain.Course;
import com.mts.lts.domain.Lesson;
import com.mts.lts.dto.LessonDto;
import com.mts.lts.service.CourseListerService;
import com.mts.lts.service.LessonListerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper extends AbstractMapper<LessonDto, Lesson, LessonListerService> {

    private final CourseListerService courseListerService;

    @Autowired
    public LessonMapper(
            LessonListerService lessonListerService,
            CourseListerService courseListerService
    ) {
        super(lessonListerService);
        this.courseListerService = courseListerService;
    }

    @Override
    public Lesson dtoToDomain(LessonDto entityDto) {
        Long lessonDtoId = entityDto.getId();
        Lesson lesson;
        if (lessonDtoId != null) {
            lesson = entityService.findById(lessonDtoId);
        } else {
            lesson = new Lesson();
            Course course = courseListerService.getOne(entityDto.getCourseId());
            lesson.setCourse(course);
        }
        lesson.setTitle(entityDto.getTitle());
        lesson.setText(entityDto.getText());

        return lesson;
    }

    @Override
    public LessonDto domainToDto(Lesson entity) {
        return new LessonDto(
                entity.getId(),
                entity.getTitle(),
                entity.getText(),
                entity.getCourse().getId()
        );
    }
}
