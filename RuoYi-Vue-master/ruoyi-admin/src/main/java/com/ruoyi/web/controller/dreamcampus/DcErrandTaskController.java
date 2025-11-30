package com.ruoyi.web.controller.dreamcampus;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.DcErrandTask;
import com.ruoyi.system.service.IDcErrandTaskService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 跑腿任务Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/dreamcampus/errand")
public class DcErrandTaskController extends BaseController
{
    @Autowired
    private IDcErrandTaskService dcErrandTaskService;

    /**
     * 查询跑腿任务列表
     */
    @GetMapping("/list")
    public TableDataInfo list(DcErrandTask dcErrandTask)
    {
        startPage();
        List<DcErrandTask> list = dcErrandTaskService.selectDcErrandTaskList(dcErrandTask);
        return getDataTable(list);
    }

    /**
     * 获取跑腿任务详细信息
     */
    @GetMapping(value = "/{taskId}")
    public AjaxResult getInfo(@PathVariable("taskId") Long taskId)
    {
        // 增加浏览次数
        dcErrandTaskService.incrementViewCount(taskId);
        DcErrandTask task = dcErrandTaskService.selectDcErrandTaskByTaskId(taskId);
        if (task != null) {
            // 设置兼容字段
            task.setReward(task.getTaskReward());
            task.setDeadline(task.getTimeRequirement());
            // 设置分类信息（用于前端显示）
            task.setCategoryName(task.getTaskType());
        }
        return success(task);
    }

    /**
     * 新增跑腿任务
     */
    @Log(title = "跑腿任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DcErrandTask dcErrandTask)
    {
        // 设置发布用户ID为当前登录用户
        dcErrandTask.setUserId(SecurityUtils.getUserId());
        dcErrandTask.setCreateBy(SecurityUtils.getUsername());
        return toAjax(dcErrandTaskService.insertDcErrandTask(dcErrandTask));
    }

    /**
     * 修改跑腿任务
     */
    @Log(title = "跑腿任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DcErrandTask dcErrandTask)
    {
        // 验证是否为任务发布者
        DcErrandTask task = dcErrandTaskService.selectDcErrandTaskByTaskId(dcErrandTask.getTaskId());
        if (task == null || !task.getUserId().equals(SecurityUtils.getUserId())) {
            return error("只能修改自己发布的任务");
        }
        dcErrandTask.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(dcErrandTaskService.updateDcErrandTask(dcErrandTask));
    }

    /**
     * 删除跑腿任务
     */
    @Log(title = "跑腿任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable Long[] taskIds)
    {
        Long currentUserId = SecurityUtils.getUserId();
        boolean isAdmin = SecurityUtils.isAdmin(currentUserId);
        
        // 管理员可以删除任何任务，普通用户只能删除自己发布的任务
        if (!isAdmin) {
            for (Long taskId : taskIds) {
                DcErrandTask task = dcErrandTaskService.selectDcErrandTaskByTaskId(taskId);
                if (task == null || !task.getUserId().equals(currentUserId)) {
                    return error("只能删除自己发布的任务");
                }
            }
        }
        return toAjax(dcErrandTaskService.deleteDcErrandTaskByTaskIds(taskIds));
    }

    /**
     * 接受任务
     */
    @Log(title = "跑腿任务", businessType = BusinessType.UPDATE)
    @PutMapping("/accept/{taskId}")
    public AjaxResult acceptTask(@PathVariable Long taskId)
    {
        // 验证任务是否存在且为待接单状态
        DcErrandTask task = dcErrandTaskService.selectDcErrandTaskByTaskId(taskId);
        if (task == null) {
            return error("任务不存在");
        }
        if (!"0".equals(task.getTaskStatus())) {
            return error("该任务已被接单或已完成");
        }
        // 不能接自己的任务
        if (task.getUserId().equals(SecurityUtils.getUserId())) {
            return error("不能接受自己发布的任务");
        }
        int result = dcErrandTaskService.acceptTask(taskId, SecurityUtils.getUserId());
        if (result > 0) {
            return success("接单成功");
        } else {
            return error("接单失败，任务可能已被他人接单");
        }
    }

    /**
     * 完成任务
     */
    @Log(title = "跑腿任务", businessType = BusinessType.UPDATE)
    @PutMapping("/complete/{taskId}")
    public AjaxResult completeTask(@PathVariable Long taskId)
    {
        // 验证是否为任务发布者
        DcErrandTask task = dcErrandTaskService.selectDcErrandTaskByTaskId(taskId);
        if (task == null || !task.getUserId().equals(SecurityUtils.getUserId())) {
            return error("只能操作自己发布的任务");
        }
        if ("2".equals(task.getTaskStatus())) {
            return error("该任务已完成");
        }
        return toAjax(dcErrandTaskService.completeTask(taskId));
    }

    /**
     * 查询热门跑腿任务（按浏览次数排序，状态为待接单）
     */
    @GetMapping("/hot")
    public AjaxResult getHotTasks(@RequestParam(defaultValue = "10") int limit)
    {
        List<DcErrandTask> list = dcErrandTaskService.selectHotErrandTaskList(limit);
        // 设置兼容字段
        for (DcErrandTask task : list) {
            task.setReward(task.getTaskReward());
            task.setDeadline(task.getTimeRequirement());
            task.setCategoryName(task.getTaskType());
        }
        return success(list);
    }
}

