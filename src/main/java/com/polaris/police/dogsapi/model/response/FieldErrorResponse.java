package com.polaris.police.dogsapi.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FieldErrorResponse(
        @JsonProperty("field") String field,
        @JsonProperty("message") String message
) {
}
