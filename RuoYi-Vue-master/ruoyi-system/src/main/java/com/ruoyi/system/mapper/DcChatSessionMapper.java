package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.DcChatSession;

/**
 * 聊天会话Mapper接口
 * 
 * @author ruoyi
 */
public interface DcChatSessionMapper 
{
    /**
     * 查询聊天会话
     * 
     * @param sessionId 聊天会话主键
     * @return 聊天会话
     */
    public DcChatSession selectDcChatSessionBySessionId(Long sessionId);

    /**
     * 查询聊天会话（根据两个用户ID）
     * 
     * @param user1Id 用户1ID
     * @param user2Id 用户2ID
     * @return 聊天会话
     */
    public DcChatSession selectDcChatSessionByUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    /**
     * 查询聊天会话列表
     * 
     * @param userId 用户ID
     * @return 聊天会话集合
     */
    public List<DcChatSession> selectDcChatSessionListByUserId(Long userId);

    /**
     * 新增聊天会话
     * 
     * @param dcChatSession 聊天会话
     * @return 结果
     */
    public int insertDcChatSession(DcChatSession dcChatSession);

    /**
     * 修改聊天会话
     * 
     * @param dcChatSession 聊天会话
     * @return 结果
     */
    public int updateDcChatSession(DcChatSession dcChatSession);

    /**
     * 更新最后一条消息
     * 
     * @param sessionId 会话ID
     * @param lastMessage 最后一条消息
     * @return 结果
     */
    public int updateLastMessage(@Param("sessionId") Long sessionId, @Param("lastMessage") String lastMessage);

    /**
     * 增加未读数
     * 
     * @param sessionId 会话ID
     * @param userId 用户ID（1或2）
     * @return 结果
     */
    public int incrementUnread(@Param("sessionId") Long sessionId, @Param("userId") Long userId);

    /**
     * 清空未读数
     * 
     * @param sessionId 会话ID
     * @param userId 用户ID（1或2）
     * @return 结果
     */
    public int clearUnread(@Param("sessionId") Long sessionId, @Param("userId") Long userId);

    /**
     * 将指定用户的会话标记为隐藏
     *
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 结果
     */
    public int hideSessionForUser(@Param("sessionId") Long sessionId, @Param("userId") Long userId);

    /**
     * 取消隐藏指定用户的会话
     *
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 结果
     */
    public int restoreSessionForUser(@Param("sessionId") Long sessionId, @Param("userId") Long userId);
}

