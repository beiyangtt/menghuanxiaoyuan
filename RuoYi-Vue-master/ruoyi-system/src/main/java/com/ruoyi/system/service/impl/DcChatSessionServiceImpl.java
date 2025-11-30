package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.DcChatSessionMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.domain.DcChatSession;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.IDcChatSessionService;

/**
 * 聊天会话Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class DcChatSessionServiceImpl implements IDcChatSessionService 
{
    @Autowired
    private DcChatSessionMapper dcChatSessionMapper;
    
    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 查询或创建聊天会话
     */
    @Override
    @Transactional
    public DcChatSession getOrCreateSession(Long user1Id, Long user2Id)
    {
        Long currentUserId = user1Id;
        Long otherUserId = user2Id;
        
        // 确保user1Id < user2Id，保持一致性
        if (user1Id > user2Id) {
            Long temp = user1Id;
            user1Id = user2Id;
            user2Id = temp;
        }
        
        // 查询是否已存在会话
        DcChatSession session = dcChatSessionMapper.selectDcChatSessionByUsers(user1Id, user2Id);
        if (session == null) {
            // 创建新会话
            session = new DcChatSession();
            session.setUser1Id(user1Id);
            session.setUser2Id(user2Id);
            session.setUser1Unread(0);
            session.setUser2Unread(0);
            session.setDelFlag("0");
            dcChatSessionMapper.insertDcChatSession(session);
        }
        
        // 取消隐藏状态，确保双方都能再次看到会话
        dcChatSessionMapper.restoreSessionForUser(session.getSessionId(), currentUserId);
        dcChatSessionMapper.restoreSessionForUser(session.getSessionId(), otherUserId);
        
        // 补充对方用户信息（确保返回最新头像）
        Long actualOtherUserId = (session.getUser1Id().equals(currentUserId)) ? session.getUser2Id() : session.getUser1Id();
        SysUser otherUser = sysUserMapper.selectUserById(actualOtherUserId);
        if (otherUser != null) {
            session.setOtherUserId(actualOtherUserId);
            session.setOtherNickName(otherUser.getNickName());
            session.setOtherAvatar(otherUser.getAvatar());
        }
        
        return session;
    }

    /**
     * 查询聊天会话列表
     */
    @Override
    public List<DcChatSession> selectDcChatSessionListByUserId(Long userId)
    {
        return dcChatSessionMapper.selectDcChatSessionListByUserId(userId);
    }

    /**
     * 清空未读数
     */
    @Override
    public int clearUnread(Long sessionId, Long userId)
    {
        return dcChatSessionMapper.clearUnread(sessionId, userId);
    }

    @Override
    public int hideSessionForUser(Long sessionId, Long userId)
    {
        return dcChatSessionMapper.hideSessionForUser(sessionId, userId);
    }

    @Override
    public void restoreSessionForUser(Long sessionId, Long userId)
    {
        dcChatSessionMapper.restoreSessionForUser(sessionId, userId);
    }
}

