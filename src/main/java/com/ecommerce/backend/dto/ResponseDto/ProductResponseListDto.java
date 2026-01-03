package com.ecommerce.backend.dto.ResponseDto;

import java.util.List;

public record ProductResponseListDto(
        List<ProductResponseDto> productResponseDto,
        int totalItemsPerPage,
        long totalItems,
        int totalPages,
        int currentPage
       

) {

}
