package com.ruoyi.system.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 论坛评论对象 dc_forum_comment
 * 
 * @author ruoyi
 */
public class DcForumComment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评论ID */
    private Long commentId;

    /** 帖子ID */
    @Excel(name = "帖子ID")
    private Long postId;

    /** 评论用户ID */
    @Excel(name = "评论用户ID")
    private Long userId;

    /** 父评论ID（0表示顶级评论） */
    private Long parentId;

    /** 评论内容 */
    private String commentContent;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Integer likeCount;

    /** 状态（0正常 1删除） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=删除")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 用户昵称（关联查询） */
    private String nickName;

    /** 用户头像（关联查询） */
    private String avatar;

    /** 是否已点赞（用于前端显示） */
    private Boolean isLiked;
    
    /** 子评论列表（用于树形结构） */
    private java.util.List<DcForumComment> children;

    public void setCommentId(Long commentId)
    {
        this.commentId = commentId;
    }

    public Long getCommentId()
    {
        return commentId;
    }

    public void setPostId(Long postId)
    {
        this.postId = postId;
    }

    public Long getPostId()
    {
        return postId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setCommentContent(String commentContent)
    {
        this.commentContent = commentContent;
    }

    public String getCommentContent()
    {
        return commentContent;
    }

    public void setLikeCount(Integer likeCount)
    {
        this.likeCount = likeCount;
    }

    public Integer getLikeCount()
    {
        return likeCount;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public Boolean getIsLiked()
    {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked)
    {
        this.isLiked = isLiked;
    }
    
    public java.util.List<DcForumComment> getChildren()
    {
        return children;
    }
    
    public void setChildren(java.util.List<DcForumComment> children)
    {
        this.children = children;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("commentId", getCommentId())
            .append("postId", getPostId())
            .append("userId", getUserId())
            .append("parentId", getParentId())
            .append("commentContent", getCommentContent())
            .append("likeCount", getLikeCount())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

