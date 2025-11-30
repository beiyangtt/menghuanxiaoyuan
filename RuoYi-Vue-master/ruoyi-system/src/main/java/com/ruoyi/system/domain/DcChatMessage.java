package com.ruoyi.system.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 聊天消息对象 dc_chat_message
 * 
 * @author ruoyi
 */
public class DcChatMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 消息ID */
    private Long messageId;

    /** 会话ID */
    @Excel(name = "会话ID")
    private Long sessionId;

    /** 发送者ID */
    @Excel(name = "发送者ID")
    private Long senderId;

    /** 接收者ID */
    @Excel(name = "接收者ID")
    private Long receiverId;

    /** 消息内容 */
    private String messageContent;

    /** 消息类型（text文本 image图片） */
    @Excel(name = "消息类型", readConverterExp = "text=文本,image=图片")
    private String messageType;

    /** 是否已读（0未读 1已读） */
    @Excel(name = "是否已读", readConverterExp = "0=未读,1=已读")
    private String isRead;

    /** 阅读时间 */
    private Date readTime;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 发送者昵称（关联查询） */
    private String senderNickName;
    private String senderAvatar;

    public void setMessageId(Long messageId)
    {
        this.messageId = messageId;
    }

    public Long getMessageId()
    {
        return messageId;
    }

    public void setSessionId(Long sessionId)
    {
        this.sessionId = sessionId;
    }

    public Long getSessionId()
    {
        return sessionId;
    }

    public void setSenderId(Long senderId)
    {
        this.senderId = senderId;
    }

    public Long getSenderId()
    {
        return senderId;
    }

    public void setReceiverId(Long receiverId)
    {
        this.receiverId = receiverId;
    }

    public Long getReceiverId()
    {
        return receiverId;
    }

    public void setMessageContent(String messageContent)
    {
        this.messageContent = messageContent;
    }

    public String getMessageContent()
    {
        return messageContent;
    }

    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
    }

    public String getMessageType()
    {
        return messageType;
    }

    public void setIsRead(String isRead)
    {
        this.isRead = isRead;
    }

    public String getIsRead()
    {
        return isRead;
    }

    public void setReadTime(Date readTime)
    {
        this.readTime = readTime;
    }

    public Date getReadTime()
    {
        return readTime;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public String getSenderNickName()
    {
        return senderNickName;
    }

    public void setSenderNickName(String senderNickName)
    {
        this.senderNickName = senderNickName;
    }

    public String getSenderAvatar()
    {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar)
    {
        this.senderAvatar = senderAvatar;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("messageId", getMessageId())
            .append("sessionId", getSessionId())
            .append("senderId", getSenderId())
            .append("receiverId", getReceiverId())
            .append("messageContent", getMessageContent())
            .append("messageType", getMessageType())
            .append("isRead", getIsRead())
            .append("readTime", getReadTime())
            .append("delFlag", getDelFlag())
            .append("createTime", getCreateTime())
            .toString();
    }
}

