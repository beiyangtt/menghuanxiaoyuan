package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.DcForumComment;

/**
 * 论坛评论Mapper接口
 * 
 * @author ruoyi
 */
public interface DcForumCommentMapper 
{
    /**
     * 查询论坛评论
     * 
     * @param commentId 评论ID
     * @return 论坛评论
     */
    public DcForumComment selectDcForumCommentByCommentId(Long commentId);

    /**
     * 查询论坛评论列表
     * 
     * @param postId 帖子ID
     * @return 论坛评论集合
     */
    public List<DcForumComment> selectDcForumCommentListByPostId(Long postId);
    
    /**
     * 查询所有论坛评论列表（包括子评论）
     * 
     * @param postId 帖子ID
     * @return 论坛评论集合
     */
    public List<DcForumComment> selectAllDcForumCommentListByPostId(Long postId);

    /**
     * 新增论坛评论
     * 
     * @param dcForumComment 论坛评论
     * @return 结果
     */
    public int insertDcForumComment(DcForumComment dcForumComment);

    /**
     * 删除论坛评论
     * 
     * @param commentId 评论ID
     * @return 结果
     */
    public int deleteDcForumCommentByCommentId(Long commentId);

    /**
     * 增加点赞数
     * 
     * @param commentId 评论ID
     * @return 结果
     */
    public int incrementLikeCount(Long commentId);

    /**
     * 减少点赞数
     * 
     * @param commentId 评论ID
     * @return 结果
     */
    public int decrementLikeCount(Long commentId);
}

