package net.pengcook.ingredient.dto;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import net.pengcook.ingredient.domain.Requirement;

class IngredientCreateRequestTest {

    static Stream<Arguments> substitutions() {
        List<String> nullSubstitution=new ArrayList<>();
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
    @DisplayName("재료 이름에 공백 또는 null은 허용하지 않는다.")
    void validateName(String name) {
        assertThatThrownBy(() -> new IngredientCreateRequest(name, Requirement.ALTERNATIVE, List.of("wheat")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("substitutions")
    @DisplayName("ALTERNATIVE 상태일 때, 대체 재료 이름에 공백 또는 null은 허용하지 않는다.")
    void validateSubstitutionName(List<String> substitutions) {
        assertThatThrownBy(() -> new IngredientCreateRequest("rice", Requirement.ALTERNATIVE, substitutions))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("ALTERNATIVE 상태일 때, 대체 재료 리스트에 null은 허용하지 않는다.")
    void validateSubstitutionIsNull() {
        assertThatThrownBy(() -> new IngredientCreateRequest("rice", Requirement.ALTERNATIVE, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
