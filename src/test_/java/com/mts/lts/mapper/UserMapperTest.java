package com.mts.lts.mapper;

import com.mts.lts.Constants;
import com.mts.lts.domain.Role;
import com.mts.lts.domain.User;
import com.mts.lts.dto.UserDto;
import com.mts.lts.service.UserListerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Component
public class UserMapperTest {

    private PasswordEncoder encoderMock;
    private UserListerService userListerServiceMock;
    private UserMapper userMapper;
    private static User user;
    private static Role role;
    private User emptyUser;
    private UserDto userDto;

    private static final long userId = 2;
    private static final String testUsername = "username";
    private static final String testPassword = "password";
    private static final String testEncodedPassword = "encoded";

    @BeforeAll
    public static void setUp() {
        role = new Role(Constants.STUDENT_ROLE);
        user = new User();
        user.setId(userId);
        user.setUsername(testUsername);
        user.setPassword(testEncodedPassword);
        user.setRoles(Collections.singleton(role));
    }

    @BeforeEach
    public void setUpForEach() {
        emptyUser = new User();
        emptyUser.setId(userId);
        userDto = new UserDto(userId, testUsername, testPassword, Collections.singleton(role));
        userListerServiceMock = Mockito.mock(UserListerService.class);
        Mockito.when(userListerServiceMock.getOne(userId)).thenReturn(emptyUser);
        encoderMock = Mockito.mock(PasswordEncoder.class);
        Mockito.when(encoderMock.encode(testPassword)).thenReturn(testEncodedPassword);
        userMapper = new UserMapper(userListerServiceMock, encoderMock);
    }

    @Test
    public void dtoWithPasswordToDomainExistsTest() {
        User convertedUser = userMapper.dtoToDomain(userDto);
        assertEquals(convertedUser, user);
    }

    @Test
    public void dtoNoPasswordToDomainExistsTest() {
        userDto.setPassword("");
        emptyUser.setPassword(testEncodedPassword);
        User convertedUser = userMapper.dtoToDomain(userDto);
        assertEquals(convertedUser, user);
    }

    @Test
    public void dtoToDomainNewTest() {
        UserDto newUserDto = new UserDto(null, testUsername, testPassword, Collections.singleton(role));
        User convertedUser = userMapper.dtoToDomain(newUserDto);
        assertEquals(convertedUser.getUsername(), testUsername);
        assertEquals(convertedUser.getPassword(), testEncodedPassword);
        assertEquals(convertedUser.getRoles(), Collections.singleton(role));
        assertNotEquals(convertedUser.getId(), user.getId());
    }

    @Test
    public void domainToDtoTest() {
        userDto.setPassword("");
        UserDto convertedUserDto = userMapper.domainToDto(user);
        assertEquals(convertedUserDto, userDto);
    }
}
