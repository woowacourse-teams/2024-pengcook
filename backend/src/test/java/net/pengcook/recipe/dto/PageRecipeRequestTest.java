package net.pengcook.recipe.dto;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import net.pengcook.recipe.exception.InvalidParameterException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PageRecipeRequestTest {

    @Test
    @DisplayName("요청받은 페이지 offset 값이 int 타입의 최댓값을 초과하면 예외가 발생한다.")
    void readRecipesWhenPageOffsetIsGreaterThanIntMaxValue() {
        int pageNumber = 1073741824;
        int pageSize = 2;

        assertThatThrownBy(() -> new PageRecipeRequest(pageNumber, pageSize, null, null, null))
                .isInstanceOf(InvalidParameterException.class);
    }
}
