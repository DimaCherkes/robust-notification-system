package com.dmytrocherkes.iamservice.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "Paginated response wrapper")
public class PaginationResponse<T> implements Serializable {

    @Schema(description = "List of items for the current page")
    private List<T> content;

    @Schema(description = "Pagination metadata")
    private Pagination pagination;

    @Getter
    @AllArgsConstructor
    @Schema(description = "Pagination details")
    public static class Pagination implements Serializable {
        @Schema(description = "Total number of elements across all pages", example = "100")
        private Long total;

        @Schema(description = "Number of elements per page", example = "10")
        private Integer limit;

        @Schema(description = "Current page number (1-based)", example = "1")
        private Integer page;

        @Schema(description = "Total number of pages", example = "10")
        private Integer pages;
    }
}
