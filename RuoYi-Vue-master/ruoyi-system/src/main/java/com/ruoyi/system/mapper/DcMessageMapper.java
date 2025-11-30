package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.DcMessage;

/**
 * 消息Mapper接口
 * 
 * @author ruoyi
 */
public interface DcMessageMapper 
{
    /**
     * 查询消息列表
     * 
     * @param receiverId 接收者ID
     * @param messageType 消息类型（可选）
     * @return 消息集合
     */
    public List<DcMessage> selectDcMessageList(@Param("receiverId") Long receiverId, @Param("messageType") String messageType);

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
    public int countUnread(@Param("receiverId") Long receiverId, @Param("messageType") String messageType);

    /**
     * 删除消息（标记为已删除，仅限接收者）
     *
     * @param messageId 消息ID
     * @param receiverId 当前用户ID
     * @return 结果
     */
    public int deleteMessage(@Param("messageId") Long messageId, @Param("receiverId") Long receiverId);
}

