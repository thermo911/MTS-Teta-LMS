package com.mts.lts.dto;

import com.mts.lts.domain.Role;
import com.mts.lts.service.UserListerService;
import com.mts.lts.validation.unique.Unique;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UserDto {

    private Long id;

    @NotBlank(message = "Username has to be filled")
    @Unique(service = UserListerService.class, fieldName = "username", message = "This username is already used")
    private String username;

    @NotBlank(message = "Password has to be filled")
    private String password;

    private boolean hasAvatarImage;

    private Set<Role> roles;

    public UserDto() {
        roles = new HashSet<>();
    }

    public UserDto(
            Long id,
            String username,
            String password
    ) {
        this();
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public UserDto(
            Long id,
            String username,
            String password,
            Set<Role> roles
    ) {
        this(id, username, password);
        this.roles = roles;
    }

    public UserDto(
            Long id,
            String username,
            String password,
            Set<Role> roles,
            boolean hasAvatarImage
    ) {
        this(id, username, password, roles);
        this.hasAvatarImage = hasAvatarImage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean getHasAvatarImage() {
        return hasAvatarImage;
    }

    public void setHasAvatarImage(boolean hasAvatarImage) {
        this.hasAvatarImage = hasAvatarImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDto userDto = (UserDto) o;
        return Objects.equals(id, userDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
