package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.DcChatMessage;

/**
 * 聊天消息Mapper接口
 * 
 * @author ruoyi
 */
public interface DcChatMessageMapper 
{
    /**
     * 查询聊天消息列表
     * 
     * @param sessionId 会话ID
     * @return 聊天消息集合
     */
    public List<DcChatMessage> selectDcChatMessageListBySessionId(Long sessionId);

    /**
     * 新增聊天消息
     * 
     * @param dcChatMessage 聊天消息
     * @return 结果
     */
    public int insertDcChatMessage(DcChatMessage dcChatMessage);

    /**
     * 标记消息为已读
     * 
     * @param sessionId 会话ID
     * @param receiverId 接收者ID
     * @return 结果
     */
    public int markAsRead(@Param("sessionId") Long sessionId, @Param("receiverId") Long receiverId);
}

