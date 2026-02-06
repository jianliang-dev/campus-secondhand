package com.graduation.graduationdesign.controller;

import com.graduation.graduationdesign.service.impl.PostLikeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 帖子点赞接口
 */
@RestController
@RequestMapping("/postLike")
public class PostLikeController {

    @Autowired
    private PostLikeServiceImpl likeService;

    @PostMapping("/toggle")
    public String toggle(@RequestParam Long postId,
                         @RequestHeader("Authorization") String token) {
        try {
            likeService.toggle(postId, token);
            return "操作成功";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    @GetMapping("/check")
    public boolean check(@RequestParam Long postId,
                         @RequestHeader(value = "Authorization", required = false) String token) {
        return likeService.isLiked(postId, token);
    }
}
