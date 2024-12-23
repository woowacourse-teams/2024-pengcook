package net.pengcook.category.dto;

import net.pengcook.category.domain.Category;

public record CategoryResponse(long categoryId, String categoryName) {

    public CategoryResponse(Category category) {
        this(category.getId(), category.getName());
    }
}
