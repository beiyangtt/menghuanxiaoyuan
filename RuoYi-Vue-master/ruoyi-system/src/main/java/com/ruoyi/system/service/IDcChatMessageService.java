package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.DcChatMessage;

/**
 * 聊天消息Service接口
 * 
 * @author ruoyi
 */
public interface IDcChatMessageService 
{
    /**
     * 查询聊天消息列表
     * 
     * @param sessionId 会话ID
     * @return 聊天消息集合
     */
    public List<DcChatMessage> selectDcChatMessageListBySessionId(Long sessionId);

    /**
     * 发送消息
     * 
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param messageContent 消息内容
     * @return 结果
     */
    public int sendMessage(Long senderId, Long receiverId, String messageContent);

    /**
     * 标记消息为已读
     * 
     * @param sessionId 会话ID
     * @param receiverId 接收者ID
     * @return 结果
     */
    public int markAsRead(Long sessionId, Long receiverId);
}

