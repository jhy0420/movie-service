package com.vinsguru.netflux.movie.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.util.StringUtils;

import java.util.Objects;

public record MovieSearchRequest(String title,
                                 String genre,
                                 String director,
                                 @Min(1) @Max(100) Integer limit) {

    public MovieSearchRequest {
        limit = Objects.isNull(limit) ? 20 : limit;
    }

    @AssertTrue(message = "At least one of title, genre, or director is required")
    public boolean isAtLeastOneCriterionPresent() {
        return StringUtils.hasText(title) || StringUtils.hasText(genre) || StringUtils.hasText(director);
    }
}
