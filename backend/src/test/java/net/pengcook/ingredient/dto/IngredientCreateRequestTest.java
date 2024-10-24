package net.pengcook.ingredient.dto;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.pengcook.ingredient.domain.Requirement;
import net.pengcook.ingredient.exception.InvalidNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class IngredientCreateRequestTest {

    static Stream<Arguments> substitutions() {
        List<String> nullSubstitution = new ArrayList<>();
        nullSubstitution.add(null);

        return Stream.of(
                Arguments.arguments(List.of("", "wheat")),
                Arguments.arguments(List.of(" ", "wheat")),
                Arguments.arguments(nullSubstitution)
        );
    }

    @ParameterizedTest
    @CsvSource(value = {
            "''",
            "' '",
    })
    @DisplayName("재료 이름에 공백 또는 null이 입력되면 예외가 발생한다.")
    void validateName(String name) {
        assertThatThrownBy(() -> new IngredientCreateRequest(name, Requirement.ALTERNATIVE, List.of("wheat")))
                .isInstanceOf(InvalidNameException.class);
    }

    @ParameterizedTest
    @MethodSource("substitutions")
    @DisplayName("ALTERNATIVE 상태일 때, 대체 재료 이름에 공백 또는 null이 입력되면 예외가 발생한다.")
    void validateSubstitutionName(List<String> substitutions) {
        assertThatThrownBy(() -> new IngredientCreateRequest("rice", Requirement.ALTERNATIVE, substitutions))
                .isInstanceOf(InvalidNameException.class);
    }

    @Test
    @DisplayName("ALTERNATIVE 상태일 때, 대체 재료 리스트에 null이 입력되면 예외가 발생한다.")
    void validateSubstitutionIsNull() {
        assertThatThrownBy(() -> new IngredientCreateRequest("rice", Requirement.ALTERNATIVE, null))
                .isInstanceOf(InvalidNameException.class);
    }
}
