package com.mts.lts.controller;

import com.mts.lts.Constants;
import com.mts.lts.domain.Role;
import com.mts.lts.domain.User;
import com.mts.lts.dto.UserDto;
import com.mts.lts.mapper.UserMapper;
import com.mts.lts.service.AvatarStorageService;
import com.mts.lts.service.RoleListerService;
import com.mts.lts.service.UserListerService;
import com.mts.lts.service.errors.InternalServerError;
import com.mts.lts.service.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.management.relation.RoleNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserListerService userListerService;
    private final RoleListerService roleListerService;
    private final AvatarStorageService avatarStorageService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(
            UserListerService userListerService,
            RoleListerService roleListerService,
            AvatarStorageService avatarStorageService,
            UserMapper userMapper
    ) {
        this.userListerService = userListerService;
        this.roleListerService = roleListerService;
        this.avatarStorageService = avatarStorageService;
        this.userMapper = userMapper;
    }

    @ModelAttribute("roles")
    public List<Role> rolesAttribute() {
        return roleListerService.findAll();
    }

    @GetMapping("/new")
    public String userForm(
            Model model
    ) {
        model.addAttribute("user", new UserDto());
        return "create_user";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public String userForm(
            Model model,
            Principal principal
    ) {
        String username = principal.getName();
        UserDto userDto = userMapper.domainToDto(userListerService.findByUsername(username));
        model.addAttribute("user", userDto);
        return "edit_user";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/avatar")
    @ResponseBody
    public ResponseEntity<byte[]> avatarImage(Principal principal) {
        String contentType = avatarStorageService.getContentTypeByUser(principal.getName());
        byte[] data = avatarStorageService.getAvatarImageByUser(principal.getName());
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/avatar")
    public String updateAvatarImage(
            @RequestParam("avatar") MultipartFile avatar,
            Principal principal
    ) {
        if (!avatar.isEmpty()) {
            try {
                avatarStorageService.save(
                        principal.getName(),
                        avatar.getContentType(),
                        avatar.getInputStream()
                );
            } catch (Exception ex) {
                throw new InternalServerError(ex);
            }
        }
        return "redirect:/user/me";
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me/avatar")
    public String deleteAvatarImage(
            Principal principal
    ) {
        avatarStorageService.deleteAvatarImageByUser(principal.getName());
        return "redirect:/user/me";
    }

    @PostMapping
    public String submitUserForm(
            @Valid @ModelAttribute("user") UserDto user,
            BindingResult bindingResult,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws RoleNotFoundException {
        if (bindingResult.hasErrors()) {
            if (user.getId() != null) {
                List<FieldError> errorsToKeep = bindingResult.getFieldErrors().stream()
                        .filter(fer -> !fer.getField().equals("password"))
                        .collect(Collectors.toList());
                bindingResult = new BeanPropertyBindingResult(user, "user");
                for (FieldError error : errorsToKeep) {
                    bindingResult.addError(error);
                }
            }

            if (user.getId() == null) {
                return "create_user";
            } else {
                return "edit_user";
            }
        }

        String redirectPath;
        if (request.isUserInRole(Constants.ADMIN_ROLE)) {
            redirectPath = "redirect:/admin/user";
        } else {
            Long userId = user.getId();
            if (userId != null) {
                // prevent post of other user
                User savedUser = userListerService.getOne(userId);
                if (!savedUser.getUsername().equals(request.getUserPrincipal().getName())) {
                    response.setStatus(403);
                    return "forward:/access_denied";
                }
                // prevent role setting by user
                user.setRoles(userListerService.getOne(userId).getRoles());
            } else {
                Role studentRole = roleListerService.findByName(Constants.STUDENT_ROLE);
                user.setRoles(Collections.singleton(studentRole));
            }
            redirectPath = "redirect:/course";
        }

        userListerService.save(userMapper.dtoToDomain(user));
        return redirectPath;
    }

    @ExceptionHandler
    public ModelAndView notFoundExceptionHandler(UserNotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("not_found");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

    @ExceptionHandler
    public ModelAndView internalServerErrorHandler(InternalServerError error) {
        logger.error(error.getMessage());
        ModelAndView modelAndView = new ModelAndView("internal_error");
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return modelAndView;
    }
}
