package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.DcForumLike;

/**
 * 论坛点赞Mapper接口
 * 
 * @author ruoyi
 */
public interface DcForumLikeMapper 
{
    /**
     * 查询点赞记录
     * 
     * @param userId 用户ID
     * @param targetType 目标类型（1帖子 2评论）
     * @param targetId 目标ID
     * @return 点赞记录
     */
    public DcForumLike selectDcForumLike(@Param("userId") Long userId, 
                                         @Param("targetType") String targetType, 
                                         @Param("targetId") Long targetId);

    /**
     * 新增点赞记录
     * 
     * @param dcForumLike 点赞记录
     * @return 结果
     */
    public int insertDcForumLike(DcForumLike dcForumLike);

    /**
     * 删除点赞记录
     * 
     * @param userId 用户ID
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 结果
     */
    public int deleteDcForumLike(@Param("userId") Long userId, 
                                 @Param("targetType") String targetType, 
                                 @Param("targetId") Long targetId);
}

