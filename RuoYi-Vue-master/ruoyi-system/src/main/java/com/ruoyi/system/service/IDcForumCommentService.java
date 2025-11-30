package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.DcForumComment;

/**
 * 论坛评论Service接口
 * 
 * @author ruoyi
 */
public interface IDcForumCommentService 
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
     * 查询所有论坛评论列表（包括子评论，组织成树形结构）
     * 
     * @param postId 帖子ID
     * @return 论坛评论集合（树形结构）
     */
    public List<DcForumComment> selectDcForumCommentTreeByPostId(Long postId);

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
}

