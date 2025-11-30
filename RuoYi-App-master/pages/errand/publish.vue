<template>
  <view class="container">
    <view class="form-section">
      <view class="form-item">
        <text class="label">任务标题</text>
        <input class="input" v-model="form.taskTitle" placeholder="请输入任务标题" />
      </view>

      <view class="form-item">
        <text class="label">任务类型</text>
        <picker 
          mode="selector" 
          :range="taskTypes" 
          range-key="label" 
          @change="onTypeChange">
          <view class="picker">
            {{ form.taskType || '请选择类型' }}
          </view>
        </picker>
      </view>

      <view class="form-item">
        <text class="label">起点位置</text>
        <input class="input" v-model="form.startLocation" placeholder="请输入起点位置" />
      </view>

      <view class="form-item">
        <text class="label">终点位置</text>
        <input class="input" v-model="form.endLocation" placeholder="请输入终点位置" />
      </view>

      <view class="form-item">
        <text class="label">时间要求</text>
        <input class="input" v-model="form.timeRequirement" placeholder="例如：2025-01-15 15:00" />
      </view>

      <view class="form-item">
        <text class="label">任务报酬</text>
        <input class="input" type="digit" v-model="form.taskReward" placeholder="请输入报酬金额" />
      </view>

      <view class="form-item">
        <text class="label">任务描述</text>
        <textarea class="textarea" v-model="form.taskDesc" placeholder="请详细描述任务要求" />
      </view>
    </view>

    <view class="submit-btn" @click="handleSubmit">{{ isEdit ? '更新任务' : '发布任务' }}</view>
  </view>
</template>

