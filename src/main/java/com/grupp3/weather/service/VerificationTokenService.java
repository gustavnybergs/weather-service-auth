package com.grupp3.weather.service;

import com.grupp3.weather.model.User;
import com.grupp3.weather.model.VerificationToken;
import com.grupp3.weather.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;

    public VerificationTokenService(VerificationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String createVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        tokenRepository.save(verificationToken);
        return token;
    }

    public Optional<VerificationToken> getVerificationToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public void markAsVerified(VerificationToken token) {
        token.setVerifiedAt(LocalDateTime.now());
        tokenRepository.save(token);
    }
}
