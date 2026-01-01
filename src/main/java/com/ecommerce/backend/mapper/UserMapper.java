package com.ecommerce.backend.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ecommerce.backend.dto.ResponseDto.UserResponseDto;
import com.ecommerce.backend.dto.requestDto.AuthenticationRequestDto;
import com.ecommerce.backend.model.User;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserMapper {

    public UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(
                user.getEmail(),
                user.getRole());
    }

    public List<UserResponseDto> toListResponseDto(List<User> users) {
        List<UserResponseDto> response = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            response.add(
                    toResponseDto(
                            users.get(i)));
        }
        return response;
    }
}
