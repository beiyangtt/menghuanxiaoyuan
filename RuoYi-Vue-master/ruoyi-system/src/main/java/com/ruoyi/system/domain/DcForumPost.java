package com.ruoyi.system.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 论坛帖子对象 dc_forum_post
 * 
 * @author ruoyi
 */
public class DcForumPost extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 帖子ID */
    private Long postId;

    /** 发布用户ID */
    @Excel(name = "发布用户ID")
    private Long userId;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;

    /** 帖子标题 */
    @Excel(name = "帖子标题")
    private String postTitle;

    /** 帖子内容 */
    private String postContent;

    /** 浏览次数 */
    @Excel(name = "浏览次数")
    private Integer viewCount;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Integer likeCount;

    /** 评论数 */
    @Excel(name = "评论数")
    private Integer commentCount;

    /** 是否置顶（0否 1是） */
    @Excel(name = "是否置顶", readConverterExp = "0=否,1=是")
    private String isTop;

    /** 是否热门（0否 1是） */
    @Excel(name = "是否热门", readConverterExp = "0=否,1=是")
    private String isHot;

    /** 状态（0正常 1关闭） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=关闭")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 用户昵称（关联查询） */
    private String nickName;

    /** 用户头像（关联查询） */
    private String avatar;

    /** 分类名称（关联查询） */
    private String categoryName;

    /** 图片URLs（逗号分隔） */
    private String images;
    
    /**
     * 获取图片URLs，确保返回的路径包含 /profile 前缀
     * 如果数据库存储的是相对路径（如 upload/2025/11/26/abc.png），
     * 需要添加 /profile 前缀
     */
    public String getImages() {
        if (images == null || images.trim().isEmpty()) {
            return images;
        }
        // 如果已经是 /profile/ 开头，直接返回
        if (images.startsWith("/profile/")) {
            return images;
        }
        // 如果包含逗号分隔的多个路径，需要分别处理
        if (images.contains(",")) {
            String[] paths = images.split(",");
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < paths.length; i++) {
                String path = paths[i].trim();
                if (path.isEmpty()) {
                    continue;
                }
                if (!path.startsWith("/profile/")) {
                    // 如果路径不是以 /profile/ 开头，添加前缀
                    if (path.startsWith("/")) {
                        path = "/profile" + path;
                    } else {
                        path = "/profile/" + path;
                    }
                }
                if (result.length() > 0) {
                    result.append(",");
                }
                result.append(path);
            }
            return result.toString();
        } else {
            // 单个路径
            String path = images.trim();
            if (!path.startsWith("/profile/")) {
                if (path.startsWith("/")) {
                    path = "/profile" + path;
                } else {
                    path = "/profile/" + path;
                }
            }
            return path;
        }
    }

    /** 是否已点赞（用于前端显示） */
    private Boolean isLiked;

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

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setPostTitle(String postTitle)
    {
        this.postTitle = postTitle;
    }

    public String getPostTitle()
    {
        return postTitle;
    }

    public void setPostContent(String postContent)
    {
        this.postContent = postContent;
    }

    public String getPostContent()
    {
        return postContent;
    }

    public void setViewCount(Integer viewCount)
    {
        this.viewCount = viewCount;
    }

    public Integer getViewCount()
    {
        return viewCount;
    }

    public void setLikeCount(Integer likeCount)
    {
        this.likeCount = likeCount;
    }

    public Integer getLikeCount()
    {
        return likeCount;
    }

    public void setCommentCount(Integer commentCount)
    {
        this.commentCount = commentCount;
    }

    public Integer getCommentCount()
    {
        return commentCount;
    }

    public void setIsTop(String isTop)
    {
        this.isTop = isTop;
    }

    public String getIsTop()
    {
        return isTop;
    }

    public void setIsHot(String isHot)
    {
        this.isHot = isHot;
    }

    public String getIsHot()
    {
        return isHot;
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

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public void setImages(String images)
    {
        this.images = images;
    }

    public Boolean getIsLiked()
    {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked)
    {
        this.isLiked = isLiked;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("postId", getPostId())
            .append("userId", getUserId())
            .append("categoryId", getCategoryId())
            .append("postTitle", getPostTitle())
            .append("postContent", getPostContent())
            .append("viewCount", getViewCount())
            .append("likeCount", getLikeCount())
            .append("commentCount", getCommentCount())
            .append("isTop", getIsTop())
            .append("isHot", getIsHot())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

