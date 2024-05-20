package com.archworker.coreapplication.bootstrap;

import com.archworker.coreapplication.entity.Role;
import com.archworker.coreapplication.enums.RoleEnum;
import com.archworker.coreapplication.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args){
        if (roleRepository.findByRole(RoleEnum.USER).isEmpty()) {
            Role userRole = new Role();
            userRole.setRole(RoleEnum.USER);
            roleRepository.save(userRole);
        }
        if (roleRepository.findByRole(RoleEnum.ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setRole(RoleEnum.ADMIN);
            roleRepository.save(adminRole);
        }
    }
}
