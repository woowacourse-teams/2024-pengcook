package net.pengcook.comment.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.authentication.resolver.LoginUser;
import net.pengcook.comment.dto.CommentResponse;
import net.pengcook.comment.service.CommentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{recipeId}")
    public List<CommentResponse> readComments(@PathVariable long recipeId, @LoginUser UserInfo userInfo) {
        return commentService.readComments(recipeId, userInfo);
    }
}
