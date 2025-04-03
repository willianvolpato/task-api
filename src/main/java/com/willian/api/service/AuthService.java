package com.willian.api.service;

import com.willian.api.config.JwtTokenProvider;
import com.willian.api.model.AuthRequest;
import com.willian.api.model.AuthResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse authenticate(AuthRequest authRequest) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.username(),
                authRequest.password()
            )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.username());
        String token = jwtTokenProvider.generateToken(userDetails);

        return new AuthResponse(token);
    }
}
