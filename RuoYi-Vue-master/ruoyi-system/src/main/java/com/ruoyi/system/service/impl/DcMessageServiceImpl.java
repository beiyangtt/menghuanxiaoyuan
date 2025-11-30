package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.DcMessageMapper;
import com.ruoyi.system.domain.DcMessage;
import com.ruoyi.system.service.IDcMessageService;

/**
 * 消息Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class DcMessageServiceImpl implements IDcMessageService 
{
    @Autowired
    private DcMessageMapper dcMessageMapper;

    /**
     * 查询消息列表
     */
    @Override
    public List<DcMessage> selectDcMessageList(Long receiverId, String messageType)
    {
        return dcMessageMapper.selectDcMessageList(receiverId, messageType);
    }

    /**
     * 新增消息
     */
    @Override
    public int insertDcMessage(DcMessage dcMessage)
    {
        if (dcMessage.getIsRead() == null) {
            dcMessage.setIsRead("0");
        }
        if (dcMessage.getDelFlag() == null) {
            dcMessage.setDelFlag("0");
        }
        return dcMessageMapper.insertDcMessage(dcMessage);
    }

    /**
     * 标记消息为已读
     */
    @Override
    public int markAsRead(Long messageId)
    {
        return dcMessageMapper.markAsRead(messageId);
    }

    /**
     * 查询未读消息数
     */
    @Override
    public int countUnread(Long receiverId, String messageType)
    {
        return dcMessageMapper.countUnread(receiverId, messageType);
    }

    @Override
    public int deleteMessage(Long messageId, Long receiverId)
    {
        return dcMessageMapper.deleteMessage(messageId, receiverId);
    }
}

