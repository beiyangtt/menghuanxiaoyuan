package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.DcErrandTask;

/**
 * 跑腿任务Mapper接口
 * 
 * @author ruoyi
 */
public interface DcErrandTaskMapper 
{
    /**
     * 查询跑腿任务
     * 
     * @param taskId 跑腿任务主键
     * @return 跑腿任务
     */
    public DcErrandTask selectDcErrandTaskByTaskId(Long taskId);

    /**
     * 查询跑腿任务列表
     * 
     * @param dcErrandTask 跑腿任务
     * @return 跑腿任务集合
     */
    public List<DcErrandTask> selectDcErrandTaskList(DcErrandTask dcErrandTask);

    /**
     * 新增跑腿任务
     * 
     * @param dcErrandTask 跑腿任务
     * @return 结果
     */
    public int insertDcErrandTask(DcErrandTask dcErrandTask);

    /**
     * 修改跑腿任务
     * 
     * @param dcErrandTask 跑腿任务
     * @return 结果
     */
    public int updateDcErrandTask(DcErrandTask dcErrandTask);

    /**
     * 删除跑腿任务
     * 
     * @param taskId 跑腿任务主键
     * @return 结果
     */
    public int deleteDcErrandTaskByTaskId(Long taskId);

    /**
     * 批量删除跑腿任务
     * 
     * @param taskIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDcErrandTaskByTaskIds(Long[] taskIds);

    /**
     * 增加浏览次数
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    public int incrementViewCount(Long taskId);

    /**
     * 接受任务
     * 
     * @param taskId 任务ID
     * @param accepterId 接单人ID
     * @return 结果
     */
    public int acceptTask(@Param("taskId") Long taskId, @Param("accepterId") Long accepterId);

    /**
     * 完成任务
     * 
     * @param taskId 任务ID
     * @return 结果
     */
    public int completeTask(Long taskId);

    /**
     * 查询热门跑腿任务（按浏览次数排序，状态为待接单）
     * 
     * @param limit 限制数量
     * @return 跑腿任务集合
     */
    public List<DcErrandTask> selectHotErrandTaskList(int limit);
}

