package com.ruoyi.system.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 聊天会话对象 dc_chat_session
 * 
 * @author ruoyi
 */
public class DcChatSession extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 会话ID */
    private Long sessionId;

    /** 用户1ID */
    @Excel(name = "用户1ID")
    private Long user1Id;

    /** 用户2ID */
    @Excel(name = "用户2ID")
    private Long user2Id;

    /** 最后一条消息 */
    private String lastMessage;

    /** 最后消息时间 */
    private Date lastMessageTime;

    /** 用户1未读数 */
    @Excel(name = "用户1未读数")
    private Integer user1Unread;

    /** 用户2未读数 */
    @Excel(name = "用户2未读数")
    private Integer user2Unread;

    /** 用户1是否隐藏会话（0可见 1隐藏） */
    private String user1Hidden;

    /** 用户2是否隐藏会话（0可见 1隐藏） */
    private String user2Hidden;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 对方用户信息（关联查询） */
    private String otherNickName;
    private String otherAvatar;
    private Long otherUserId;

    public void setSessionId(Long sessionId)
    {
        this.sessionId = sessionId;
    }

    public Long getSessionId()
    {
        return sessionId;
    }

    public void setUser1Id(Long user1Id)
    {
        this.user1Id = user1Id;
    }

    public Long getUser1Id()
    {
        return user1Id;
    }

    public void setUser2Id(Long user2Id)
    {
        this.user2Id = user2Id;
    }

    public Long getUser2Id()
    {
        return user2Id;
    }

    public void setLastMessage(String lastMessage)
    {
        this.lastMessage = lastMessage;
    }

    public String getLastMessage()
    {
        return lastMessage;
    }

    public void setLastMessageTime(Date lastMessageTime)
    {
        this.lastMessageTime = lastMessageTime;
    }

    public Date getLastMessageTime()
    {
        return lastMessageTime;
    }

    public void setUser1Unread(Integer user1Unread)
    {
        this.user1Unread = user1Unread;
    }

    public Integer getUser1Unread()
    {
        return user1Unread;
    }

    public void setUser2Unread(Integer user2Unread)
    {
        this.user2Unread = user2Unread;
    }

    public Integer getUser2Unread()
    {
        return user2Unread;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public String getUser1Hidden()
    {
        return user1Hidden;
    }

    public void setUser1Hidden(String user1Hidden)
    {
        this.user1Hidden = user1Hidden;
    }

    public String getUser2Hidden()
    {
        return user2Hidden;
    }

    public void setUser2Hidden(String user2Hidden)
    {
        this.user2Hidden = user2Hidden;
    }

    public String getOtherNickName()
    {
        return otherNickName;
    }

    public void setOtherNickName(String otherNickName)
    {
        this.otherNickName = otherNickName;
    }

    public String getOtherAvatar()
    {
        return otherAvatar;
    }

    public void setOtherAvatar(String otherAvatar)
    {
        this.otherAvatar = otherAvatar;
    }

    public Long getOtherUserId()
    {
        return otherUserId;
    }

    public void setOtherUserId(Long otherUserId)
    {
        this.otherUserId = otherUserId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("sessionId", getSessionId())
            .append("user1Id", getUser1Id())
            .append("user2Id", getUser2Id())
            .append("lastMessage", getLastMessage())
            .append("lastMessageTime", getLastMessageTime())
            .append("user1Unread", getUser1Unread())
            .append("user2Unread", getUser2Unread())
            .append("user1Hidden", getUser1Hidden())
            .append("user2Hidden", getUser2Hidden())
            .append("delFlag", getDelFlag())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

