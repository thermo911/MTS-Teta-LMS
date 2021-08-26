package com.mts.lts.service;

import com.mts.lts.Constants;
import com.mts.lts.domain.Role;
import com.mts.lts.dao.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@AutoConfigureTestDatabase
public class RoleListerServiceTest {

    @Autowired
    private RoleListerService roleListerService;
    @Autowired
    private RoleRepository roleRepository;

    private Role testRole1;
    private Role testRole2;

    @BeforeEach
    void setUpForEach() {
        testRole1 = new Role(Constants.STUDENT_ROLE);
        testRole2 = new Role(Constants.ADMIN_ROLE);
        roleRepository.deleteAll();
    }

    @Test
    public void findAllTest() {
        roleRepository.save(testRole1);
        roleRepository.save(testRole2);
        List<Role> res = roleListerService.findAll();
        assertEquals(res.size(), 2);
    }

    @Test
    public void findByNameSuccessTest() throws RoleNotFoundException {
        roleRepository.save(testRole1);
        roleRepository.save(testRole2);
        Role res = roleListerService.findByName(testRole1.getName());
        assertEquals(res, testRole1);
    }

    @Test
    public void findByNameFailTest() {
        roleRepository.save(testRole1);
        roleRepository.save(testRole2);
        assertThrows(RoleNotFoundException.class, () -> roleListerService.findByName("ROLE_NULL"));
    }
}
