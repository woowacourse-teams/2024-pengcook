package net.pengcook.like.service;

import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.like.domain.RecipeLike;
import net.pengcook.like.exception.RecipeLikeException;
import net.pengcook.like.repository.RecipeLikeRepository;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.repository.RecipeRepository;
import net.pengcook.user.domain.User;
import net.pengcook.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipeLikeService {

    private final RecipeLikeRepository likeRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    @Transactional
    public void toggleLike(UserInfo userInfo, long recipeId) {
        Optional<RecipeLike> like = likeRepository.findByUserIdAndRecipeId(userInfo.getId(), recipeId);
        like.ifPresentOrElse(this::deleteLike, () -> addLike(userInfo.getId(), recipeId));
    }

    private void deleteLike(RecipeLike recipeLike) {
        Recipe recipe = recipeLike.getRecipe();

        recipe.decreaseLikeCount();

        likeRepository.delete(recipeLike);
        recipeRepository.save(recipe);
    }

    private void addLike(long userId, long recipeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RecipeLikeException(HttpStatus.NOT_FOUND, "존재하지 않는 유저 입니다."));
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeLikeException(HttpStatus.NOT_FOUND, "존재하지 않는 레시피 입니다."));

        recipe.increaseLikeCount();

        likeRepository.save(new RecipeLike(user, recipe));
        recipeRepository.save(recipe);
    }
}
