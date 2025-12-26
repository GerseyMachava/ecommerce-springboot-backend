package com.ecommerce.backend.shared.apiResponse;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

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
    private List<String> error;

    public ApiResponse(ApiStatus status, String message, T data, Instant timestamp, String httpStatus) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.data = data;
        this.httpStatus = httpStatus;
    }

    public ApiResponse(ApiStatus status, String message, Instant timestamp,
            List<String> error) {
        this.status = status;
        this.message = message;

        this.timestamp = timestamp;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(boolean status, String message, T data, HttpStatusCode httpStatus) {
        ApiStatus apiStatus = status ? ApiStatus.SUCCESS : ApiStatus.ERROR;
        return new ApiResponse<>(
                apiStatus,
                message,
                data,
                Instant.now(),
                httpStatus.toString()

        );

    }

}
