package com.mts.lts.dto;

import com.mts.lts.domain.Role;
import com.mts.lts.service.UserListerService;
import com.mts.lts.validation.unique.Unique;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class UserDto {

    private Long id;

    @NotBlank(message = "Username has to be filled")
    @Unique(service = UserListerService.class, fieldName = "username", message = "This username is already used")
    @Email
    private String email;

    @NotBlank(message = "Password has to be filled")
    private String password;

    private boolean hasAvatarImage;

    private Set<Role> roles;

    public UserDto() {
        roles = new HashSet<>();
    }

    public UserDto(
            Long id,
            String email,
            String password
    ) {
        this();
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public UserDto(
            Long id,
            String email,
            String password,
            Set<Role> roles
    ) {
        this(id, email, password);
        this.roles = roles;
    }

    public UserDto(
            Long id,
            String email,
            String password,
            Set<Role> roles,
            boolean hasAvatarImage
    ) {
        this(id, email, password, roles);
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
