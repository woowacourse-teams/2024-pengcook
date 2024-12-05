package net.pengcook.recipe.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import java.util.Objects;
import java.util.stream.Stream;
import net.pengcook.recipe.exception.InvalidParameterException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record PageRecipeRequest(
        @Min(0) int pageNumber,
        @Min(1) int pageSize,
        @Nullable String category,
        @Nullable String keyword,
        @Nullable Long userId
) {

    public PageRecipeRequest {
        long offset = (long) pageNumber * pageSize;
        if (offset > Integer.MAX_VALUE) {
            throw new InvalidParameterException("적절하지 않은 페이지 정보입니다.");
        }
    }

    public Pageable getPageable() {
        return PageRequest.of(pageNumber, pageSize);
    }

    public long getConditionCount() {
        return Stream.of(category, keyword, userId)
                .filter(Objects::nonNull)
                .count();
    }
}
