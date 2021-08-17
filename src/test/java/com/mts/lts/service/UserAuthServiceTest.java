package com.mts.lts.service;

import com.mts.lts.Constants;
import com.mts.lts.domain.Role;
import com.mts.lts.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAuthServiceTest {

    private UserAuthService userAuthService;
    private User user;
    private static final String username = "dima";

    @BeforeEach
    public void setUpForEach() {
        UserListerService userListerService = Mockito.mock(UserListerService.class);
        Role role = new Role(Constants.STUDENT_ROLE);
        user = new User(username);
        user.setPassword("123");
        user.setRoles(Collections.singleton(role));
        userAuthService = new UserAuthService(userListerService);
        Mockito.when(userListerService.findByUsername("dima")).thenReturn(user);
    }

    @Test
    public void loadUserByUsernameTest() {
        UserDetails userDetails = userAuthService.loadUserByUsername(username);
        assertEquals(userDetails.getUsername(), user.getUsername());
        assertEquals(userDetails.getPassword(), user.getPassword());
        assertEquals(userDetails.getAuthorities().size(), 1);
        assertTrue(
                userDetails.getAuthorities().contains(new SimpleGrantedAuthority(Constants.STUDENT_ROLE)));
    }
}
