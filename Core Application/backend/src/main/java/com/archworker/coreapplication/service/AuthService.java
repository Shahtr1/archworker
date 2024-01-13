package com.archworker.coreapplication.service;

import com.archworker.coreapplication.dto.SignupDTO;

public interface AuthService {
    boolean createUser(SignupDTO signupDTO);
}
