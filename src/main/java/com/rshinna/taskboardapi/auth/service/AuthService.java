package com.rshinna.taskboardapi.auth.service;

import com.rshinna.taskboardapi.auth.dto.LoginRequestDTO;
import com.rshinna.taskboardapi.auth.dto.LoginResponseDTO;
import com.rshinna.taskboardapi.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResponseDTO login(LoginRequestDTO request){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        String token = jwtService.generateToken(request.email());

        return new LoginResponseDTO(token);
    }
}
