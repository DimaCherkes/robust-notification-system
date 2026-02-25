package com.dmytrocherkes.iamservice.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@Schema(description = "Standard API response wrapper")
public class DefaultApiResponse<R extends Serializable> implements Serializable {

    @Schema(description = "Descriptive message about the response status", example = "Operation successful")
    private String message;

    @Schema(description = "Response payload")
    private R body;

    @Schema(description = "Indicates if the operation was successful", example = "true")
    boolean success;

    public static <R extends Serializable> DefaultApiResponse<R> createSuccessfulResponse(R body) {
        return new DefaultApiResponse<>(StringUtils.EMPTY, body, true);
    }

    public static <R extends Serializable> DefaultApiResponse<R> createSuccessfulResponse(String message, R body) {
        return new DefaultApiResponse<>(message, body, true);
    }

    public static <R extends Serializable> DefaultApiResponse<R> createFailureResponse(String message, R body) {
        return new DefaultApiResponse<>(message, body, false);
    }

    public static <R extends Serializable> DefaultApiResponse<R> createFailureResponse(String message) {
        return new DefaultApiResponse<>(message, null, false);
    }

}
