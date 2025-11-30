package com.grupp3.weather.controller;

import com.grupp3.weather.model.VerificationToken;
import com.grupp3.weather.service.UserService;
import com.grupp3.weather.service.VerificationTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class VerificationController {
    private static final Logger log = LoggerFactory.getLogger(VerificationController.class);

    private final VerificationTokenService verificationTokenService;
    private final UserService userService;

    public VerificationController(VerificationTokenService verificationTokenService, 
                                 UserService userService) {
        this.verificationTokenService = verificationTokenService;
        this.userService = userService;
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        log.info("Email verification attempt with token: {}", token);

        return verificationTokenService.getVerificationToken(token)
            .map(verificationToken -> {
                if (verificationToken.isExpired()) {
                    log.warn("Verification token expired: {}", token);
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "Token has expired"));
                }

                if (verificationToken.getVerifiedAt() != null) {
                    log.warn("Token already used: {}", token);
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "Token already used"));
                }

                userService.enableUser(verificationToken.getUser());
                verificationTokenService.markAsVerified(verificationToken);

                log.info("Email verified successfully for user: {}", 
                    verificationToken.getUser().getUsername());

                return ResponseEntity.ok(Map.of(
                    "message", "Email verified successfully",
                    "username", verificationToken.getUser().getUsername()
                ));
            })
            .orElseGet(() -> {
                log.warn("Invalid verification token: {}", token);
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid verification token"));
            });
    }
}
