package com.ruoyi.web.controller.dreamcampus;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.DcMessage;
import com.ruoyi.system.service.IDcMessageService;

/**
 * 消息Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/dreamcampus/message")
public class DcMessageController extends BaseController
{
    @Autowired
    private IDcMessageService dcMessageService;
    
    @Autowired
    private com.ruoyi.system.service.IDcChatSessionService dcChatSessionService;

    /**
     * 查询消息列表
     */
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(required = false) String messageType)
    {
        Long receiverId = SecurityUtils.getUserId();
        List<DcMessage> list = dcMessageService.selectDcMessageList(receiverId, messageType);
        return success(list);
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/read/{messageId}")
    public AjaxResult markAsRead(@PathVariable("messageId") Long messageId)
    {
        return toAjax(dcMessageService.markAsRead(messageId));
    }

    /**
     * 删除消息（仅当前用户）
     */
    @DeleteMapping("/{messageId}")
    public AjaxResult delete(@PathVariable("messageId") Long messageId)
    {
        Long receiverId = SecurityUtils.getUserId();
        return toAjax(dcMessageService.deleteMessage(messageId, receiverId));
    }

    /**
     * 查询未读消息数（包括系统消息和聊天消息）
     */
    @GetMapping("/unread/count")
    public AjaxResult countUnread(@RequestParam(required = false) String messageType)
    {
        Long receiverId = SecurityUtils.getUserId();
        int count = 0;
        
        // 如果指定了消息类型，只统计该类型的系统消息
        if (messageType != null && !messageType.isEmpty()) {
            count = dcMessageService.countUnread(receiverId, messageType);
        } else {
            // 统计所有系统消息的未读数
            int systemMessageCount = dcMessageService.countUnread(receiverId, null);
            
            // 统计所有聊天会话的未读数
            int chatMessageCount = 0;
            List<com.ruoyi.system.domain.DcChatSession> chatSessions = dcChatSessionService.selectDcChatSessionListByUserId(receiverId);
            for (com.ruoyi.system.domain.DcChatSession session : chatSessions) {
                if (session.getUser1Id().equals(receiverId)) {
                    chatMessageCount += (session.getUser1Unread() != null ? session.getUser1Unread() : 0);
                } else {
                    chatMessageCount += (session.getUser2Unread() != null ? session.getUser2Unread() : 0);
                }
            }
            
            count = systemMessageCount + chatMessageCount;
        }
        
        return success(count);
    }

    /**
     * 发送评论通知（前端调用，用于兼容）
     */
    @PostMapping("/comment/notify")
    public AjaxResult sendCommentNotification(@RequestBody DcMessage dcMessage)
    {
        dcMessage.setSenderId(SecurityUtils.getUserId());
        dcMessage.setMessageType("comment");
        return toAjax(dcMessageService.insertDcMessage(dcMessage));
    }
}

