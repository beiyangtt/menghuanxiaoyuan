package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;

import com.ruoyi.system.domain.DcChatSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.DcChatMessageMapper;
import com.ruoyi.system.mapper.DcChatSessionMapper;
import com.ruoyi.system.domain.DcChatMessage;
import com.ruoyi.system.service.IDcChatMessageService;
import com.ruoyi.system.service.IDcChatSessionService;

/**
 * 聊天消息Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class DcChatMessageServiceImpl implements IDcChatMessageService 
{
    @Autowired
    private DcChatMessageMapper dcChatMessageMapper;

    @Autowired
    private IDcChatSessionService dcChatSessionService;

    @Autowired
    private DcChatSessionMapper dcChatSessionMapper;

    /**
     * 查询聊天消息列表
     */
    @Override
    public List<DcChatMessage> selectDcChatMessageListBySessionId(Long sessionId)
    {
        return dcChatMessageMapper.selectDcChatMessageListBySessionId(sessionId);
    }

    /**
     * 发送消息
     */
    @Override
    @Transactional
    public int sendMessage(Long senderId, Long receiverId, String messageContent)
    {
        // 获取或创建会话
        DcChatSession session = dcChatSessionService.getOrCreateSession(senderId, receiverId);
        
        // 创建消息
        DcChatMessage message = new DcChatMessage();
        message.setSessionId(session.getSessionId());
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setMessageContent(messageContent);
        message.setMessageType("text");
        message.setIsRead("0");
        message.setDelFlag("0");
        
        int result = dcChatMessageMapper.insertDcChatMessage(message);
        
        // 更新会话的最后一条消息和未读数
        if (result > 0) {
            dcChatSessionMapper.updateLastMessage(session.getSessionId(), messageContent);
            // 增加接收者的未读数
            dcChatSessionMapper.incrementUnread(session.getSessionId(), receiverId);
        }
        
        return result;
    }

    /**
     * 标记消息为已读
     */
    @Override
    @Transactional
    public int markAsRead(Long sessionId, Long receiverId)
    {
        // 标记消息为已读
        int result = dcChatMessageMapper.markAsRead(sessionId, receiverId);
        // 清空会话未读数
        if (result > 0) {
            dcChatSessionMapper.clearUnread(sessionId, receiverId);
        }
        return result;
    }
}

