<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="帖子标题" prop="postTitle">
        <el-input
          v-model="queryParams.postTitle"
          placeholder="请输入帖子标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
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

    <el-table v-loading="loading" :data="postList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="帖子ID" align="center" prop="postId" width="80" />
      <el-table-column label="帖子标题" align="center" prop="postTitle" :show-overflow-tooltip="true" min-width="250" />
      <el-table-column label="发布人" align="center" prop="nickName" width="120" />
      <el-table-column label="点赞数" align="center" prop="likeCount" width="100">
        <template slot-scope="scope">
          <span style="color: #f56c6c;">❤ {{ scope.row.likeCount || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="评论数" align="center" prop="commentCount" width="100">
        <template slot-scope="scope">
          <span style="color: #409eff;">💬 {{ scope.row.commentCount || 0 }}</span>
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

    <!-- 帖子详情对话框 -->
    <el-dialog title="帖子详情" :visible.sync="detailOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border v-if="postDetail">
        <el-descriptions-item label="帖子ID">{{ postDetail.postId }}</el-descriptions-item>
        <el-descriptions-item label="帖子标题">{{ postDetail.postTitle }}</el-descriptions-item>
        <el-descriptions-item label="发布人">{{ postDetail.nickName }}</el-descriptions-item>
        <el-descriptions-item label="点赞数">
          <span style="color: #f56c6c;">❤ {{ postDetail.likeCount || 0 }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="评论数">
          <span style="color: #409eff;">💬 {{ postDetail.commentCount || 0 }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="浏览次数">{{ postDetail.viewCount }}</el-descriptions-item>
        <el-descriptions-item label="帖子内容" :span="2">
          <div style="white-space: pre-wrap; max-height: 300px; overflow-y: auto;">{{ postDetail.postContent }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="发布时间" :span="2">{{ parseTime(postDetail.createTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { listPost, delPost, getPost } from "@/api/dreamcampus/forum"

export default {
  name: "ForumPost",
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
      // 帖子表格数据
      postList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      detailOpen: false,
      // 帖子详情
      postDetail: null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        postTitle: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询帖子列表 */
    getList() {
      this.loading = true
      listPost(this.queryParams).then(response => {
        this.postList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.postId)
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
      getPost(row.postId).then(response => {
        this.postDetail = response.data
        this.detailOpen = true
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const postIds = row.postId || this.ids
      this.$modal.confirm('是否确认删除帖子编号为"' + postIds + '"的数据项？').then(function() {
        return delPost(postIds)
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

