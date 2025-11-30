package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.DcForumPost;

/**
 * 论坛帖子Mapper接口
 * 
 * @author ruoyi
 */
public interface DcForumPostMapper 
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
     * 删除论坛帖子
     * 
     * @param postId 论坛帖子主键
     * @return 结果
     */
    public int deleteDcForumPostByPostId(Long postId);

    /**
     * 批量删除论坛帖子
     * 
     * @param postIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDcForumPostByPostIds(Long[] postIds);

    /**
     * 增加浏览次数
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    public int incrementViewCount(Long postId);

    /**
     * 增加点赞数
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    public int incrementLikeCount(Long postId);

    /**
     * 减少点赞数
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    public int decrementLikeCount(Long postId);

    /**
     * 增加评论数
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    public int incrementCommentCount(Long postId);

    /**
     * 减少评论数
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    public int decrementCommentCount(Long postId);
}

