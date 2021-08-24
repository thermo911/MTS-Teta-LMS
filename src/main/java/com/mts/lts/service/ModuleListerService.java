package com.mts.lts.service;

import com.mts.lts.domain.Course;
import com.mts.lts.domain.Module;
import com.mts.lts.dao.ModuleRepository;
import com.mts.lts.service.exceptions.CourseNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ModuleListerService {

    private final ModuleRepository moduleRepository;

    @Autowired
    public ModuleListerService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public List<Module> findAll() {
        return moduleRepository.findAll();
    }

    public Module getOne(Long id) {
        return moduleRepository.getOne(id);
    }


    public void save(Module module) {
        moduleRepository.save(module);
    }

    public void deleteById(Long id) {
        moduleRepository.deleteById(id);
    }

    public List<Module> findByTitleWithPrefix(String prefix) {
        return moduleRepository.findByTitleLike(prefix + "%");
    }
    public List<Module> findByCourseId(Long courseId) {
        return moduleRepository.findByCourseId(courseId);
    }

    public Module findById(Long moduleId) {
        return moduleRepository.findById(moduleId).orElseThrow(() -> new CourseNotFoundException(moduleId));//module или универсал класс
    }
}