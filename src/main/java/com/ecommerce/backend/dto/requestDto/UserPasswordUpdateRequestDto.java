package com.ecommerce.backend.dto.requestDto;

public record UserPasswordUpdateRequestDto(
    long userId,
    String actualPassword,
    String newPassword
) {

}
