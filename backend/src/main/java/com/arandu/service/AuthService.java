package com.arandu.service;

import com.arandu.domain.UserAccount;
import com.arandu.dto.AuthDtos;
import com.arandu.repository.UserAccountRepository;
import com.arandu.security.JwtService;
import com.arandu.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (userAccountRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalStateException("Ya existe una cuenta con ese correo");
        }
        UserAccount user = new UserAccount();
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userAccountRepository.save(user);
        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return new AuthDtos.AuthResponse(token, toUser(user));
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password()));
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        String token = jwtService.generateToken(principal.getId(), principal.getUsername());
        return new AuthDtos.AuthResponse(
                token,
                new AuthDtos.UserResponse(principal.getId(), principal.getFullName(), principal.getUsername()));
    }

    public AuthDtos.UserResponse me(UserPrincipal principal) {
        return new AuthDtos.UserResponse(principal.getId(), principal.getFullName(), principal.getUsername());
    }

    private AuthDtos.UserResponse toUser(UserAccount user) {
        return new AuthDtos.UserResponse(user.getId(), user.getFullName(), user.getEmail());
    }
}
