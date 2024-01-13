package com.archworker.coreapplication.service;

import com.archworker.coreapplication.dto.SignupDTO;
import com.archworker.coreapplication.entity.User;
import com.archworker.coreapplication.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean createUser(SignupDTO signupDTO) {
        if (userRepository.existsByEmail(signupDTO.getEmail())) {
            return false;
        }
        User user = new User();
        BeanUtils.copyProperties(signupDTO, user);

        String hashPassword = passwordEncoder.encode(signupDTO.getPassword());
        user.setPassword(hashPassword);

        userRepository.save(user);
        return true;
    }
}
