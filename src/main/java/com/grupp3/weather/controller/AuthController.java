package com.grupp3.weather.controller;

import com.grupp3.weather.dto.UserDTO;
import com.grupp3.weather.mapper.UserMapper;
import com.grupp3.weather.model.User;
import com.grupp3.weather.security.JwtUtil;
import com.grupp3.weather.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    public AuthController(UserService userService, JwtUtil jwtUtil, 
                         AuthenticationManager authenticationManager,
                         UserMapper userMapper) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(request.username, request.email, request.password);
            UserDTO userDTO = userMapper.toDTO(user);
            
            Map<String, Object> response = Map.of(
                "message", "User registered successfully",
                "user", userDTO
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/register-admin")
    public ResponseEntity<Map<String, Object>> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.registerAdmin(request.username, request.email, request.password);
            UserDTO userDTO = userMapper.toDTO(user);
            
            Map<String, Object> response = Map.of(
                "message", "Admin user registered successfully",
                "user", userDTO
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username, request.password)
            );

            User user = userService.findByUsername(request.username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());
            UserDTO userDTO = userMapper.toDTO(user);

            Map<String, Object> response = Map.of(
                "message", "Login successful",
                "token", token,
                "user", userDTO
            );

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            Map<String, Object> error = Map.of("error", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    public static class RegisterRequest {
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        public String username;
        
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        public String email;
        
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        public String password;
    }

    public static class LoginRequest {
        @NotBlank(message = "Username is required")
        public String username;
        
        @NotBlank(message = "Password is required")
        public String password;
    }
}
