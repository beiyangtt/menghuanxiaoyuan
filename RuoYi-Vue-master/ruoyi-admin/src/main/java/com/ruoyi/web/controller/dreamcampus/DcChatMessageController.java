package com.ruoyi.web.controller.dreamcampus;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.DcChatMessage;
import com.ruoyi.system.service.IDcChatMessageService;

/**
 * 聊天消息Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/dreamcampus/chat/message")
public class DcChatMessageController extends BaseController
{
    @Autowired
    private IDcChatMessageService dcChatMessageService;

    /**
     * 查询聊天消息列表
     */
    @GetMapping("/list/{sessionId}")
    public AjaxResult list(@PathVariable("sessionId") Long sessionId)
    {
        List<DcChatMessage> list = dcChatMessageService.selectDcChatMessageListBySessionId(sessionId);
        return success(list);
    }

    /**
     * 发送消息
     */
    @Log(title = "聊天消息", businessType = BusinessType.INSERT)
    @PostMapping("/send")
    public AjaxResult sendMessage(@RequestBody DcChatMessage dcChatMessage)
    {
        Long senderId = SecurityUtils.getUserId();
        Long receiverId = dcChatMessage.getReceiverId();
        String messageContent = dcChatMessage.getMessageContent();
        
        // 参数验证
        if (receiverId == null) {
            return error("接收者ID不能为空");
        }
        if (messageContent == null || messageContent.trim().isEmpty()) {
            return error("消息内容不能为空");
        }
        if (senderId.equals(receiverId)) {
            return error("不能给自己发送消息");
        }
        
        int result = dcChatMessageService.sendMessage(
            senderId, 
            receiverId, 
            messageContent.trim()
        );
        return toAjax(result);
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/read/{sessionId}")
    public AjaxResult markAsRead(@PathVariable("sessionId") Long sessionId)
    {
        Long receiverId = SecurityUtils.getUserId();
        return toAjax(dcChatMessageService.markAsRead(sessionId, receiverId));
    }
}

