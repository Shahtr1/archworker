package com.archworker.coreapplication.service;

import com.archworker.coreapplication.dto.SignupDTO;
import com.archworker.coreapplication.entity.Role;
import com.archworker.coreapplication.entity.User;
import com.archworker.coreapplication.enums.RoleEnum;
import com.archworker.coreapplication.repository.RoleRepository;
import com.archworker.coreapplication.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public boolean createUser(SignupDTO signupDTO) throws RoleNotFoundException {
        if (userRepository.existsByEmail(signupDTO.getEmail())) {
            return false;
        }
        User user = new User();
        BeanUtils.copyProperties(signupDTO, user);

        String hashPassword = passwordEncoder.encode(signupDTO.getPassword());
        user.setPassword(hashPassword);

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByRole(RoleEnum.USER)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with name: " + RoleEnum.USER));
        roles.add(userRole);

        user.setRoles(roles);

        userRepository.save(user);
        return true;
    }
}
