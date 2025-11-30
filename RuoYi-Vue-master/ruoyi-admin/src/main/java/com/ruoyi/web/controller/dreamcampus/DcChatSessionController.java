package com.ruoyi.web.controller.dreamcampus;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.DcChatSession;
import com.ruoyi.system.service.IDcChatSessionService;

/**
 * 聊天会话Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/dreamcampus/chat/session")
public class DcChatSessionController extends BaseController
{
    @Autowired
    private IDcChatSessionService dcChatSessionService;

    /**
     * 查询聊天会话列表
     */
    @GetMapping("/list")
    public AjaxResult list()
    {
        Long userId = SecurityUtils.getUserId();
        List<DcChatSession> list = dcChatSessionService.selectDcChatSessionListByUserId(userId);
        return success(list);
    }

    /**
     * 获取或创建会话
     */
    @GetMapping("/get/{otherUserId}")
    public AjaxResult getOrCreateSession(@PathVariable("otherUserId") Long otherUserId)
    {
        Long currentUserId = SecurityUtils.getUserId();
        DcChatSession session = dcChatSessionService.getOrCreateSession(currentUserId, otherUserId);
        return success(session);
    }

    /**
     * 清空未读数
     */
    @PutMapping("/clearUnread/{sessionId}")
    public AjaxResult clearUnread(@PathVariable("sessionId") Long sessionId)
    {
        Long userId = SecurityUtils.getUserId();
        return toAjax(dcChatSessionService.clearUnread(sessionId, userId));
    }

    /**
     * 隐藏当前用户的会话
     */
    @DeleteMapping("/{sessionId}")
    public AjaxResult deleteSession(@PathVariable("sessionId") Long sessionId)
    {
        Long userId = SecurityUtils.getUserId();
        return toAjax(dcChatSessionService.hideSessionForUser(sessionId, userId));
    }
}

