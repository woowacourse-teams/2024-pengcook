package net.pengcook.like.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.authentication.resolver.LoginUser;
import net.pengcook.like.dto.RecipeLikeRequest;
import net.pengcook.like.dto.RecipeLikeResponse;
import net.pengcook.like.service.RecipeLikeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class RecipeLikeController {

    private final RecipeLikeService likeService;

    @GetMapping("/{recipeId}")
    public RecipeLikeResponse readLikesCount(
            @LoginUser UserInfo userInfo,
            @PathVariable("recipeId") long recipeId
    ) {
        return likeService.readLikesCount(userInfo, recipeId);
    }

    @PostMapping
    public void toggleLike(
            @LoginUser UserInfo userInfo,
            @RequestBody @Valid RecipeLikeRequest likeRequest
    ) {
        if (likeRequest.isLike()) {
            likeService.addLike(userInfo, likeRequest.recipeId());
            return;
        }
        likeService.deleteLike(userInfo, likeRequest.recipeId());
    }
}
