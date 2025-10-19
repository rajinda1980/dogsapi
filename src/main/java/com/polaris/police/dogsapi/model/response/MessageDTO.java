package com.polaris.police.dogsapi.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MessageDTO(
        @JsonProperty("timestamp") LocalDateTime timestamp,
        @JsonProperty("status") Integer status,
        @JsonProperty("error") String error,
        @JsonProperty("message") String message,
        @JsonProperty("fieldErrors") List<FieldErrorResponse> fieldErrors,
        @JsonProperty("path") String path
) {
}
