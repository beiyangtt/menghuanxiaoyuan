package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 跑腿任务对象 dc_errand_task
 * 
 * @author ruoyi
 */
public class DcErrandTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private Long taskId;

    /** 发布用户ID */
    @Excel(name = "发布用户ID")
    private Long userId;

    /** 任务类型（代取/代买/代送/其他） */
    @Excel(name = "任务类型")
    private String taskType;

    /** 任务标题 */
    @Excel(name = "任务标题")
    private String taskTitle;

    /** 任务描述 */
    private String taskDesc;

    /** 起点位置 */
    @Excel(name = "起点位置")
    private String startLocation;

    /** 终点位置 */
    @Excel(name = "终点位置")
    private String endLocation;

    /** 任务报酬 */
    @Excel(name = "任务报酬")
    private BigDecimal taskReward;

    /** 时间要求 */
    private String timeRequirement;

    /** 任务状态（0待接单 1进行中 2已完成 3已取消） */
    @Excel(name = "任务状态", readConverterExp = "0=待接单,1=进行中,2=已完成,3=已取消")
    private String taskStatus;

    /** 接单人ID */
    private Long accepterId;

    /** 接单时间 - 使用服务器本地时间（Asia/Shanghai UTC+8） */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "Asia/Shanghai")
    private Date acceptTime;

    /** 完成时间 - 使用服务器本地时间（Asia/Shanghai UTC+8） */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "Asia/Shanghai")
    private Date completeTime;

    /** 浏览次数 */
    @Excel(name = "浏览次数")
    private Integer viewCount;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 用户昵称（关联查询） */
    private String nickName;

    /** 用户头像（关联查询） */
    private String avatar;

    /** 分类ID（用于前端映射，反序列化时忽略，避免前端误传字符串导致错误） */
    @JsonIgnore
    private Long categoryId;

    /** 分类名称（用于前端映射，反序列化时忽略） */
    @JsonIgnore
    private String categoryName;

    /** 截止时间（用于前端显示，反序列化时忽略） */
    @JsonIgnore
    private String deadline;

    /** 报酬（兼容字段，反序列化时忽略） */
    @JsonIgnore
    private BigDecimal reward;

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public Long getTaskId()
    {
        return taskId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setTaskType(String taskType)
    {
        this.taskType = taskType;
    }

    public String getTaskType()
    {
        return taskType;
    }

    public void setTaskTitle(String taskTitle)
    {
        this.taskTitle = taskTitle;
    }

    public String getTaskTitle()
    {
        return taskTitle;
    }

    public void setTaskDesc(String taskDesc)
    {
        this.taskDesc = taskDesc;
    }

    public String getTaskDesc()
    {
        return taskDesc;
    }

    public void setStartLocation(String startLocation)
    {
        this.startLocation = startLocation;
    }

    public String getStartLocation()
    {
        return startLocation;
    }

    public void setEndLocation(String endLocation)
    {
        this.endLocation = endLocation;
    }

    public String getEndLocation()
    {
        return endLocation;
    }

    public void setTaskReward(BigDecimal taskReward)
    {
        this.taskReward = taskReward;
    }

    public BigDecimal getTaskReward()
    {
        return taskReward;
    }

    public void setTimeRequirement(String timeRequirement)
    {
        this.timeRequirement = timeRequirement;
    }

    public String getTimeRequirement()
    {
        return timeRequirement;
    }

    public void setTaskStatus(String taskStatus)
    {
        this.taskStatus = taskStatus;
    }

    public String getTaskStatus()
    {
        return taskStatus;
    }

    public void setAccepterId(Long accepterId)
    {
        this.accepterId = accepterId;
    }

    public Long getAccepterId()
    {
        return accepterId;
    }

    public void setAcceptTime(Date acceptTime)
    {
        this.acceptTime = acceptTime;
    }

    public Date getAcceptTime()
    {
        return acceptTime;
    }

    public void setCompleteTime(Date completeTime)
    {
        this.completeTime = completeTime;
    }

    public Date getCompleteTime()
    {
        return completeTime;
    }

    public void setViewCount(Integer viewCount)
    {
        this.viewCount = viewCount;
    }

    public Integer getViewCount()
    {
        return viewCount;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public String getDeadline()
    {
        return deadline;
    }

    public void setDeadline(String deadline)
    {
        this.deadline = deadline;
    }

    public BigDecimal getReward()
    {
        return reward;
    }

    public void setReward(BigDecimal reward)
    {
        this.reward = reward;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("taskId", getTaskId())
            .append("userId", getUserId())
            .append("taskType", getTaskType())
            .append("taskTitle", getTaskTitle())
            .append("taskDesc", getTaskDesc())
            .append("startLocation", getStartLocation())
            .append("endLocation", getEndLocation())
            .append("taskReward", getTaskReward())
            .append("timeRequirement", getTimeRequirement())
            .append("taskStatus", getTaskStatus())
            .append("accepterId", getAccepterId())
            .append("acceptTime", getAcceptTime())
            .append("completeTime", getCompleteTime())
            .append("viewCount", getViewCount())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

