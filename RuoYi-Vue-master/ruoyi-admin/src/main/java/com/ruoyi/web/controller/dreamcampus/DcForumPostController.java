package com.ruoyi.web.controller.dreamcampus;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import com.ruoyi.system.domain.DcForumPost;
import com.ruoyi.system.service.IDcForumPostService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 论坛帖子Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/dreamcampus/forum")
public class DcForumPostController extends BaseController
{
    @Autowired
    private IDcForumPostService dcForumPostService;

    /**
     * 查询论坛帖子列表
     */
    @GetMapping("/list")
    public TableDataInfo list(DcForumPost dcForumPost)
    {
        startPage();
        List<DcForumPost> list = dcForumPostService.selectDcForumPostList(dcForumPost);
        // 检查当前用户是否已点赞
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId != null) {
            for (DcForumPost post : list) {
                // 这里需要查询点赞状态，暂时设为false
                post.setIsLiked(false);
            }
        }
        return getDataTable(list);
    }

    /**
     * 获取论坛帖子详细信息
     */
    @GetMapping(value = "/{postId}")
    public AjaxResult getInfo(@PathVariable("postId") Long postId)
    {
        // 增加浏览次数
        dcForumPostService.incrementViewCount(postId);
        DcForumPost post = dcForumPostService.selectDcForumPostByPostId(postId);
        if (post != null) {
            // 检查当前用户是否已点赞
            Long currentUserId = SecurityUtils.getUserId();
            if (currentUserId != null) {
                // 这里需要查询点赞状态，暂时设为false
                post.setIsLiked(false);
            }
        }
        return success(post);
    }

    /**
     * 新增论坛帖子
     */
    @Log(title = "论坛帖子", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DcForumPost dcForumPost)
    {
        // 设置发布用户ID为当前登录用户
        dcForumPost.setUserId(SecurityUtils.getUserId());
        dcForumPost.setCreateBy(SecurityUtils.getUsername());
        return toAjax(dcForumPostService.insertDcForumPost(dcForumPost));
    }

    /**
     * 修改论坛帖子
     */
    @Log(title = "论坛帖子", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DcForumPost dcForumPost)
    {
        // 验证是否为帖子发布者
        DcForumPost post = dcForumPostService.selectDcForumPostByPostId(dcForumPost.getPostId());
        if (post == null || !post.getUserId().equals(SecurityUtils.getUserId())) {
            return error("只能修改自己发布的帖子");
        }
        dcForumPost.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(dcForumPostService.updateDcForumPost(dcForumPost));
    }

    /**
     * 删除论坛帖子
     */
    @Log(title = "论坛帖子", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public AjaxResult remove(@PathVariable Long[] postIds)
    {
        Long currentUserId = SecurityUtils.getUserId();
        boolean isAdmin = SecurityUtils.isAdmin(currentUserId);
        
        // 管理员可以删除任何帖子，普通用户只能删除自己发布的帖子
        if (!isAdmin) {
            for (Long postId : postIds) {
                DcForumPost post = dcForumPostService.selectDcForumPostByPostId(postId);
                if (post == null || !post.getUserId().equals(currentUserId)) {
                    return error("只能删除自己发布的帖子");
                }
            }
        }
        return toAjax(dcForumPostService.deleteDcForumPostByPostIds(postIds));
    }

    /**
     * 点赞/取消点赞
     */
    @Log(title = "论坛帖子", businessType = BusinessType.UPDATE)
    @PutMapping("/like/{postId}")
    public AjaxResult toggleLike(@PathVariable Long postId)
    {
        Long userId = SecurityUtils.getUserId();
        boolean isLiked = dcForumPostService.toggleLike(postId, userId);
        return success(isLiked ? "点赞成功" : "取消点赞成功");
    }
}

