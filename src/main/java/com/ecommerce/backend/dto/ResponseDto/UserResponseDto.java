package com.ecommerce.backend.dto.ResponseDto;

import com.ecommerce.backend.model.enums.UserRole;

public record UserResponseDto(String email, UserRole role) {

}
