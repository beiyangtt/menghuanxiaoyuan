package com.ruoyi.web.controller.dreamcampus;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.DcForumComment;
import com.ruoyi.system.domain.DcForumPost;
import com.ruoyi.system.service.IDcForumCommentService;
import com.ruoyi.system.service.IDcForumPostService;
import com.ruoyi.system.service.IDcMessageService;
import com.ruoyi.system.domain.DcMessage;

/**
 * 论坛评论Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/dreamcampus/forum/comment")
public class DcForumCommentController extends BaseController
{
    @Autowired
    private IDcForumCommentService dcForumCommentService;

    @Autowired
    private IDcForumPostService dcForumPostService;

    @Autowired
    private IDcMessageService dcMessageService;

    /**
     * 查询论坛评论列表（树形结构，包括子评论）
     */
    @GetMapping("/list/{postId}")
    public AjaxResult list(@PathVariable("postId") Long postId)
    {
        List<DcForumComment> list = dcForumCommentService.selectDcForumCommentTreeByPostId(postId);
        // 检查当前用户是否已点赞
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId != null) {
            setLikeStatus(list, currentUserId);
        }
        return success(list);
    }
    
    /**
     * 递归设置点赞状态
     */
    private void setLikeStatus(List<DcForumComment> comments, Long currentUserId) {
        for (DcForumComment comment : comments) {
            // 这里需要查询点赞状态，暂时设为false
            comment.setIsLiked(false);
            // 递归处理子评论
            if (comment.getChildren() != null && !comment.getChildren().isEmpty()) {
                setLikeStatus(comment.getChildren(), currentUserId);
            }
        }
    }

    /**
     * 新增论坛评论
     */
    @Log(title = "论坛评论", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DcForumComment dcForumComment)
    {
        // 设置评论用户ID为当前登录用户
        Long currentUserId = SecurityUtils.getUserId();
        dcForumComment.setUserId(currentUserId);
        dcForumComment.setCreateBy(SecurityUtils.getUsername());
        int result = dcForumCommentService.insertDcForumComment(dcForumComment);
        
        // 发送消息通知
        if (result > 0) {
            try {
                DcForumPost post = dcForumPostService.selectDcForumPostByPostId(dcForumComment.getPostId());
                if (post != null) {
                    Long receiverId = null;
                    String messageContent = "";
                    
                    if (dcForumComment.getParentId() != null && dcForumComment.getParentId() > 0) {
                        // 回复评论，通知被回复的用户
                        DcForumComment parentComment = dcForumCommentService.selectDcForumCommentByCommentId(dcForumComment.getParentId());
                        if (parentComment != null && !parentComment.getUserId().equals(currentUserId)) {
                            receiverId = parentComment.getUserId();
                            messageContent = SecurityUtils.getUsername() + " 回复了你的评论：" + dcForumComment.getCommentContent();
                        }
                    } else {
                        // 评论帖子，通知帖子发布者
                        if (!post.getUserId().equals(currentUserId)) {
                            receiverId = post.getUserId();
                            messageContent = SecurityUtils.getUsername() + " 评论了你的帖子《" + post.getPostTitle() + "》：" + dcForumComment.getCommentContent();
                        }
                    }
                    
                    if (receiverId != null) {
                        DcMessage message = new DcMessage();
                        message.setSenderId(currentUserId);
                        message.setReceiverId(receiverId);
                        message.setMessageType("comment");
                        message.setMessageTitle("评论回复");
                        message.setMessageContent(messageContent);
                        message.setRelatedId(dcForumComment.getPostId());
                        message.setRelatedType("post");
                        dcMessageService.insertDcMessage(message);
                    }
                }
            } catch (Exception e) {
                // 消息通知失败不影响评论功能
                logger.error("发送评论通知失败", e);
            }
        }
        
        return toAjax(result);
    }

    /**
     * 删除论坛评论
     */
    @Log(title = "论坛评论", businessType = BusinessType.DELETE)
    @DeleteMapping("/{commentId}")
    public AjaxResult remove(@PathVariable Long commentId)
    {
        // 验证是否为评论发布者
        DcForumComment comment = dcForumCommentService.selectDcForumCommentByCommentId(commentId);
        if (comment == null || !comment.getUserId().equals(SecurityUtils.getUserId())) {
            return error("只能删除自己发布的评论");
        }
        return toAjax(dcForumCommentService.deleteDcForumCommentByCommentId(commentId));
    }
}

