package com.grupp3.weather.service;

import com.grupp3.weather.model.Role;
import com.grupp3.weather.model.User;
import com.grupp3.weather.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final EmailPublisher emailPublisher;

    public UserService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      VerificationTokenService verificationTokenService,
                      EmailPublisher emailPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenService = verificationTokenService;
        this.emailPublisher = emailPublisher;
    }

    public User registerUser(String username, String email, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Set.of(Role.ROLE_USER));
        user.setEnabled(true); // Auto-enable for demo
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        String token = verificationTokenService.createVerificationToken(savedUser);
        
        log.info("Sending verification email to: {}", email);
        emailPublisher.publishVerificationEmail(email, username, token);

        return savedUser;
    }

    public User registerAdmin(String username, String email, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void enableUser(User user) {
        user.setEnabled(true);
        userRepository.save(user);
        log.info("User {} enabled successfully", user.getUsername());
    }
}
