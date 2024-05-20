package com.archworker.coreapplication.integration.configuration;

import com.archworker.coreapplication.dto.HelloDTO;
import com.archworker.coreapplication.dto.JwtDTO;
import com.archworker.coreapplication.dto.LoginDTO;
import com.archworker.coreapplication.entity.Role;
import com.archworker.coreapplication.entity.User;
import com.archworker.coreapplication.enums.RoleEnum;
import com.archworker.coreapplication.repository.RoleRepository;
import com.archworker.coreapplication.repository.UserRepository;
import com.archworker.coreapplication.util.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class WebSecurityConfigurationIntTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private String adminToken;
    private String userToken;
    private HttpEntity<?> httpEntity;

    private Role adminRole;
    private Role userRole;
    private User adminUser;
    private User normalUser;

    @BeforeEach
    void setup() {

        // Create roles
         adminRole = new Role();
        adminRole.setRole(RoleEnum.ADMIN);
        roleRepository.save(adminRole);

         userRole = new Role();
        userRole.setRole(RoleEnum.USER);
        roleRepository.save(userRole);

        // Create admin user
         adminUser = createUser("admin", "admin@admin.com", "password", adminRole);

        // Create normal user
         normalUser = createUser("user", "user@user.com", "password", userRole);

        // Generate tokens for admin and user
        adminToken = generateToken(adminUser);
        userToken = generateToken(normalUser);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        httpEntity = new HttpEntity<>(null, headers);
    }

    private User createUser(String name, String email, String password, Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    private String generateToken(User user) {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(user.getEmail());
        loginDTO.setPassword("password");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginDTO> request = new HttpEntity<>(loginDTO, headers);

        ResponseEntity<JwtDTO> response = testRestTemplate.postForEntity("/login", request, JwtDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP Status 200 should have been returned");

        JwtDTO jwtDTO = response.getBody();
        assert jwtDTO != null;
        return jwtDTO.jwt();
    }

    private HttpEntity<?> getHttpEntityWithToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Authorization", "Bearer " + token);
        return new HttpEntity<>(null, headers);
    }

    @AfterEach
    void teardown(){
        userRepository.deleteById(normalUser.getId());
        userRepository.deleteById(adminUser.getId());
        roleRepository.deleteById(adminRole.getId());
        roleRepository.deleteById(userRole.getId());
    }


    // TODO: Add admin role test
//    @Test
//    @DisplayName("Should allow access to admin path with ADMIN role")
//    public void testAdminPathWithAdminRole() {
//        ResponseEntity<List<HelloDTO>> response =
//                testRestTemplate.exchange("/admin/somepath", HttpMethod.GET, getHttpEntityWithToken(adminToken),
//                        new ParameterizedTypeReference<>() {
//                        });
//
//        assertEquals(HttpStatus.OK, response.getStatusCode(),
//                "HTTP Status 200 should have been returned");
//    }

    @Test
    @DisplayName("Should forbid access to admin path without role")
    public void testAdminPathWithoutRole() {
        ResponseEntity<List<HelloDTO>> response =
                testRestTemplate.exchange("/admin/somepath", HttpMethod.GET, httpEntity,
                        new ParameterizedTypeReference<>() {
                        });

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(),
                "HTTP Status 403 should have been returned");
    }

    @Test
    @DisplayName("Should allow access to API path with USER role")
    public void testApiPathWithUserRole() {
        ResponseEntity<HelloDTO> response =
                testRestTemplate.exchange("/api/hello", HttpMethod.GET, getHttpEntityWithToken(userToken),
                        new ParameterizedTypeReference<>() {
                        });

//        Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "HTTP status code should be 200");
    }

    @Test
    @DisplayName("Should forbid access to API path without role")
    public void testApiPathWithoutRole() {
        ResponseEntity<List<HelloDTO>> response =
                testRestTemplate.exchange("/api/somepath", HttpMethod.GET, httpEntity,
                        new ParameterizedTypeReference<>() {
                        });

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(),
                "HTTP Status 403 should have been returned");
    }
}
