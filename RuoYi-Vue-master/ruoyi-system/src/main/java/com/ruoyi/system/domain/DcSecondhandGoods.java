package com.ruoyi.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 二手商品对象 dc_secondhand_goods
 * 
 * @author ruoyi
 */
public class DcSecondhandGoods extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private Long goodsId;

    /** 发布用户ID */
    @Excel(name = "发布用户ID")
    private Long userId;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;

    /** 商品标题 */
    @Excel(name = "商品标题")
    private String goodsTitle;

    /** 商品描述 */
    private String goodsDesc;

    /** 商品价格 */
    @Excel(name = "商品价格")
    private BigDecimal goodsPrice;

    /** 原价 */
    @Excel(name = "原价")
    private BigDecimal originalPrice;

    /** 商品状态（0待售 1已售 2已下架） */
    @Excel(name = "商品状态", readConverterExp = "0=待售,1=已售,2=已下架")
    private String goodsStatus;

    /** 联系方式 */
    private String contactWay;

    /** 交易地点 */
    private String location;

    /** 浏览次数 */
    @Excel(name = "浏览次数")
    private Integer viewCount;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Integer likeCount;

    /** 收藏数 */
    @Excel(name = "收藏数")
    private Integer collectCount;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 分类名称（关联查询） */
    private String categoryName;

    /** 用户昵称（关联查询） */
    private String nickName;

    /** 用户头像（关联查询） */
    private String avatar;

    /** 商品图片（逗号分隔的URL） */
    private String images;

    public void setGoodsId(Long goodsId) 
    {
        this.goodsId = goodsId;
    }

    public Long getGoodsId() 
    {
        return goodsId;
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
    public void setGoodsTitle(String goodsTitle) 
    {
        this.goodsTitle = goodsTitle;
    }

    public String getGoodsTitle() 
    {
        return goodsTitle;
    }
    public void setGoodsDesc(String goodsDesc) 
    {
        this.goodsDesc = goodsDesc;
    }

    public String getGoodsDesc() 
    {
        return goodsDesc;
    }
    public void setGoodsPrice(BigDecimal goodsPrice) 
    {
        this.goodsPrice = goodsPrice;
    }

    public BigDecimal getGoodsPrice() 
    {
        return goodsPrice;
    }
    public void setOriginalPrice(BigDecimal originalPrice) 
    {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getOriginalPrice() 
    {
        return originalPrice;
    }
    public void setGoodsStatus(String goodsStatus) 
    {
        this.goodsStatus = goodsStatus;
    }

    public String getGoodsStatus() 
    {
        return goodsStatus;
    }
    public void setContactWay(String contactWay) 
    {
        this.contactWay = contactWay;
    }

    public String getContactWay() 
    {
        return contactWay;
    }
    public void setLocation(String location) 
    {
        this.location = location;
    }

    public String getLocation() 
    {
        return location;
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
    public void setCollectCount(Integer collectCount) 
    {
        this.collectCount = collectCount;
    }

    public Integer getCollectCount() 
    {
        return collectCount;
    }
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public String getCategoryName() 
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName) 
    {
        this.categoryName = categoryName;
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

    public void setImages(String images) 
    {
        this.images = images;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("goodsId", getGoodsId())
            .append("userId", getUserId())
            .append("categoryId", getCategoryId())
            .append("goodsTitle", getGoodsTitle())
            .append("goodsDesc", getGoodsDesc())
            .append("goodsPrice", getGoodsPrice())
            .append("originalPrice", getOriginalPrice())
            .append("goodsStatus", getGoodsStatus())
            .append("contactWay", getContactWay())
            .append("location", getLocation())
            .append("viewCount", getViewCount())
            .append("likeCount", getLikeCount())
            .append("collectCount", getCollectCount())
            .append("delFlag", getDelFlag())
            .append("images", getImages())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

