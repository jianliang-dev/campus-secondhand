package com.graduation.graduationdesign.service.impl;

import com.graduation.graduationdesign.entity.Post;
import com.graduation.graduationdesign.mapper.PostMapper;
import com.graduation.graduationdesign.service.PostService;
import com.graduation.graduationdesign.vo.PostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 帖子服务实现类
 */
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Override
    public void publishPost(Post post, Long userId) {
        post.setUserId(userId);
        postMapper.insert(post);
    }

    @Override
    public List<PostVO> getPosts(String circleName, String sort, Integer page, Integer size) {
        int pageNum = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? 10 : size;
        int offset = (pageNum - 1) * pageSize;
        String sortKey = sort == null ? "latest" : sort;
        return postMapper.findPaged(circleName, sortKey, offset, pageSize);
    }

    @Override
    public Integer countPosts(String circleName) {
        return postMapper.countPosts(circleName);
    }

    @Override
    public List<PostVO> getHotPosts(Integer limit) {
        int size = limit == null || limit < 1 ? 10 : limit;
        return postMapper.findHotPosts(size);
    }

    @Override
    public PostVO getPostDetail(Long id) {
        return postMapper.findById(id);
    }

    @Override
    public List<Post> getMyPosts(Long userId) {
        return postMapper.findByUserId(userId);
    }

    @Override
    public void updatePost(Post post, Long userId) {
        // 查询帖子是否存在
        PostVO existPost = postMapper.findById(post.getId());
        if (existPost == null) {
            throw new RuntimeException("帖子不存在");
        }
        // 校验权限
        if (!existPost.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改该帖子");
        }
        postMapper.update(post);
    }

    @Override
    public void deletePost(Long id, Long userId) {
        // 查询帖子是否存在
        PostVO existPost = postMapper.findById(id);
        if (existPost == null) {
            throw new RuntimeException("帖子不存在");
        }
        // 校验权限
        if (!existPost.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除该帖子");
        }
        postMapper.delete(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        postMapper.incrementViewCount(id);
    }
}
