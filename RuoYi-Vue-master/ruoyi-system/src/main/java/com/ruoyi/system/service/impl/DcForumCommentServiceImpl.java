package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.DcForumCommentMapper;
import com.ruoyi.system.mapper.DcForumPostMapper;
import com.ruoyi.system.domain.DcForumComment;
import com.ruoyi.system.service.IDcForumCommentService;

/**
 * 论坛评论Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class DcForumCommentServiceImpl implements IDcForumCommentService 
{
    @Autowired
    private DcForumCommentMapper dcForumCommentMapper;

    @Autowired
    private DcForumPostMapper dcForumPostMapper;

    /**
     * 查询论坛评论
     * 
     * @param commentId 评论ID
     * @return 论坛评论
     */
    @Override
    public DcForumComment selectDcForumCommentByCommentId(Long commentId)
    {
        return dcForumCommentMapper.selectDcForumCommentByCommentId(commentId);
    }

    /**
     * 查询论坛评论列表
     * 
     * @param postId 帖子ID
     * @return 论坛评论集合
     */
    @Override
    public List<DcForumComment> selectDcForumCommentListByPostId(Long postId)
    {
        return dcForumCommentMapper.selectDcForumCommentListByPostId(postId);
    }
    
    /**
     * 查询所有论坛评论列表（包括子评论，组织成树形结构）
     * 
     * @param postId 帖子ID
     * @return 论坛评论集合（树形结构）
     */
    @Override
    public List<DcForumComment> selectDcForumCommentTreeByPostId(Long postId)
    {
        // 查询所有评论（包括子评论）
        List<DcForumComment> allComments = dcForumCommentMapper.selectAllDcForumCommentListByPostId(postId);
        
        // 组织成树形结构
        List<DcForumComment> rootComments = new java.util.ArrayList<>();
        java.util.Map<Long, DcForumComment> commentMap = new java.util.HashMap<>();
        
        // 第一遍遍历：创建所有评论的映射，并初始化children列表
        for (DcForumComment comment : allComments) {
            comment.setChildren(new java.util.ArrayList<>());
            commentMap.put(comment.getCommentId(), comment);
        }
        
        // 第二遍遍历：构建树形结构
        for (DcForumComment comment : allComments) {
            if (comment.getParentId() == null || comment.getParentId() == 0) {
                // 顶级评论
                rootComments.add(comment);
            } else {
                // 子评论，添加到父评论的children中
                DcForumComment parent = commentMap.get(comment.getParentId());
                if (parent != null) {
                    parent.getChildren().add(comment);
                }
            }
        }
        
        return rootComments;
    }

    /**
     * 新增论坛评论
     * 
     * @param dcForumComment 论坛评论
     * @return 结果
     */
    @Override
    @Transactional
    public int insertDcForumComment(DcForumComment dcForumComment)
    {
        // 设置默认值
        if (dcForumComment.getParentId() == null) {
            dcForumComment.setParentId(0L);
        }
        if (dcForumComment.getLikeCount() == null) {
            dcForumComment.setLikeCount(0);
        }
        if (dcForumComment.getStatus() == null) {
            dcForumComment.setStatus("0");
        }
        if (dcForumComment.getDelFlag() == null) {
            dcForumComment.setDelFlag("0");
        }
        int result = dcForumCommentMapper.insertDcForumComment(dcForumComment);
        // 增加帖子的评论数
        if (result > 0) {
            dcForumPostMapper.incrementCommentCount(dcForumComment.getPostId());
        }
        return result;
    }

    /**
     * 删除论坛评论
     * 
     * @param commentId 评论ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteDcForumCommentByCommentId(Long commentId)
    {
        // 查询评论信息，用于减少帖子评论数
        DcForumComment comment = dcForumCommentMapper.selectDcForumCommentByCommentId(commentId);
        int result = dcForumCommentMapper.deleteDcForumCommentByCommentId(commentId);
        // 减少帖子的评论数
        if (result > 0 && comment != null) {
            dcForumPostMapper.decrementCommentCount(comment.getPostId());
        }
        return result;
    }
}

