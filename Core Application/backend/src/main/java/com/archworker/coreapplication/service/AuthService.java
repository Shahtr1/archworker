package com.archworker.coreapplication.service;

import com.archworker.coreapplication.dto.SignupDTO;

import javax.management.relation.RoleNotFoundException;

public interface AuthService {
    boolean createUser(SignupDTO signupDTO) throws RoleNotFoundException;
}
