package com.mts.lts.controller;

import com.mts.lts.dto.UserDto;
import com.mts.lts.mapper.UserMapper;
import com.mts.lts.service.UserListerService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class AccessController {

    @GetMapping("/access_denied")
    public String accessDenied() {
        return "access_denied";
    }


    private UserListerService userService;
    private UserMapper userMapper;

    public AccessController(UserListerService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "sign_up";
    }

    @PostMapping("/registration")
    public String addUser(@Valid UserDto userForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "sign_up";
        }
//        if (!userService.save(userMapper.dtoToDomain(userForm))){
//            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
//            return "registration";
//        }
        userService.save(userMapper.dtoToDomain(userForm));

        return "redirect:/courses";
    }


}
