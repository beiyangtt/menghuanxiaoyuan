<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="任务标题" prop="taskTitle">
        <el-input
          v-model="queryParams.taskTitle"
          placeholder="请输入任务标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="任务状态" prop="taskStatus">
        <el-select v-model="queryParams.taskStatus" placeholder="任务状态" clearable>
          <el-option label="待接单" value="0" />
          <el-option label="进行中" value="1" />
          <el-option label="已完成" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="taskList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="任务ID" align="center" prop="taskId" width="80" />
      <el-table-column label="任务标题" align="center" prop="taskTitle" :show-overflow-tooltip="true" min-width="200" />
      <el-table-column label="任务类型" align="center" prop="taskType" width="120" />
      <el-table-column label="奖励" align="center" prop="taskReward" width="100">
        <template slot-scope="scope">
          <span style="color: #67c23a; font-weight: bold;">¥{{ scope.row.taskReward }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发布人" align="center" prop="nickName" width="120" />
      <el-table-column label="状态" align="center" prop="taskStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.taskStatus === '0'" type="warning">待接单</el-tag>
          <el-tag v-else-if="scope.row.taskStatus === '1'" type="primary">进行中</el-tag>
          <el-tag v-else type="success">已完成</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="浏览次数" align="center" prop="viewCount" width="100" />
      <el-table-column label="发布时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 任务详情对话框 -->
    <el-dialog title="任务详情" :visible.sync="detailOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border v-if="taskDetail">
        <el-descriptions-item label="任务ID">{{ taskDetail.taskId }}</el-descriptions-item>
        <el-descriptions-item label="任务标题">{{ taskDetail.taskTitle }}</el-descriptions-item>
        <el-descriptions-item label="任务类型">{{ taskDetail.taskType }}</el-descriptions-item>
        <el-descriptions-item label="奖励">¥{{ taskDetail.taskReward }}</el-descriptions-item>
        <el-descriptions-item label="发布人">{{ taskDetail.nickName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="taskDetail.taskStatus === '0'" type="warning">待接单</el-tag>
          <el-tag v-else-if="taskDetail.taskStatus === '1'" type="primary">进行中</el-tag>
          <el-tag v-else type="success">已完成</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="起点">{{ taskDetail.startLocation }}</el-descriptions-item>
        <el-descriptions-item label="终点">{{ taskDetail.endLocation }}</el-descriptions-item>
        <el-descriptions-item label="任务描述" :span="2">{{ taskDetail.taskDesc }}</el-descriptions-item>
        <el-descriptions-item label="浏览次数">{{ taskDetail.viewCount }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ parseTime(taskDetail.createTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { listTask, delTask, getTask } from "@/api/dreamcampus/errand"

export default {
  name: "ErrandTask",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 任务表格数据
      taskList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      detailOpen: false,
      // 任务详情
      taskDetail: null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        taskTitle: null,
        taskStatus: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询任务列表 */
    getList() {
      this.loading = true
      listTask(this.queryParams).then(response => {
        this.taskList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.taskId)
      this.single = selection.length != 1
      this.multiple = !selection.length
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    /** 查看按钮操作 */
    handleView(row) {
      getTask(row.taskId).then(response => {
        this.taskDetail = response.data
        this.detailOpen = true
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const taskIds = row.taskId || this.ids
      this.$modal.confirm('是否确认删除任务编号为"' + taskIds + '"的数据项？').then(function() {
        return delTask(taskIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
</style>
