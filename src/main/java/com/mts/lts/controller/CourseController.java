package com.mts.lts.controller;

import com.mts.lts.Constants;
import com.mts.lts.domain.Course;
import com.mts.lts.domain.User;
import com.mts.lts.dto.CourseDto;
import com.mts.lts.mapper.CourseMapper;
import com.mts.lts.mapper.LessonMapper;
import com.mts.lts.mapper.UserMapper;
import com.mts.lts.service.*;
import com.mts.lts.service.errors.InternalServerError;
import com.mts.lts.service.exceptions.CourseNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/course")
public class CourseController {

    private final CourseListerService courseListerService;
    private final UserListerService userListerService;
    private final LessonListerService lessonListerService;
    private final CourseAssignService courseAssignService;
    private final AvatarStorageService avatarStorageService;
    private final LessonMapper lessonMapper;
    private final CourseMapper courseMapper;
    private final UserMapper userMapper;

    @Autowired
    public CourseController(
            CourseListerService courseListerService,
            UserListerService userListerService,
            LessonListerService lessonListerService,
            CourseAssignService courseAssignService,
            AvatarStorageService avatarStorageService,
            LessonMapper lessonMapper,
            CourseMapper courseMapper,
            UserMapper userMapper
    ) {
        this.courseListerService = courseListerService;
        this.userListerService = userListerService;
        this.lessonListerService = lessonListerService;
        this.courseAssignService = courseAssignService;
        this.avatarStorageService = avatarStorageService;
        this.lessonMapper = lessonMapper;
        this.courseMapper = courseMapper;
        this.userMapper = userMapper;
    }

    @GetMapping
    public String courseTable(
            Model model,
            @RequestParam(name = "titlePrefix", required = false) String titlePrefix,
            Principal principal
    ) {
        titlePrefix = titlePrefix == null ? "" : titlePrefix;
        model.addAttribute("activePage", "courses");
        model.addAttribute(
                "courses",
                courseMapper.domainToDto(courseListerService.findByTitleWithPrefix(titlePrefix))
        );
        if (principal != null) {
            User user = userListerService.findByUsername(principal.getName());
            model.addAttribute("userCourses", courseMapper.domainToDto(user.getCourses()));
        }
        return "course_list";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/new")
    public String courseForm(Model model) {
        model.addAttribute("courseDto", new CourseDto());
        return "edit_course";
    }

    @GetMapping("/{id}")
    public String courseForm(Model model, @PathVariable("id") Long id) {
        Course course = courseListerService.findById(id);
        model.addAttribute("courseDto", courseMapper.domainToDto(course));
        model.addAttribute(
                "lessons",
                lessonMapper.domainToDto(lessonListerService.findByCourseIdWithoutText(course.getId()))
        );
        model.addAttribute(
                "users",
                userMapper.domainToDto(course.getUsers())
        );
        return "edit_course";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{id}/assign")
    public String assignUserForm(Model model, @PathVariable("id") Long id) {
        model.addAttribute(
                "users",
                userMapper.domainToDto(userListerService.findNotAssignedToCourse(id))
        );
        model.addAttribute("courseId", id);
        return "assign_course";
    }

    @GetMapping("/{id}/avatar")
    @ResponseBody
    public ResponseEntity<byte[]> coverImage(
            @PathVariable("id") Long courseId
    ) {
        String contentType = avatarStorageService.getContentTypeByCourse(courseId);
        byte[] data = avatarStorageService.getAvatarImageByCourse(courseId);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/avatar")
    public String updateCoverImage(
            @PathVariable("id") Long courseId,
            @RequestParam("cover") MultipartFile cover
    ) {
        if (!cover.isEmpty()) {
            try {
                avatarStorageService.save(
                        courseId,
                        cover.getContentType(),
                        cover.getInputStream()
                );
            } catch (Exception ex) {
                throw new InternalServerError(ex);
            }
        }
        return "redirect:/course/" + courseId;
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}/avatar")
    public String deleteCoverImage(
            @PathVariable("id") Long courseId
    ) {
        avatarStorageService.deleteAvatarImageByCourse(courseId);
        return "redirect:/course/" + courseId;
    }

    @Secured("ROLE_ADMIN")
    @Transactional
    @PostMapping
    public String submitCourseForm(@Valid CourseDto courseDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "edit_course";
        }
        courseListerService.save(courseMapper.dtoToDomain(courseDto));
        return "redirect:/course";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{courseId}/assign")
    public String submitAssignUserForm(
            @PathVariable("courseId") Long courseId,
            @RequestParam(value = "userId", required = false) Long userId,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (userId != null && !request.isUserInRole(Constants.ADMIN_ROLE)) {
            response.setStatus(403);
            return "forward:/access_denied";
        }

        String redirect;
        if (userId == null) {
            userId = userListerService.findByUsername(request.getUserPrincipal().getName()).getId();
            redirect = "redirect:/course";
        } else {
            redirect = "redirect:/course/" + courseId;
        }
        courseAssignService.assignToCourse(userId, courseId);
        return redirect;
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{courseId}/unassign/{userId}")
    public String unassignUser(
            @PathVariable("courseId") Long courseId,
            @PathVariable("userId") Long userId
    ) {
        courseAssignService.unassignToCourse(userId, courseId);
        return "redirect:/course/" + courseId;
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{courseId}/unassign")
    public String unassignUser(
            @PathVariable("courseId") Long courseId,
            Principal principal
    ) {
        Long userId = userListerService.findByUsername(principal.getName()).getId();
        courseAssignService.unassignToCourse(userId, courseId);
        return "redirect:/course";
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        courseListerService.deleteById(id);
        return "redirect:/course";
    }

    @ExceptionHandler
    public ModelAndView notFoundExceptionHandler(CourseNotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("not_found");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }
}
