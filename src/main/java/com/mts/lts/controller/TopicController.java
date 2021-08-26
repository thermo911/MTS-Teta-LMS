package com.mts.lts.controller;

import com.mts.lts.domain.Topic;
import com.mts.lts.dto.TopicDto;
import com.mts.lts.mapper.TopicMapper;
import com.mts.lts.service.TopicListerService;
import com.mts.lts.service.exceptions.TopicNotFoundException;
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

import static com.mts.lts.Constants.PREF_ADMIN_MODULES;
import static com.mts.lts.Constants.PREF_ADMIN_TOPICS;

@Controller
@RequestMapping(PREF_ADMIN_TOPICS)
public class TopicController {

    private final TopicListerService topicListerService;
    private final TopicMapper topicMapper;

    @Autowired
    public TopicController(TopicListerService topicListerService, TopicMapper topicMapper) {
        this.topicListerService = topicListerService;
        this.topicMapper = topicMapper;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/new")
    public String topicForm(Model model, @RequestParam(value = "moduleId", required = true) long moduleId) {
        model.addAttribute("moduleId", moduleId);
        model.addAttribute("topicDto", new TopicDto(moduleId));
        return "edit_topic";
    }

    @Transactional
    @GetMapping("/{id}")
    public String topicForm(
            Model model,
            @RequestParam("moduleId") long moduleId,
            @PathVariable("id") long id
    ) {
        model.addAttribute("moduleId", moduleId);
        model.addAttribute("topicDto", topicMapper.domainToDto(topicListerService.findById(id)));
        return "edit_topic";
    }

    @Secured("ROLE_ADMIN")
    @Transactional
    @PostMapping
    public String submitTopicForm(@Valid TopicDto topicDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "edit_topic";
        }
        Topic topic = topicMapper.dtoToDomain(topicDto);
        topicListerService.save(topic);
        return String.format("redirect:%s/%d", PREF_ADMIN_MODULES, topicDto.getModuleId());
    }

    @Secured("ROLE_ADMIN")
    @Transactional
    @DeleteMapping("/{id}")
    public String deleteTopic(@PathVariable("id") Long id) {
        Long moduleId = topicListerService.findById(id).getModule().getId();
        topicListerService.deleteById(id);
        return String.format("redirect:%s/%d", PREF_ADMIN_MODULES, moduleId);
    }

    @ExceptionHandler
    public ModelAndView notFoundExceptionHandler(TopicNotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("not_found");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }
}
