package net.pengcook.like.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.like.domain.RecipeLike;
import net.pengcook.like.dto.RecipeLikeResponse;
import net.pengcook.like.exception.RecipeNotFoundException;
import net.pengcook.like.exception.UserNotFoundException;
import net.pengcook.like.repository.RecipeLikeRepository;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.recipe.repository.RecipeRepository;
import net.pengcook.user.domain.User;
import net.pengcook.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecipeLikeService {

    private final RecipeLikeRepository likeRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    public RecipeLikeResponse readLikesCount(long recipeId) {
        int likesCount = recipeRepository.findById(recipeId).stream()
                .mapToInt(Recipe::getLikeCount)
                .findAny()
                .orElseThrow(RecipeNotFoundException::new);

        return new RecipeLikeResponse(likesCount);
    }

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
                .orElseThrow(UserNotFoundException::new);
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(RecipeNotFoundException::new);

        recipe.increaseLikeCount();

        likeRepository.save(new RecipeLike(user, recipe));
        recipeRepository.save(recipe);
    }
}
