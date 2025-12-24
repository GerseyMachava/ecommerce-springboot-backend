package com.ecommerce.backend.dto.requestDto;

import com.ecommerce.backend.model.enums.UserRole;

public record RegisterRequestDto(String email, String password, UserRole role) {

}