<script>
  import { addTask, updateTask, getTaskDetail } from '@/api/errand'
  
  export default {
    data() {
      return {
        isEdit: false,
        taskId: null,
        taskTypes: [
          { value: '代取', label: '代取' },
          { value: '代买', label: '代买' },
          { value: '代送', label: '代送' },
          { value: '其他', label: '其他' }
        ],
        form: {
          taskTitle: '',
          taskType: '',
          startLocation: '',
          endLocation: '',
          timeRequirement: '',
          taskReward: '',
          taskDesc: ''
        }
      }
    },
    onLoad(options) {
      if (options.id && options.mode === 'edit') {
        this.isEdit = true
        this.taskId = options.id
        this.loadTaskDetail()
        uni.setNavigationBarTitle({
          title: '编辑任务'
        })
      }
    },
    methods: {
      async loadTaskDetail() {
        try {
          uni.showLoading({ title: '加载中...' })
          const res = await getTaskDetail(this.taskId)
          if (res.code === 200) {
            const data = res.data
            this.form = {
              taskTitle: data.taskTitle || '',
              taskType: data.taskType || '',
              startLocation: data.startLocation || '',
              endLocation: data.endLocation || '',
              timeRequirement: data.timeRequirement || '',
              taskReward: data.taskReward ? String(data.taskReward) : '',
              taskDesc: data.taskDesc || ''
            }
          } else {
            uni.showToast({
              title: res.msg || '加载失败',
              icon: 'none'
            })
          }
        } catch (error) {
          console.error('加载任务详情失败:', error)
          uni.showToast({
            title: '加载失败，请重试',
            icon: 'none'
          })
        } finally {
          uni.hideLoading()
        }
      },
      onTypeChange(e) {
        const index = Number(e.detail.value)
        if (Number.isInteger(index) && index >= 0 && index < this.taskTypes.length) {
          const selected = this.taskTypes[index]
          this.form.taskType = selected.value
        }
      },
      async handleSubmit() {
        // 验证必填字段
        if (!this.form.taskTitle || !this.form.taskTitle.trim()) {
          uni.showToast({ title: '请输入任务标题', icon: 'none' })
          return
        }
        if (!this.form.taskType || !this.form.taskType.trim()) {
          uni.showToast({ title: '请选择任务类型', icon: 'none' })
          return
        }
        if (!this.form.startLocation || !this.form.startLocation.trim()) {
          uni.showToast({ title: '请输入起点位置', icon: 'none' })
          return
        }
        if (!this.form.endLocation || !this.form.endLocation.trim()) {
          uni.showToast({ title: '请输入终点位置', icon: 'none' })
          return
        }
        if (!this.form.taskReward || !this.form.taskReward.trim()) {
          uni.showToast({ title: '请输入报酬金额', icon: 'none' })
          return
        }
        
        const reward = parseFloat(this.form.taskReward)
        if (isNaN(reward) || reward <= 0) {
          uni.showToast({ title: '请输入有效的报酬金额', icon: 'none' })
          return
        }
        
        try {
          uni.showLoading({ title: this.isEdit ? '更新中...' : '发布中...' })
          
          // 构建提交数据，只包含后端需要的字段
          // 明确排除 categoryId 等不需要的字段
          const submitData = {
            taskTitle: this.form.taskTitle.trim(),
            taskType: this.form.taskType.trim(),
            startLocation: this.form.startLocation.trim(),
            endLocation: this.form.endLocation.trim(),
            timeRequirement: this.form.timeRequirement ? this.form.timeRequirement.trim() : '',
            taskReward: reward,
            taskDesc: this.form.taskDesc ? this.form.taskDesc.trim() : '',
            taskStatus: '0' // 待接单
          }
          
          // 确保不包含 categoryId、categoryName、deadline 等字段
          // 这些字段会导致后端反序列化错误
          delete submitData.categoryId
          delete submitData.categoryName
          delete submitData.deadline
          delete submitData.reward
          
          let res
          if (this.isEdit) {
            submitData.taskId = this.taskId
            res = await updateTask(submitData)
          } else {
            res = await addTask(submitData)
          }
          
          if (res.code === 200) {
            uni.showToast({ 
              title: this.isEdit ? '更新成功' : '发布成功', 
              icon: 'success' 
            })
            setTimeout(() => {
              uni.navigateBack()
            }, 1500)
          } else {
            uni.showToast({ 
              title: res.msg || (this.isEdit ? '更新失败' : '发布失败'), 
              icon: 'none',
              duration: 3000
            })
            console.error('提交失败:', res)
          }
        } catch (error) {
          console.error(this.isEdit ? '更新任务失败:' : '发布任务失败:', error)
          uni.showToast({ 
            title: (this.isEdit ? '更新失败' : '发布失败') + '，请重试', 
            icon: 'none',
            duration: 3000
          })
        } finally {
          uni.hideLoading()
        }
      }
    }
  }
</script>

<style scoped>
  .container {
    background-color: #f5f5f5;
    min-height: 100vh;
    padding-bottom: 40rpx;
  }

  .form-section {
    background-color: #fff;
    margin-top: 20rpx;
    padding: 30rpx;
  }

  .form-item {
    margin-bottom: 40rpx;
  }

  .label {
    font-size: 28rpx;
    color: #333;
    display: block;
    margin-bottom: 20rpx;
  }

  .input {
    width: 100%;
    height: 80rpx;
    background-color: #f5f5f5;
    border-radius: 10rpx;
    padding: 0 20rpx;
    font-size: 28rpx;
  }

  .picker {
    width: 100%;
    height: 80rpx;
    background-color: #f5f5f5;
    border-radius: 10rpx;
    padding: 0 20rpx;
    display: flex;
    align-items: center;
    font-size: 28rpx;
    color: #333;
  }

  .textarea {
    width: 100%;
    min-height: 200rpx;
    background-color: #f5f5f5;
    border-radius: 10rpx;
    padding: 20rpx;
    font-size: 28rpx;
  }

  .submit-btn {
    margin: 40rpx 30rpx;
    background-color: #67C23A;
    color: #fff;
    text-align: center;
    padding: 30rpx;
    border-radius: 50rpx;
    font-size: 32rpx;
  }
</style>
