package com.mts.lts.mapper;

import com.mts.lts.domain.User;
import com.mts.lts.dto.UserDto;
import com.mts.lts.service.UserListerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper extends AbstractMapper<UserDto, User, UserListerService> {

    private final PasswordEncoder encoder;

    @Autowired
    public UserMapper(
            UserListerService userListerService,
            PasswordEncoder encoder
    ) {
        super(userListerService);
        this.encoder = encoder;
    }

    @Override
    public User dtoToDomain(UserDto entityDto) {
        User user;
        Long entityDtoId = entityDto.getId();
        if (entityDtoId != null) {
            user = entityService.getOne(entityDtoId);
        } else {
            user = new User();
        }
        user.setEmail(entityDto.getUsername());
        // For ability to update roles without password change
        if (entityDtoId == null || !entityDto.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(entityDto.getPassword()));
        }
        user.setRoles(entityDto.getRoles());

        return user;
    }

    @Override
    public UserDto domainToDto(User entity) {
        return new UserDto(
                entity.getId(),
                entity.getEmail(),
                "",
                entity.getRoles(),
                entity.getImage() != null
        );
    }
}
