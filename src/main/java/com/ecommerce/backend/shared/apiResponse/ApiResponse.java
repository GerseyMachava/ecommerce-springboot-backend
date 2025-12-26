package com.ecommerce.backend.shared.apiResponse;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import com.ecommerce.backend.model.enums.ApiStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private ApiStatus status;
    private T data;
    private String message;
    private String httpStatus;
    private Instant timestamp;
    private Object error;

    public static <T> ApiResponse<T> success(String message, T data, HttpStatusCode httpStatus) {

        return new ApiResponse<>(
                ApiStatus.SUCCESS,
                data,
                message,
                httpStatus.toString(),
                Instant.now(),
                null

        );

    }

    public static <T> ApiResponse<T> error(String message, HttpStatusCode httpStatus, Object errorDetails) {

        return new ApiResponse<>(
                ApiStatus.ERROR,
                null,
                message,
                httpStatus.toString(),
                Instant.now(),
                errorDetails

        );

    }

}
