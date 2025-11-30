package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.DcMessage;

/**
 * 消息Service接口
 * 
 * @author ruoyi
 */
public interface IDcMessageService 
{
    /**
     * 查询消息列表
     * 
     * @param receiverId 接收者ID
     * @param messageType 消息类型（可选）
     * @return 消息集合
     */
    public List<DcMessage> selectDcMessageList(Long receiverId, String messageType);

    /**
     * 新增消息
     * 
     * @param dcMessage 消息
     * @return 结果
     */
    public int insertDcMessage(DcMessage dcMessage);

    /**
     * 标记消息为已读
     * 
     * @param messageId 消息ID
     * @return 结果
     */
    public int markAsRead(Long messageId);

    /**
     * 查询未读消息数
     * 
     * @param receiverId 接收者ID
     * @param messageType 消息类型（可选）
     * @return 未读消息数
     */
    public int countUnread(Long receiverId, String messageType);

    /**
     * 删除消息（仅当前用户可见）
     *
     * @param messageId 消息ID
     * @param receiverId 当前用户ID
     * @return 结果
     */
    public int deleteMessage(Long messageId, Long receiverId);
}

