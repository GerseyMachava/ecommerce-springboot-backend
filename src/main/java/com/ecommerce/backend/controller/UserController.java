package com.ecommerce.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.backend.dto.ResponseDto.UserResponseDto;
import com.ecommerce.backend.dto.requestDto.RegisterRequestDto;
import com.ecommerce.backend.dto.requestDto.UserPasswordUpdateRequestDto;
import com.ecommerce.backend.service.UserService;
import com.ecommerce.backend.shared.apiResponse.ApiResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        List<UserResponseDto> usersList = userService.getAllUsers();
     
        String message = usersList.isEmpty() ? "No users found" : "All users fetched successfully";
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(message, usersList, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}/toggle-lock")
    public ResponseEntity<ApiResponse<UserResponseDto>> toggleUserLock(@PathVariable(name = "userId") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("User lock status updated", userService.toggleUserLock(userId), HttpStatus.OK));

    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@RequestBody @Valid UserPasswordUpdateRequestDto dto) {
         userService.PasswordUpdate(dto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("user password updated",null, HttpStatus.OK));

    }

    @PutMapping("{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    public ResponseEntity<ApiResponse<UserResponseDto>> userUpdate(@RequestBody @Valid RegisterRequestDto dto,
            @PathVariable(name = "userId") long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("User updated successfully", userService.updateUser(userId, dto), HttpStatus.OK));

    }

}
