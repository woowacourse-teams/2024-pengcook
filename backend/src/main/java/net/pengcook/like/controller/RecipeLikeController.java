package net.pengcook.like.controller;

import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.authentication.resolver.LoginUser;
import net.pengcook.like.dto.RecipeLikeResponse;
import net.pengcook.like.service.RecipeLikeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class RecipeLikeController {

    private final RecipeLikeService likeService;

    @GetMapping("/{recipeId}")
    public RecipeLikeResponse readLikesCount(@PathVariable("recipeId") long recipeId) {
        return likeService.readLikesCount(recipeId);
    }

    @PostMapping("/{recipeId}")
    public void toggleLike(@LoginUser UserInfo userInfo,
                           @PathVariable("recipeId") long recipeId) {
        likeService.toggleLike(userInfo, recipeId);
    }
}
