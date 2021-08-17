package com.mts.lts.mapper;

import com.mts.lts.domain.Course;
import com.mts.lts.dto.CourseDto;
import com.mts.lts.service.CourseListerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper extends AbstractMapper<CourseDto, Course, CourseListerService> {

    @Autowired
    public CourseMapper(CourseListerService courseListerService) {
        super(courseListerService);
    }

    @Override
    public Course dtoToDomain(CourseDto entityDto) {
        Course course;
        Long courseDtoId = entityDto.getId();
        if (courseDtoId != null) {
            course = entityService.getOne(courseDtoId);
        } else {
            course = new Course();
        }
        course.setAuthor(entityDto.getAuthor());
        course.setTitle(entityDto.getTitle());

        return course;
    }

    @Override
    public CourseDto domainToDto(Course entity) {
        return new CourseDto(
                entity.getId(),
                entity.getAuthor(),
                entity.getTitle(),
                entity.getCoverImage() != null
        );
    }
}
