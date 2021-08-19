package com.mts.lts.service;

import com.mts.lts.domain.Role;
import com.mts.lts.dao.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@Component
public class RoleListerService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleListerService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role findByName(String name) throws RoleNotFoundException {
        return roleRepository.findRoleByName(name)
                .orElseThrow(() -> new RoleNotFoundException("Not found role: " + name));
    }
}
