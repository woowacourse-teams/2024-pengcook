package net.pengcook.comment.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.authentication.resolver.LoginUser;
import net.pengcook.comment.dto.CommentOfUserResponse;
import net.pengcook.comment.dto.CommentOfRecipeResponse;
import net.pengcook.comment.dto.CreateCommentRequest;
import net.pengcook.comment.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{recipeId}")
    public List<CommentOfRecipeResponse> readComments(@PathVariable long recipeId, @LoginUser UserInfo userInfo) {
        return commentService.readComments(recipeId, userInfo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createComment(@RequestBody @Valid CreateCommentRequest request, @LoginUser UserInfo userInfo) {
        commentService.createComment(request, userInfo);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long commentId, @LoginUser UserInfo userInfo) {
        commentService.deleteComment(commentId, userInfo);
    }

    @GetMapping("/mine")
    public List<CommentOfUserResponse> readCommentsOfMe(@LoginUser UserInfo userInfo) {
        return commentService.readCommentsOfUser(userInfo);
    }
}
