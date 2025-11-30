package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.DcForumPost;

/**
 * 论坛帖子Service接口
 * 
 * @author ruoyi
 */
public interface IDcForumPostService 
{
    /**
     * 查询论坛帖子
     * 
     * @param postId 论坛帖子主键
     * @return 论坛帖子
     */
    public DcForumPost selectDcForumPostByPostId(Long postId);

    /**
     * 查询论坛帖子列表
     * 
     * @param dcForumPost 论坛帖子
     * @return 论坛帖子集合
     */
    public List<DcForumPost> selectDcForumPostList(DcForumPost dcForumPost);

    /**
     * 新增论坛帖子
     * 
     * @param dcForumPost 论坛帖子
     * @return 结果
     */
    public int insertDcForumPost(DcForumPost dcForumPost);

    /**
     * 修改论坛帖子
     * 
     * @param dcForumPost 论坛帖子
     * @return 结果
     */
    public int updateDcForumPost(DcForumPost dcForumPost);

    /**
     * 批量删除论坛帖子
     * 
     * @param postIds 需要删除的论坛帖子主键集合
     * @return 结果
     */
    public int deleteDcForumPostByPostIds(Long[] postIds);

    /**
     * 删除论坛帖子信息
     * 
     * @param postId 论坛帖子主键
     * @return 结果
     */
    public int deleteDcForumPostByPostId(Long postId);

    /**
     * 增加浏览次数
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    public int incrementViewCount(Long postId);

    /**
     * 点赞/取消点赞
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果（true表示点赞，false表示取消点赞）
     */
    public boolean toggleLike(Long postId, Long userId);
}

