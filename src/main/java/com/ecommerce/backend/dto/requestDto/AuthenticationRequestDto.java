package com.ecommerce.backend.dto.requestDto;

public record AuthenticationRequestDto(
    String email,
    String password
) {

}
