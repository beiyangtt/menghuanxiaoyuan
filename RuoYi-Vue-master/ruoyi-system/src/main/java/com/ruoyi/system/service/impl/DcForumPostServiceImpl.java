package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.DcForumPostMapper;
import com.ruoyi.system.mapper.DcForumLikeMapper;
import com.ruoyi.system.domain.DcForumPost;
import com.ruoyi.system.domain.DcForumLike;
import com.ruoyi.system.service.IDcForumPostService;

/**
 * 论坛帖子Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class DcForumPostServiceImpl implements IDcForumPostService 
{
    @Autowired
    private DcForumPostMapper dcForumPostMapper;

    @Autowired
    private DcForumLikeMapper dcForumLikeMapper;

    /**
     * 查询论坛帖子
     * 
     * @param postId 论坛帖子主键
     * @return 论坛帖子
     */
    @Override
    public DcForumPost selectDcForumPostByPostId(Long postId)
    {
        return dcForumPostMapper.selectDcForumPostByPostId(postId);
    }

    /**
     * 查询论坛帖子列表
     * 
     * @param dcForumPost 论坛帖子
     * @return 论坛帖子
     */
    @Override
    public List<DcForumPost> selectDcForumPostList(DcForumPost dcForumPost)
    {
        return dcForumPostMapper.selectDcForumPostList(dcForumPost);
    }

    /**
     * 新增论坛帖子
     * 
     * @param dcForumPost 论坛帖子
     * @return 结果
     */
    @Override
    public int insertDcForumPost(DcForumPost dcForumPost)
    {
        // 设置默认值
        if (dcForumPost.getViewCount() == null) {
            dcForumPost.setViewCount(0);
        }
        if (dcForumPost.getLikeCount() == null) {
            dcForumPost.setLikeCount(0);
        }
        if (dcForumPost.getCommentCount() == null) {
            dcForumPost.setCommentCount(0);
        }
        if (dcForumPost.getIsTop() == null) {
            dcForumPost.setIsTop("0");
        }
        if (dcForumPost.getIsHot() == null) {
            dcForumPost.setIsHot("0");
        }
        if (dcForumPost.getStatus() == null) {
            dcForumPost.setStatus("0");
        }
        if (dcForumPost.getDelFlag() == null) {
            dcForumPost.setDelFlag("0");
        }
        return dcForumPostMapper.insertDcForumPost(dcForumPost);
    }

    /**
     * 修改论坛帖子
     * 
     * @param dcForumPost 论坛帖子
     * @return 结果
     */
    @Override
    public int updateDcForumPost(DcForumPost dcForumPost)
    {
        return dcForumPostMapper.updateDcForumPost(dcForumPost);
    }

    /**
     * 批量删除论坛帖子
     * 
     * @param postIds 需要删除的论坛帖子主键
     * @return 结果
     */
    @Override
    public int deleteDcForumPostByPostIds(Long[] postIds)
    {
        return dcForumPostMapper.deleteDcForumPostByPostIds(postIds);
    }

    /**
     * 删除论坛帖子信息
     * 
     * @param postId 论坛帖子主键
     * @return 结果
     */
    @Override
    public int deleteDcForumPostByPostId(Long postId)
    {
        return dcForumPostMapper.deleteDcForumPostByPostId(postId);
    }

    /**
     * 增加浏览次数
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    @Override
    public int incrementViewCount(Long postId)
    {
        return dcForumPostMapper.incrementViewCount(postId);
    }

    /**
     * 点赞/取消点赞
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果（true表示点赞，false表示取消点赞）
     */
    @Override
    @Transactional
    public boolean toggleLike(Long postId, Long userId)
    {
        // 查询是否已点赞
        DcForumLike like = dcForumLikeMapper.selectDcForumLike(userId, "1", postId);
        if (like != null) {
            // 已点赞，取消点赞
            dcForumLikeMapper.deleteDcForumLike(userId, "1", postId);
            dcForumPostMapper.decrementLikeCount(postId);
            return false;
        } else {
            // 未点赞，添加点赞
            DcForumLike newLike = new DcForumLike();
            newLike.setUserId(userId);
            newLike.setTargetType("1");
            newLike.setTargetId(postId);
            dcForumLikeMapper.insertDcForumLike(newLike);
            dcForumPostMapper.incrementLikeCount(postId);
            return true;
        }
    }
}

