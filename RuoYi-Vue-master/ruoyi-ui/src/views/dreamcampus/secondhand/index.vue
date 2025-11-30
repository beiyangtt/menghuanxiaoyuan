<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商品标题" prop="goodsTitle">
        <el-input
          v-model="queryParams.goodsTitle"
          placeholder="请输入商品标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品状态" prop="goodsStatus">
        <el-select v-model="queryParams.goodsStatus" placeholder="商品状态" clearable>
          <el-option label="待售" value="0" />
          <el-option label="已售" value="1" />
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

    <el-table v-loading="loading" :data="goodsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="商品ID" align="center" prop="goodsId" width="80" />
      <el-table-column label="商品标题" align="center" prop="goodsTitle" :show-overflow-tooltip="true" min-width="200" />
      <el-table-column label="价格" align="center" prop="goodsPrice" width="100">
        <template slot-scope="scope">
          <span style="color: #f56c6c; font-weight: bold;">¥{{ scope.row.goodsPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发布人" align="center" prop="nickName" width="120" />
      <el-table-column label="状态" align="center" prop="goodsStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.goodsStatus === '0'" type="success">待售</el-tag>
          <el-tag v-else type="info">已售</el-tag>
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

    <!-- 商品详情对话框 -->
    <el-dialog title="商品详情" :visible.sync="detailOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border v-if="goodsDetail">
        <el-descriptions-item label="商品ID">{{ goodsDetail.goodsId }}</el-descriptions-item>
        <el-descriptions-item label="商品标题">{{ goodsDetail.goodsTitle }}</el-descriptions-item>
        <el-descriptions-item label="价格">¥{{ goodsDetail.goodsPrice }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="goodsDetail.goodsStatus === '0'" type="success">待售</el-tag>
          <el-tag v-else type="info">已售</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布人">{{ goodsDetail.nickName }}</el-descriptions-item>
        <el-descriptions-item label="浏览次数">{{ goodsDetail.viewCount }}</el-descriptions-item>
        <el-descriptions-item label="商品描述" :span="2">{{ goodsDetail.goodsDesc }}</el-descriptions-item>
        <el-descriptions-item label="发布时间" :span="2">{{ parseTime(goodsDetail.createTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { listGoods, delGoods, getGoods } from "@/api/dreamcampus/secondhand"

export default {
  name: "SecondhandGoods",
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
      // 商品表格数据
      goodsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      detailOpen: false,
      // 商品详情
      goodsDetail: null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        goodsTitle: null,
        goodsStatus: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询商品列表 */
    getList() {
      this.loading = true
      listGoods(this.queryParams).then(response => {
        this.goodsList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.goodsId)
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
      getGoods(row.goodsId).then(response => {
        this.goodsDetail = response.data
        this.detailOpen = true
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const goodsIds = row.goodsId || this.ids
      this.$modal.confirm('是否确认删除商品编号为"' + goodsIds + '"的数据项？').then(function() {
        return delGoods(goodsIds)
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


