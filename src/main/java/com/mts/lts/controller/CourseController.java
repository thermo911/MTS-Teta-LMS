package com.mts.lts.controller;

import com.mts.lts.Constants;
import com.mts.lts.domain.Course;
import com.mts.lts.domain.Image;
import com.mts.lts.domain.User;
import com.mts.lts.dto.CourseDto;
import com.mts.lts.dto.ModuleDto;
import com.mts.lts.mapper.CourseMapper;
import com.mts.lts.mapper.ModuleMapper;
import com.mts.lts.mapper.UserMapper;
import com.mts.lts.service.*;
import com.mts.lts.service.errors.InternalServerError;
import com.mts.lts.service.exceptions.CourseNotFoundException;
import com.mts.lts.service.exceptions.ResourceNotFoundException;
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

import static com.mts.lts.Constants.PREF_ADMIN_COURSES;

@Controller
@RequestMapping(PREF_ADMIN_COURSES)
public class CourseController {
    private final CourseListerService courseListerService;
    private final UserListerService userListerService;
    private final ModuleListerService moduleListerService;
    private final CourseAssignService courseAssignService;
    private final ImageStorageService imageStorageService;
    private final ModuleMapper moduleMapper;
    private final CourseMapper courseMapper;
    private final UserMapper userMapper;

    @Autowired
    public CourseController(
            CourseListerService courseListerService,
            UserListerService userListerService,
            ModuleListerService topicListerService,
            CourseAssignService courseAssignService,
            ImageStorageService imageStorageService,
            ModuleMapper moduleMapper,
            CourseMapper courseMapper,
            UserMapper userMapper
    ) {
        this.courseListerService = courseListerService;
        this.userListerService = userListerService;
        this.moduleListerService = topicListerService;
        this.courseAssignService = courseAssignService;
        this.imageStorageService = imageStorageService;
        this.moduleMapper = moduleMapper;
        this.courseMapper = courseMapper;
        this.userMapper = userMapper;
    }

    @GetMapping
    public String courseTable(
            Model model,
            @RequestParam(name = "titlePrefix", required = false, defaultValue = "") String titlePrefix,
            Principal principal
    ) {
        model.addAttribute(
                "courses",
                courseMapper.domainToDto(courseListerService.findByTitleWithPrefix(titlePrefix))
        );
        if (principal != null) {
            User user = userListerService.findByEmail(principal.getName());
            model.addAttribute("userCourses", courseMapper.domainToDto(user.getCourses()));
        }
        return "courses";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/new")
    public String courseForm(Model model) {
        model.addAttribute("courseDto", new ModuleDto());
        return "edit_course";
    }

    @GetMapping("/{id}")
    public String courseForm(Model model, @PathVariable("id") Long id) {
        Course course = courseListerService.findById(id);
        model.addAttribute("courseDto", courseMapper.domainToDto(course));
        model.addAttribute(
                "modules",
                moduleMapper.domainToDto(moduleListerService.findByCourseId(course.getId()))
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
    ) throws ResourceNotFoundException {
        Course course = courseListerService.findById(courseId);
        if (course.getCoverImage() == null) {
            course.setCoverImage(new Image(
                    null, "image/jpeg", "default_cover.jpeg"
            ));
        }
        byte[] data = imageStorageService.getImageData(course.getCoverImage())
                .orElseThrow(ResourceNotFoundException::new);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(
                        course.getCoverImage()
                        .getContentType())
                ).body(data);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/avatar")
    @Transactional
    public String updateCoverImage(
            @PathVariable("id") Long courseId,
            @RequestParam("cover") MultipartFile cover
    ) {
        if (!cover.isEmpty()) {
            Course course = courseListerService.findById(courseId);
            try {
                course.setCoverImage(
                        imageStorageService.save(
                                course.getCoverImage(),
                                cover.getContentType(),
                                cover.getInputStream()
                        )
                );
                courseListerService.save(course);
            } catch (Exception ex) {
                throw new InternalServerError(ex);
            }
        }
        return String.format("redirect:%s/%d", PREF_ADMIN_COURSES, courseId);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}/avatar")
    public String deleteCoverImage(
            @PathVariable("id") Long courseId
    ) {
        Course course = courseListerService.findById(courseId);
        imageStorageService.deleteImage(course.getCoverImage());
        return String.format("redirect:%s/%d", PREF_ADMIN_COURSES, courseId);
    }

    @Secured("ROLE_ADMIN")
    @Transactional
    @PostMapping
    public String submitCourseForm(@Valid CourseDto courseDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "edit_course";
        }
        courseListerService.save(courseMapper.dtoToDomain(courseDto));
        return String.format("redirect:/%s", PREF_ADMIN_COURSES);
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
            userId = userListerService.findByEmail(request.getUserPrincipal().getName()).getId();
            redirect = "redirect:/courses";
        } else {
            redirect = "redirect:/courses/" + courseId;
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
        return String.format("redirect:%s/%d", PREF_ADMIN_COURSES, courseId);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{courseId}/unassign")
    public String unassignUser(
            @PathVariable("courseId") Long courseId,
            Principal principal
    ) {
        Long userId = userListerService.findByEmail(principal.getName()).getId();
        courseAssignService.unassignToCourse(userId, courseId);
        return String.format("redirect:%s", PREF_ADMIN_COURSES);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        courseListerService.deleteById(id);
        return String.format("redirect:%s", PREF_ADMIN_COURSES);
    }

    @ExceptionHandler
    public ModelAndView notFoundExceptionHandler(CourseNotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("not_found");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

    @ExceptionHandler
    public ResponseEntity<Void> resourceNotFoundExceptionHandler(ResourceNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}
