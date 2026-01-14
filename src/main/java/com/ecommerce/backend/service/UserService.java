package com.ecommerce.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.backend.dto.ResponseDto.UserResponseDto;
import com.ecommerce.backend.dto.requestDto.RegisterRequestDto;
import com.ecommerce.backend.dto.requestDto.UserPasswordUpdateRequestDto;
import com.ecommerce.backend.mapper.UserMapper;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.model.enums.UserRole;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.security.SecurityService;
import com.ecommerce.backend.shared.exception.BusinessException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;

    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toListResponseDto(users);
    }

    public UserResponseDto updateUser(long userId, RegisterRequestDto authDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("No User Found with id " + userId, HttpStatus.NOT_FOUND));

        if (userRepository.findByEmail(authDto.email()) != null) {
            throw new BusinessException("Email already in use", HttpStatus.CONFLICT);
        }
        user.setEmail(authDto.email());
        user.setPassword(passwordEncoder.encode(authDto.password()));
        user.setRole(authDto.role());
        userRepository.save(user);
        return new UserResponseDto(user.getEmail(), user.getRole());

    }

    public void PasswordUpdate(UserPasswordUpdateRequestDto dto) {
        User userToUpdate = userRepository.findById(dto.userId())
                .orElseThrow(
                        () -> new BusinessException("No User Found with id " + dto.userId(), HttpStatus.NOT_FOUND));
        User loggedUser = securityService.getAuthenticatedUser()
                .orElseThrow(
                        () -> new BusinessException("No User Found with id " + dto.userId(), HttpStatus.NOT_FOUND));
        boolean isOwner = userToUpdate.equals(loggedUser);
        boolean isAdmin = loggedUser.getRole() == UserRole.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new BusinessException("You can only update your own password", HttpStatus.CONFLICT);
        }
        if (isOwner) {
            if (!passwordEncoder.matches(dto.actualPassword(), loggedUser.getPassword())) {
                throw new BusinessException("Incorrect Password", HttpStatus.UNAUTHORIZED);
            }
        }

        userToUpdate.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(userToUpdate);
    }

    public UserResponseDto toggleUserLock(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("No User Found with id " + userId, HttpStatus.NOT_FOUND));

        user.setLocked(!user.isLocked());
        return userMapper.toResponseDto(userRepository.save(user));

    }

}
