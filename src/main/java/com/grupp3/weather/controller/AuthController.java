package com.grupp3.weather.controller;
import com.grupp3.weather.model.User;
import com.grupp3.weather.security.JwtUtil;
import com.grupp3.weather.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")  // ← ÄNDRAT FRÅN /auth
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, JwtUtil jwtUtil, 
                         AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(request.username, request.email, request.password);
            
            Map<String, Object> response = Map.of(
                "message", "User registered successfully",
                "username", user.getUsername(),
                "email", user.getEmail()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username, request.password)
            );

            User user = userService.findByUsername(request.username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());

            Map<String, Object> response = Map.of(
                "message", "Login successful",
                "token", token,
                "username", user.getUsername(),
                "roles", user.getRoles()
            );

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            Map<String, Object> error = Map.of("error", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    // Inner classes for request DTOs
    public static class RegisterRequest {
        public String username;
        public String email;
        public String password;
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }
}
