package com.mts.lts.controller;

import com.mts.lts.domain.Role;
import com.mts.lts.dto.UserDto;
import com.mts.lts.mapper.UserMapper;
import com.mts.lts.service.RoleListerService;
import com.mts.lts.service.UserListerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final RoleListerService roleListerService;
    private final UserListerService userListerService;
    private final UserMapper userMapper;

    @Autowired
    public AdminUserController(
            RoleListerService roleListerService,
            UserListerService userListerService,
            UserMapper userMapper
    ) {
        this.roleListerService = roleListerService;
        this.userListerService = userListerService;
        this.userMapper = userMapper;
    }

    @ModelAttribute("roles")
    public List<Role> rolesAttribute() {
        return roleListerService.findAll();
    }

    @GetMapping("/{id}")
    public String userForm(
            Model model,
            @PathVariable("id") Long id
    ) {
        UserDto userDto = userMapper.domainToDto(userListerService.getOne(id));
        model.addAttribute("user", userDto);
        return "edit_user";
    }

    @GetMapping
    public String userTable(Model model) {
        model.addAttribute("users", userMapper.domainToDto(userListerService.findAll()));
        return "users";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userListerService.deleteById(id);
        return "redirect:/admin/users";
    }
}
