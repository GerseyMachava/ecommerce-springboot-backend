package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.ResponseDto.LoginResponseDto;
import com.ecommerce.backend.dto.ResponseDto.UserResponseDto;
import com.ecommerce.backend.security.AuthenticationService;
import com.ecommerce.backend.security.SecurityService;
import com.ecommerce.backend.shared.apiResponse.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ecommerce.backend.dto.requestDto.AuthenticationRequestDto;
import com.ecommerce.backend.dto.requestDto.RegisterRequestDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
@RestController
@RequestMapping("auth")
@AllArgsConstructor
public class AuthenticationController {
    private AuthenticationService authService;
    private SecurityService securityService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@RequestBody @Valid AuthenticationRequestDto data) {
        return ResponseEntity.status(
                HttpStatus.OK).body(
                        ApiResponse.success("Logged in successfuly", authService.login(data), HttpStatus.OK));

    }
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<ApiResponse<UserResponseDto>> register(
            @RequestBody @Valid RegisterRequestDto registerRequestDto) {
        return ResponseEntity.status(
                HttpStatus.OK).body(
                        ApiResponse.success("registed successfuly", authService.register(registerRequestDto),
                                HttpStatus.OK));

    }

   @GetMapping("/getUserName")
    public ResponseEntity<String> getUserName() {
        return securityService.getAuthenticatedUser()
                .map(user -> ResponseEntity.ok(user.getUsername())) // Retorna apenas o nome
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

}
