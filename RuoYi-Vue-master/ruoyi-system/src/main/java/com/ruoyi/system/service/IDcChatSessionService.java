package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.DcChatSession;

/**
 * 聊天会话Service接口
 * 
 * @author ruoyi
 */
public interface IDcChatSessionService 
{
    /**
     * 查询或创建聊天会话
     * 
     * @param user1Id 用户1ID
     * @param user2Id 用户2ID
     * @return 聊天会话
     */
    public DcChatSession getOrCreateSession(Long user1Id, Long user2Id);

    /**
     * 查询聊天会话列表
     * 
     * @param userId 用户ID
     * @return 聊天会话集合
     */
    public List<DcChatSession> selectDcChatSessionListByUserId(Long userId);

    /**
     * 清空未读数
     * 
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 结果
     */
    public int clearUnread(Long sessionId, Long userId);

    /**
     * 隐藏指定用户的会话
     *
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 结果
     */
    public int hideSessionForUser(Long sessionId, Long userId);

    /**
     * 取消隐藏指定用户的会话
     *
     * @param sessionId 会话ID
     * @param userId 用户ID
     */
    public void restoreSessionForUser(Long sessionId, Long userId);
}

