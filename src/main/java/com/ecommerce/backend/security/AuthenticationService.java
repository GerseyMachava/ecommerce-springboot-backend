package com.ecommerce.backend.security;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.backend.dto.ResponseDto.LoginResponseDto;
import com.ecommerce.backend.dto.ResponseDto.UserResponseDto;
import com.ecommerce.backend.dto.requestDto.AuthenticationRequestDto;
import com.ecommerce.backend.dto.requestDto.RegisterRequestDto;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.shared.exception.BusinessException;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Service
public class AuthenticationService {
    private TokenService tokenService;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDto login(AuthenticationRequestDto dataDto) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(dataDto.email(), dataDto.password());
        var auth = authenticationManager.authenticate(userNamePassword);
        var token = tokenService.generateToken((User) auth.getPrincipal());
        return new LoginResponseDto(dataDto.email(), token);
    }

    public UserResponseDto register(RegisterRequestDto dto) {
        if (userRepository.findByEmail(dto.email()) != null)
            throw new BusinessException("User: " + dto.email() + " already exists", HttpStatus.CONFLICT);
        User newUser = User.builder()
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role(dto.role())
                .build();
        userRepository.save(newUser);
        return new UserResponseDto(newUser.getEmail(), newUser.getRole());
    }

}
