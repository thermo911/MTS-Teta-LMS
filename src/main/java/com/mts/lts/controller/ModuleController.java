package com.mts.lts.controller;

import com.mts.lts.domain.Module;
import com.mts.lts.dto.ModuleDto;
import com.mts.lts.dto.TopicDto;
import com.mts.lts.mapper.ModuleMapper;
import com.mts.lts.mapper.TopicMapper;
import com.mts.lts.service.*;
import com.mts.lts.service.exceptions.CourseNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import javax.validation.Valid;

import static com.mts.lts.Constants.PREF_ADMIN_COURSES;
import static com.mts.lts.Constants.PREF_ADMIN_MODULES;

@Controller
@RequestMapping(PREF_ADMIN_MODULES)
public class ModuleController {

        private final ModuleListerService moduleListerService;
        private final TopicListerService topicListerService;
        private final TopicMapper topicMapper;
        private final ModuleMapper moduleMapper;

        @Autowired
        public ModuleController(
                ModuleListerService moduleListerService,
                TopicListerService topicListerService,
                TopicMapper topicMapper,
                ModuleMapper moduleMapper
        ) {
            this.moduleListerService = moduleListerService;
            this.topicListerService = topicListerService;
            this.topicMapper = topicMapper;
            this.moduleMapper = moduleMapper;
        }


        @Secured("ROLE_ADMIN")
        @GetMapping("/new")
        public String moduleForm(Model model, @RequestParam(value = "courseId", required = true) long courseId) {
            model.addAttribute("courseId", courseId);
            model.addAttribute("moduleDto", new ModuleDto(courseId));
            return "edit_module";
        }

        @GetMapping("/{id}")
        public String moduleForm(Model model, @PathVariable("id") Long id) {
            Module module = moduleListerService.findById(id);
            model.addAttribute("moduleDto", moduleMapper.domainToDto(module));
            model.addAttribute(
                    "topics",
                    topicMapper.domainToDto(topicListerService.findByCourseIdWithoutText(module.getId()))
            );
            return "edit_module";
        }

        @Secured("ROLE_ADMIN")
        @Transactional
        @PostMapping
        public String submitModuleForm(@Valid ModuleDto moduleDto, BindingResult bindingResult) {
            if (bindingResult.hasErrors()) {
                return "edit_module";
            }
            moduleListerService.save(moduleMapper.dtoToDomain(moduleDto));
            return String.format("redirect:%s/%d",PREF_ADMIN_COURSES, moduleDto.getCourseId());
        }


        @Secured("ROLE_ADMIN")
        @DeleteMapping("/{id}")
        public String deleteModule(@PathVariable("id") Long id) {
            Long courseId = moduleListerService.findById(id).getCourse().getId();
            moduleListerService.deleteById(id);
            return String.format("redirect:%s/%d",PREF_ADMIN_COURSES, courseId);
        }

        @ExceptionHandler
        public ModelAndView notFoundExceptionHandler(CourseNotFoundException ex) {
            ModelAndView modelAndView = new ModelAndView("not_found");
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView;
        }
    }

