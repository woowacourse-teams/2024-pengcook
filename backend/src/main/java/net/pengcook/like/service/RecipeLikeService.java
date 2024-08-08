package net.pengcook.like.service;

import jakarta.transaction.Transactional;
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

@Service
@RequiredArgsConstructor
public class RecipeLikeService {

    private final RecipeLikeRepository likeRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    public RecipeLikeResponse readLike(UserInfo userInfo, long recipeId) {
        boolean isLike = likeRepository.existsByUserIdAndRecipeId(userInfo.getId(), recipeId);

        return new RecipeLikeResponse(isLike);
    }

    @Transactional
    public void addLike(UserInfo userInfo, long recipeId) {
        if (likeRepository.existsByUserIdAndRecipeId(userInfo.getId(), recipeId)) {
            return;
        }

        User user = userRepository.findById(userInfo.getId())
                .orElseThrow(UserNotFoundException::new);
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(RecipeNotFoundException::new);

        recipe.increaseLikeCount();

        likeRepository.save(new RecipeLike(user, recipe));
        recipeRepository.save(recipe);
    }

    @Transactional
    public void deleteLike(UserInfo userInfo, long recipeId) {
        if (!likeRepository.existsByUserIdAndRecipeId(userInfo.getId(), recipeId)) {
            return;
        }

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(RecipeNotFoundException::new);

        recipe.decreaseLikeCount();

        likeRepository.deleteByUserIdAndRecipeId(userInfo.getId(), recipeId);
        recipeRepository.save(recipe);
    }
}
