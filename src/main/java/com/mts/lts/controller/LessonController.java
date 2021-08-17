package com.mts.lts.controller;

import com.mts.lts.domain.Lesson;
import com.mts.lts.dto.LessonDto;
import com.mts.lts.mapper.LessonMapper;
import com.mts.lts.service.LessonListerService;
import com.mts.lts.service.exceptions.LessonNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

@Controller
@RequestMapping("/lesson")
public class LessonController {

    private final LessonListerService lessonListerService;
    private final LessonMapper lessonMapper;

    @Autowired
    public LessonController(LessonListerService lessonListerService, LessonMapper lessonMapper) {
        this.lessonListerService = lessonListerService;
        this.lessonMapper = lessonMapper;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/new")
    public String lessonForm(Model model, @RequestParam("course_id") long courseId,
                             HttpServletRequest request) {
        model.addAttribute("courseId", courseId);
        model.addAttribute("lessonDto", new LessonDto(courseId));
        return "edit_lesson";
    }

    @Transactional
    @GetMapping("/{id}")
    public String lessonForm(
            Model model,
            @RequestParam("course_id") long courseId,
            @PathVariable("id") long id
    ) {
        model.addAttribute("courseId", courseId);
        model.addAttribute("lessonDto", lessonMapper.domainToDto(lessonListerService.findById(id)));
        return "edit_lesson";
    }

    @Secured("ROLE_ADMIN")
    @Transactional
    @PostMapping
    public String submitLessonForm(@Valid LessonDto lessonDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "edit_lesson";
        }
        Lesson lesson = lessonMapper.dtoToDomain(lessonDto);
        lessonListerService.save(lesson);
        return "redirect:/course/" + lessonDto.getCourseId();
    }

    @Secured("ROLE_ADMIN")
    @Transactional
    @DeleteMapping("/{id}")
    public String deleteLesson(@PathVariable("id") Long id) {
        Long courseId = lessonListerService.findById(id).getCourse().getId();
        lessonListerService.deleteById(id);
        return "redirect:/course/" + courseId;
    }

    @ExceptionHandler
    public ModelAndView notFoundExceptionHandler(LessonNotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("not_found");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }
}
