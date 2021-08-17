package com.mts.lts.service;

import com.mts.lts.domain.Lesson;
import com.mts.lts.repository.LessonRepository;
import com.mts.lts.service.exceptions.LessonNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LessonListerService {

    private final LessonRepository lessonRepository;

    @Autowired
    public LessonListerService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public List<Lesson> findByCourseId(Long courseId) {
        return lessonRepository.findByCourseId(courseId);
    }

    public Lesson findById(Long id) {
        return lessonRepository.findById(id).orElseThrow(() -> new LessonNotFoundException(id));
    }

    public List<Lesson> findByCourseIdWithoutText(Long courseId) {
        return lessonRepository.findByCourseIdWithoutText(courseId);
    }

    public void save(Lesson lesson) {
        lessonRepository.save(lesson);
    }

    public void deleteById(Long id) {
        lessonRepository.deleteById(id);
    }
}
