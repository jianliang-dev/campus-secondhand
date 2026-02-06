package com.graduation.graduationdesign.service;

import com.graduation.graduationdesign.entity.Post;
import com.graduation.graduationdesign.vo.PostVO;

import java.util.List;

/**
 * 帖子服务接口
 */
public interface PostService {
    void publishPost(Post post, Long userId);
    List<PostVO> getPosts(String circleName, String sort, Integer page, Integer size);
    Integer countPosts(String circleName);
    List<PostVO> getHotPosts(Integer limit);
    PostVO getPostDetail(Long id);
    List<Post> getMyPosts(Long userId);
    void updatePost(Post post, Long userId);
    void deletePost(Long id, Long userId);
    void incrementViewCount(Long id);
}
