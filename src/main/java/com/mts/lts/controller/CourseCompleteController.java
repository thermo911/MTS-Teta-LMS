package com.mts.lts.controller;

import com.mts.lts.domain.Course;
import com.mts.lts.domain.Module;
import com.mts.lts.domain.Topic;
import com.mts.lts.domain.User;
import com.mts.lts.mapper.CourseMapper;
import com.mts.lts.mapper.ModuleMapper;
import com.mts.lts.mapper.TopicMapper;
import com.mts.lts.mapper.UserMapper;
import com.mts.lts.service.*;
import com.mts.lts.service.exceptions.ResourceNotFoundException;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.NoSuchElementException;


@Controller
public class CourseCompleteController {
    private final CourseListerService courseListerService;
    private final ModuleListerService moduleListerService;
    private final TopicListerService topicListerService;
    private final UserListerService userListerService;
    private final CourseMapper courseMapper;
    private final ModuleMapper moduleMapper;
    private final UserMapper userMapper;
    private final TopicMapper topicMapper;
    private final CourseCompletionService courseCompletionService;

    public CourseCompleteController(CourseListerService courseListerService,
                                    ModuleListerService moduleListerService,
                                    TopicListerService topicListerService,
                                    UserListerService userListerService,
                                    CourseMapper courseMapper,
                                    ModuleMapper moduleMapper,
                                    UserMapper userMapper,
                                    TopicMapper topicMapper,
                                    CourseCompletionService courseCompletionService) {
        this.courseListerService = courseListerService;
        this.moduleListerService = moduleListerService;
        this.topicListerService = topicListerService;
        this.userListerService = userListerService;
        this.courseMapper = courseMapper;
        this.moduleMapper = moduleMapper;
        this.userMapper = userMapper;
        this.topicMapper = topicMapper;
        this.courseCompletionService = courseCompletionService;
    }

    @GetMapping("/courses")
    public String courseTable(
            Model model,
            @RequestParam(name = "titlePrefix", required = false, defaultValue = "") String titlePrefix
    ) {
        model.addAttribute(
                "courses",
                courseMapper.domainToDto(courseListerService.findByTitleWithPrefix(titlePrefix))
        );

        return "courses";
    }


    @GetMapping("/courses/{id}")
    @Transactional
    @PreAuthorize("isAuthenticated")
    public String coursePage(Model model, @PathVariable("id") Long id,
                             Authentication auth) {
        User user = userListerService.findByEmail(auth.getName());
        Course course = courseListerService.findById(id);
        Integer completed = courseCompletionService.countTopicsCompletedByUser(user, course);
        Integer total = courseCompletionService.countTopicsForCourse(course);
        model.addAttribute("completed_by_user", completed);
        model.addAttribute("total_topics", total);
        model.addAttribute("courseDto", courseMapper.domainToDto(course));
        model.addAttribute(
                "moduleTreeTree",
                moduleMapper.listDomainToModuleTreeDtoList(moduleListerService.findByCourseId(course.getId()))
        );
        return "course_page";
    }

    @GetMapping("/courses/{course_id}/modules/{module_id}")
    @Transactional
    public String lessonPage(Model model, @PathVariable("module_id") Long moduleId,
                             @RequestParam("step") Integer step,
                             Authentication auth) {

        Module module = moduleListerService.findById(moduleId);
        Integer max_step = module.getTopics().size();
        Topic topic = module.getTopics().get(step-1);
        model.addAttribute("course_id", module.getCourse().getId());
        model.addAttribute("moduleTitle", module.getTitle());
        model.addAttribute("topicDto", topicMapper.domainToDto(topic));
        model.addAttribute("pref", step - 1 > 0 ? step - 1: 1);
        model.addAttribute("next", step + 1 <= max_step ? step + 1: max_step);
        User user = userListerService.findByEmail(auth.getName());
        model.addAttribute("completed", user.getCompletedTopics().contains(topic));
        return "lesson_page";
    }

    @GetMapping("/courses/{course_id}/modules/{module_id}/topics/{topic_id}")
    @Transactional
    public String lessonPage(Model model, @PathVariable("course_id") Long courseId,
                      @PathVariable("module_id") Long moduleId,
                             @PathVariable("topic_id") Long topicId) {

        Module module = moduleListerService.findById(moduleId);
        Topic topic = topicListerService.findById(topicId);
        Integer step  = module.getTopics().indexOf(topic) + 1;
        model.addAttribute("course_id", courseId);
        //model.addAttribute("module_id", moduleId);
        model.addAttribute("topic_id", topic.getId());

        return String.format("redirect:/courses/%d/modules/%d?step=%d", courseId, moduleId, step);
    }

    @GetMapping("/courses/{course_id}/modules/{module_id}/complete")
    @Transactional
    public String completeLesson(Model model,
                                 @PathVariable("course_id") Long courseId,
                                 @PathVariable("module_id") Long moduleId,
                                 @RequestParam("topic_id") Long topicId,
                                 Authentication auth) {
        User user = userListerService.findByEmail(auth.getName());
        Topic topic = topicListerService.findById(topicId);
        user.getCompletedTopics().add(topic);
        topic.getUsersWhoCompleted().add(user);
        userListerService.save(user);
        topicListerService.save(topic);

        return String.format("redirect:/courses/%d/modules/%d/topics/%d", courseId, moduleId, topicId);
    }

    @ExceptionHandler
    public ResponseEntity<Void> resourceNotFoundExceptionHandler(IndexOutOfBoundsException e) {
        return ResponseEntity.notFound().build();
    }
}
