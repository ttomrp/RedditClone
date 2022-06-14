package com.redditclone.reddit.service;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.redditclone.reddit.dto.RegisterRequest;
import com.redditclone.reddit.model.NotificationEmail;
import com.redditclone.reddit.model.User;
import com.redditclone.reddit.model.VerificationToken;
import com.redditclone.reddit.repository.UserRepository;
import com.redditclone.reddit.repository.VerificationTokenRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    private final MailService mailService;

    @Transactional
    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEnabled(false);

        userRepository.save(user);

        String token = generateVerificationToken(user);

        mailService.sendMail(new NotificationEmail(
                "Please activate your account",
                user.getEmail(),
                "Please click the below URL to activate your account: " +
                        "http://localhost:8080/api/auth/accountVerification/" + token));
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }
}
