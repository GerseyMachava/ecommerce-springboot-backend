package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.ResponseDto.LoginResopnseDto;
import com.ecommerce.backend.security.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.backend.dto.requestDto.AuthenticationRequestDto;
import com.ecommerce.backend.dto.requestDto.RegisterRequestDto;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("auth")
@AllArgsConstructor
public class AuthenticationController {
    private TokenService tokenService;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationRequestDto data) {

        var userNamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(userNamePassword);
        var token = tokenService.generateToken((User)auth.getPrincipal());
        return ResponseEntity.ok(new LoginResopnseDto(token));
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity register(@RequestBody @Valid RegisterRequestDto registerRequestDto) {
        if (this.userRepository.findByEmail(registerRequestDto.email()) != null)
            return ResponseEntity.badRequest().build();
        String encryptedPassword = new BCryptPasswordEncoder().encode(registerRequestDto.password());
        User newUser = new User(registerRequestDto.email(), encryptedPassword, registerRequestDto.role());
        this.userRepository.save(newUser);
        return ResponseEntity.ok().build();
    }

}
