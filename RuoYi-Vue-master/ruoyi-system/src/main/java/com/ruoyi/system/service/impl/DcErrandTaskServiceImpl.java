package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.DcErrandTaskMapper;
import com.ruoyi.system.domain.DcErrandTask;
import com.ruoyi.system.service.IDcErrandTaskService;

/**
 * 跑腿任务Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class DcErrandTaskServiceImpl implements IDcErrandTaskService 
{
    @Autowired
    private DcErrandTaskMapper dcErrandTaskMapper;

    /**
     * 查询跑腿任务
     * 
     * @param taskId 跑腿任务主键
     * @return 跑腿任务
     */
    @Override
    public DcErrandTask selectDcErrandTaskByTaskId(Long taskId)
    {
        return dcErrandTaskMapper.selectDcErrandTaskByTaskId(taskId);
    }

    /**
     * 查询跑腿任务列表
     * 
     * @param dcErrandTask 跑腿任务
     * @return 跑腿任务
     */
    @Override
    public List<DcErrandTask> selectDcErrandTaskList(DcErrandTask dcErrandTask)
    {
        return dcErrandTaskMapper.selectDcErrandTaskList(dcErrandTask);
    }

    /**
     * 新增跑腿任务
     * 
     * @param dcErrandTask 跑腿任务
     * @return 结果
     */
    @Override
    public int insertDcErrandTask(DcErrandTask dcErrandTask)
    {
        // 清理前端可能误传的字段
        // 如果 categoryId 是字符串类型（前端误传），清空它
        if (dcErrandTask.getCategoryId() != null) {
            // 如果 categoryId 不是有效的 Long 类型，清空它
            // 这个检查在反序列化时已经失败，所以这里主要是清理
            dcErrandTask.setCategoryId(null);
        }
        
        // 设置默认值
        if (dcErrandTask.getTaskStatus() == null) {
            dcErrandTask.setTaskStatus("0"); // 默认待接单
        }
        if (dcErrandTask.getViewCount() == null) {
            dcErrandTask.setViewCount(0);
        }
        if (dcErrandTask.getDelFlag() == null) {
            dcErrandTask.setDelFlag("0");
        }
        // 如果前端传了taskType，使用taskType；否则使用categoryName作为taskType
        if (dcErrandTask.getTaskType() == null || dcErrandTask.getTaskType().isEmpty()) {
            if (dcErrandTask.getCategoryName() != null && !dcErrandTask.getCategoryName().isEmpty()) {
                dcErrandTask.setTaskType(dcErrandTask.getCategoryName());
            } else {
                // 如果都没有，设置默认值"其他"（数据库字段是NOT NULL）
                dcErrandTask.setTaskType("其他");
            }
        }
        // 兼容前端字段：reward -> taskReward
        if (dcErrandTask.getTaskReward() == null && dcErrandTask.getReward() != null) {
            dcErrandTask.setTaskReward(dcErrandTask.getReward());
        }
        // 兼容前端字段：deadline -> timeRequirement
        if (dcErrandTask.getTimeRequirement() == null || dcErrandTask.getTimeRequirement().isEmpty()) {
            if (dcErrandTask.getDeadline() != null && !dcErrandTask.getDeadline().isEmpty()) {
                dcErrandTask.setTimeRequirement(dcErrandTask.getDeadline());
            }
        }
        return dcErrandTaskMapper.insertDcErrandTask(dcErrandTask);
    }

    /**
     * 修改跑腿任务
     * 
     * @param dcErrandTask 跑腿任务
     * @return 结果
     */
    @Override
    public int updateDcErrandTask(DcErrandTask dcErrandTask)
    {
        // 兼容前端字段：reward -> taskReward
        if (dcErrandTask.getTaskReward() == null && dcErrandTask.getReward() != null) {
            dcErrandTask.setTaskReward(dcErrandTask.getReward());
        }
        // 兼容前端字段：deadline -> timeRequirement
        if (dcErrandTask.getTimeRequirement() == null || dcErrandTask.getTimeRequirement().isEmpty()) {
            if (dcErrandTask.getDeadline() != null && !dcErrandTask.getDeadline().isEmpty()) {
                dcErrandTask.setTimeRequirement(dcErrandTask.getDeadline());
            }
        }
        // 如果前端传了taskType，使用taskType；否则使用categoryName作为taskType
        if (dcErrandTask.getTaskType() == null || dcErrandTask.getTaskType().isEmpty()) {
            if (dcErrandTask.getCategoryName() != null && !dcErrandTask.getCategoryName().isEmpty()) {
                dcErrandTask.setTaskType(dcErrandTask.getCategoryName());
            } else {
                // 如果都没有，设置默认值"其他"（数据库字段是NOT NULL）
                dcErrandTask.setTaskType("其他");
            }
        }
        return dcErrandTaskMapper.updateDcErrandTask(dcErrandTask);
    }

    /**
     * 批量删除跑腿任务
     * 
     * @param taskIds 需要删除的跑腿任务主键
     * @return 结果
     */
    @Override
    public int deleteDcErrandTaskByTaskIds(Long[] taskIds)
    {
        return dcErrandTaskMapper.deleteDcErrandTaskByTaskIds(taskIds);
    }

    /**
     * 删除跑腿任务信息
     * 
     * @param taskId 跑腿任务主键
     * @return 结果
     */
    @Override
    public int deleteDcErrandTaskByTaskId(Long taskId)
    {
        return dcErrandTaskMapper.deleteDcErrandTaskByTaskId(taskId);
    }

    /**
     * 增加浏览次数
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    @Override
    public int incrementViewCount(Long taskId)
    {
        return dcErrandTaskMapper.incrementViewCount(taskId);
    }

    /**
     * 接受任务
     * 
     * @param taskId 任务ID
     * @param accepterId 接单人ID
     * @return 结果
     */
    @Override
    public int acceptTask(Long taskId, Long accepterId)
    {
        return dcErrandTaskMapper.acceptTask(taskId, accepterId);
    }

    /**
     * 完成任务
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    @Override
    public int completeTask(Long taskId)
    {
        return dcErrandTaskMapper.completeTask(taskId);
    }

    /**
     * 查询热门跑腿任务（按浏览次数排序，状态为待接单）
     * 
     * @param limit 限制数量
     * @return 跑腿任务集合
     */
    @Override
    public List<DcErrandTask> selectHotErrandTaskList(int limit)
    {
        return dcErrandTaskMapper.selectHotErrandTaskList(limit);
    }
}

