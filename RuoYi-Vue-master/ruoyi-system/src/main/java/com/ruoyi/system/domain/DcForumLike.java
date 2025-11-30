package com.ruoyi.system.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 论坛点赞对象 dc_forum_like
 * 
 * @author ruoyi
 */
public class DcForumLike
{
    private static final long serialVersionUID = 1L;

    /** 点赞ID */
    private Long likeId;

    /** 用户ID */
    private Long userId;

    /** 目标类型（1帖子 2评论） */
    private String targetType;

    /** 目标ID */
    private Long targetId;

    /** 创建时间 */
    private Date createTime;

    public void setLikeId(Long likeId)
    {
        this.likeId = likeId;
    }

    public Long getLikeId()
    {
        return likeId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setTargetType(String targetType)
    {
        this.targetType = targetType;
    }

    public String getTargetType()
    {
        return targetType;
    }

    public void setTargetId(Long targetId)
    {
        this.targetId = targetId;
    }

    public Long getTargetId()
    {
        return targetId;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("likeId", getLikeId())
            .append("userId", getUserId())
            .append("targetType", getTargetType())
            .append("targetId", getTargetId())
            .append("createTime", getCreateTime())
            .toString();
    }
}

