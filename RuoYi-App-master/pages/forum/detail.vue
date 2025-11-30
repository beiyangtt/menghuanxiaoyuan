<template>
  <view class="container">
    <!-- 右上角菜单按钮 -->
    <view class="menu-btn" v-if="isOwner" @click="showMenu">
      <uni-icons type="more-filled" size="24" color="#333"></uni-icons>
    </view>
    
    <!-- 帖子内容 -->
    <view class="post-card">
      <view class="post-header">
        <image :src="postInfo.avatar" mode="aspectFill" class="author-avatar"></image>
        <view class="author-info">
          <text class="author-name">{{ postInfo.nickName }}</text>
          <text class="post-time">{{ formatTime(postInfo.createTime) }}</text>
        </view>
        <view class="post-category" v-if="postInfo.categoryName">
          <text>{{ postInfo.categoryName }}</text>
        </view>
      </view>
      
      <text class="post-title">{{ postInfo.postTitle }}</text>
      
      <view class="post-content">
        <text class="content-text">{{ postInfo.postContent }}</text>
        <view class="post-images" v-if="postInfo.images && postInfo.images.length > 0">
          <image 
            v-for="(img, index) in postInfo.images" 
            :key="index"
            :src="img" 
            mode="aspectFill" 
            class="content-image"
            @click="previewImage(index)">
          </image>
        </view>
      </view>

      <view class="post-stats">
        <view class="stat-item">
          <uni-icons type="eye" size="18" color="#999"></uni-icons>
          <text>{{ postInfo.viewCount }}</text>
        </view>
        <view class="stat-item" @click="handleToggleLike">
          <uni-icons type="heart" :color="postInfo.isLiked ? '#f56c6c' : '#999'" size="18"></uni-icons>
          <text>{{ postInfo.likeCount }}</text>
        </view>
        <view class="stat-item">
          <uni-icons type="chat" size="18" color="#999"></uni-icons>
          <text>{{ postInfo.commentCount }}</text>
        </view>
      </view>
    </view>

    <!-- 评论区 -->
    <view class="comment-section">
      <view class="section-title">
        <text>评论 ({{ comments.length }})</text>
      </view>
      <view class="comment-list">
        <view class="comment-item" v-for="item in comments" :key="item.commentId">
          <image :src="item.avatar || '/static/images/profile.jpg'" mode="aspectFill" class="comment-avatar"></image>
          <view class="comment-content">
            <view class="comment-header">
              <text class="comment-author">{{ item.nickName || '用户' }}</text>
              <text class="comment-time">{{ formatTime(item.createTime) }}</text>
            </view>
            <text class="comment-text">{{ item.content }}</text>
            <view class="comment-actions">
              <view class="reply-btn" @click="showReplyInput(item)">
                <uni-icons type="chatbubble" size="16" color="#999"></uni-icons>
                <text>回复</text>
              </view>
            </view>
            <!-- 子评论（回复） -->
            <view class="reply-list" v-if="item.children && item.children.length > 0">
              <view class="reply-item" v-for="reply in item.children" :key="reply.commentId">
                <image :src="reply.avatar || '/static/images/profile.jpg'" mode="aspectFill" class="reply-avatar"></image>
                <view class="reply-content">
                  <view class="reply-header">
                    <text class="reply-author">{{ reply.nickName || '用户' }}</text>
                    <text class="reply-time">{{ formatTime(reply.createTime) }}</text>
                  </view>
                  <text class="reply-text">{{ reply.commentContent || reply.content }}</text>
                  <view class="comment-actions">
                    <view class="reply-btn" @click="showReplyInput(reply)">
                      <uni-icons type="chatbubble" size="16" color="#999"></uni-icons>
                      <text>回复</text>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 回复输入框 -->
      <view class="reply-input-wrapper" v-if="replyingComment">
        <view class="reply-input-header">
          <text>回复 {{ replyingComment.nickName }}</text>
          <text class="cancel-btn" @click="cancelReply">取消</text>
        </view>
        <view class="reply-input-bar">
          <input class="input" v-model="replyText" placeholder="输入回复..." />
          <view class="send-btn" @click="sendReply">
            <text>发送</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部输入栏 -->
    <view class="bottom-input">
      <input class="input" v-model="commentText" placeholder="写评论..." />
      <view class="send-btn" @click="sendComment">
        <text>发送</text>
      </view>
    </view>
  </view>
</template>

<script>
  import { getPostDetail, deletePost, toggleLike, addComment, getCommentList } from '@/api/forum'
  import store from '@/store'
  import { processAvatarUrl } from '@/utils/avatar'
  import { resolveFileUrl, formatRelativeTime } from '@/utils/common'
  
  export default {
    data() {
      return {
        postId: null,
        isOwner: false, // 是否是发布者
        currentUserId: null, // 当前登录用户ID
        commentText: '',
        replyingComment: null, // 正在回复的评论
        replyText: '', // 回复内容
        postInfo: {
          postTitle: '',
          postContent: '',
          nickName: '',
          avatar: '/static/images/profile.jpg',
          createTime: '',
          categoryName: '',
          viewCount: 0,
          likeCount: 0,
          commentCount: 0,
          isLiked: false,
          images: [],
          userId: null
        },
        comments: []
      }
    },
    onLoad(options) {
      if (options.id) {
        this.postId = options.id
        this.currentUserId = store.getters.id
        this.loadPostDetail()
      }
    },
    onShow() {
      // 每次显示时刷新当前用户ID（可能已更新）
      this.currentUserId = store.getters.id
      // 如果是当前用户的帖子，更新头像
      if (this.postInfo && this.postInfo.userId && this.currentUserId && 
          String(this.postInfo.userId) === String(this.currentUserId) && store.getters.avatar) {
        this.postInfo.avatar = store.getters.avatar
      }
    },
    methods: {
      // 加载帖子详情
      async loadPostDetail() {
        try {
          uni.showLoading({ title: '加载中...' })
          const res = await getPostDetail(this.postId)
          if (res.code === 200) {
            const data = res.data
            // 如果是当前用户的帖子，使用 store 中的头像（确保是最新的）
            let avatar = data.avatar
            const userId = data.userId || null
            if (userId && this.currentUserId && String(userId) === String(this.currentUserId) && store.getters.avatar) {
              avatar = store.getters.avatar
            } else {
              // 处理头像URL，确保正确拼接baseUrl
              avatar = processAvatarUrl(avatar)
            }
            // 处理图片数据
            const rawImages = data.images ? (typeof data.images === 'string' ? data.images.split(',') : data.images) : []
            
            const processedImages = rawImages
              .filter(url => url && url.trim())
              .map(img => {
                // 确保图片路径正确解析
                const trimmed = img.trim()
                
                // 如果已经是完整URL，直接使用
                if (trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
                  return trimmed
                }
                
                // 否则使用 resolveFileUrl 处理
                return resolveFileUrl(trimmed)
              })
              .filter(Boolean)
            
            this.postInfo = {
              postTitle: data.postTitle || '',
              postContent: data.postContent || '',
              nickName: data.nickName || '用户',
              avatar: avatar,
              createTime: data.createTime || '',
              categoryName: data.categoryName || '',
              viewCount: data.viewCount || 0,
              likeCount: data.likeCount || 0,
              commentCount: data.commentCount || 0,
              isLiked: data.isLiked || false,
              images: processedImages,
              userId: userId
            }
            // 判断是否是发布者
            this.isOwner = this.currentUserId && this.postInfo.userId && 
                          String(this.currentUserId) === String(this.postInfo.userId)
            // 加载评论列表
            this.loadComments()
          } else {
            uni.showToast({
              title: res.msg || '加载失败',
              icon: 'none'
            })
          }
        } catch (error) {
          console.error('加载帖子详情失败:', error)
          uni.showToast({
            title: '加载失败，请重试',
            icon: 'none'
          })
        } finally {
          uni.hideLoading()
        }
      },
      // 显示菜单
      showMenu() {
        uni.showActionSheet({
          itemList: ['重新编辑', '删除帖子'],
          success: (res) => {
            if (res.tapIndex === 0) {
              this.editPost()
            } else if (res.tapIndex === 1) {
              this.deletePostConfirm()
            }
          }
        })
      },
      // 编辑帖子
      editPost() {
        uni.navigateTo({
          url: `/pages/forum/publish?id=${this.postId}&mode=edit`
        })
      },
      // 删除帖子确认
      deletePostConfirm() {
        uni.showModal({
          title: '提示',
          content: '确定要删除该帖子吗？删除后无法恢复。',
          success: async (res) => {
            if (res.confirm) {
              try {
                uni.showLoading({ title: '删除中...' })
                const result = await deletePost(this.postId)
                if (result.code === 200) {
                  uni.showToast({
                    title: '删除成功',
                    icon: 'success'
                  })
                  setTimeout(() => {
                    uni.navigateBack()
                  }, 1500)
                } else {
                  uni.showToast({
                    title: result.msg || '删除失败',
                    icon: 'none'
                  })
                }
              } catch (error) {
                console.error('删除帖子失败:', error)
                uni.showToast({
                  title: '删除失败，请重试',
                  icon: 'none'
                })
              } finally {
                uni.hideLoading()
              }
            }
          }
        })
      },
      async handleToggleLike() {
        try {
          const res = await toggleLike(this.postId)
          if (res.code === 200) {
            this.postInfo.isLiked = !this.postInfo.isLiked
            this.postInfo.likeCount += this.postInfo.isLiked ? 1 : -1
          } else {
            uni.showToast({
              title: res.msg || '操作失败',
              icon: 'none'
            })
          }
        } catch (error) {
          console.error('点赞失败:', error)
        }
      },
      previewImage(index) {
        uni.previewImage({
          current: index,
          urls: this.postInfo.images
        })
      },
      async sendComment() {
        if (!this.commentText.trim()) {
          uni.showToast({ title: '请输入评论内容', icon: 'none' })
          return
        }
        try {
          const res = await addComment({
            postId: this.postId,
            commentContent: this.commentText,
            parentId: 0
          })
          if (res.code === 200) {
            this.commentText = ''
            this.postInfo.commentCount++
            uni.showToast({ title: '评论成功', icon: 'success' })
            // 重新加载评论列表
            this.loadComments()
            // 发送消息通知给帖子发布者
            if (this.postInfo.userId && this.postInfo.userId !== this.currentUserId) {
              this.sendCommentNotification(this.postInfo.userId, this.postInfo.postTitle)
            }
          } else {
            uni.showToast({ title: res.msg || '评论失败', icon: 'none' })
          }
        } catch (error) {
          console.error('评论失败:', error)
          uni.showToast({ title: '评论失败，请重试', icon: 'none' })
        }
      },
      // 显示回复输入框
      showReplyInput(comment) {
        this.replyingComment = comment
        this.replyText = ''
      },
      // 取消回复
      cancelReply() {
        this.replyingComment = null
        this.replyText = ''
      },
      // 发送回复
      async sendReply() {
        if (!this.replyText.trim()) {
          uni.showToast({ title: '请输入回复内容', icon: 'none' })
          return
        }
        try {
          const res = await addComment({
            postId: this.postId,
            commentContent: this.replyText,
            parentId: this.replyingComment.commentId
          })
          if (res.code === 200) {
            const repliedUserId = this.replyingComment.userId
            this.replyText = ''
            this.replyingComment = null
            this.postInfo.commentCount++
            uni.showToast({ title: '回复成功', icon: 'success' })
            // 重新加载评论列表
            this.loadComments()
            // 发送消息通知给被回复的用户
            if (repliedUserId && repliedUserId !== this.currentUserId) {
              this.sendCommentNotification(repliedUserId, this.postInfo.postTitle)
            }
          } else {
            uni.showToast({ title: res.msg || '回复失败', icon: 'none' })
          }
        } catch (error) {
          console.error('回复失败:', error)
          uni.showToast({ title: '回复失败，请重试', icon: 'none' })
        }
      },
      // 发送评论通知（后端已自动发送，这里保留作为备用）
      async sendCommentNotification(receiverId, postTitle) {
        // 后端已在评论时自动发送通知，这里不需要再调用
      },
      // 加载评论列表
      async loadComments() {
        try {
          const res = await getCommentList(this.postId)
          if (res.code === 200) {
            this.comments = this.formatComments(res.data || [])
          }
        } catch (error) {
          console.error('加载评论列表失败:', error)
        }
      },
      // 格式化评论数据（处理树形结构）
      formatComments(comments) {
        return comments.map(item => {
          // 如果是当前用户的评论，使用 store 中的头像（确保是最新的）
          let avatar = item.avatar
          if (item.userId && this.currentUserId && String(item.userId) === String(this.currentUserId) && store.getters.avatar) {
            avatar = store.getters.avatar
          } else {
            // 处理头像URL，确保正确拼接baseUrl
            avatar = processAvatarUrl(avatar)
          }
          return {
            commentId: item.commentId,
            userId: item.userId,
            nickName: item.nickName || '用户',
            avatar: avatar,
            content: item.commentContent || '',
            createTime: item.createTime || '',
            likeCount: item.likeCount || 0,
            isLiked: item.isLiked || false,
            children: item.children ? this.formatComments(item.children) : []
          }
        })
      },
      formatTime(time) {
        return formatRelativeTime(time)
      }
    }
  }
</script>

<style scoped>
  .container {
    background-color: #f5f5f5;
    min-height: 100vh;
    padding-bottom: 120rpx;
  }

  .post-card {
    background-color: #fff;
    padding: 30rpx;
    margin-bottom: 20rpx;
  }

  .post-header {
    display: flex;
    flex-direction: row;
    align-items: center;
    margin-bottom: 20rpx;
  }

  .author-avatar {
    width: 60rpx;
    height: 60rpx;
    border-radius: 50%;
    margin-right: 15rpx;
  }

  .author-info {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  .author-name {
    font-size: 28rpx;
    color: #333;
    margin-bottom: 5rpx;
  }

  .post-time {
    font-size: 22rpx;
    color: #999;
  }

  .post-category {
    background-color: #fff7e6;
    color: #E6A23C;
    padding: 5rpx 15rpx;
    border-radius: 20rpx;
    font-size: 22rpx;
  }

  .post-title {
    font-size: 36rpx;
    color: #333;
    font-weight: bold;
    display: block;
    margin-bottom: 20rpx;
  }

  .post-content {
    margin-bottom: 20rpx;
  }

  .content-text {
    font-size: 30rpx;
    color: #333;
    line-height: 1.8;
    display: block;
    margin-bottom: 20rpx;
  }

  .post-images {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
  }

  .content-image {
    width: 200rpx;
    height: 200rpx;
    border-radius: 10rpx;
    margin-right: 10rpx;
    margin-bottom: 10rpx;
  }

  .post-stats {
    display: flex;
    flex-direction: row;
    justify-content: space-around;
    padding-top: 20rpx;
    border-top: 1rpx solid #f0f0f0;
  }

  .stat-item {
    display: flex;
    align-items: center;
    font-size: 24rpx;
    color: #999;
  }

  .stat-item text {
    margin-left: 8rpx;
  }

  .comment-section {
    background-color: #fff;
    padding: 30rpx;
  }

  .section-title {
    font-size: 32rpx;
    color: #333;
    font-weight: bold;
    margin-bottom: 20rpx;
  }

  .comment-list {
    margin-top: 20rpx;
  }

  .comment-item {
    display: flex;
    flex-direction: row;
    margin-bottom: 30rpx;
  }

  .comment-avatar {
    width: 60rpx;
    height: 60rpx;
    border-radius: 50%;
    margin-right: 15rpx;
  }

  .comment-content {
    flex: 1;
  }

  .comment-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10rpx;
  }

  .comment-author {
    font-size: 26rpx;
    color: #333;
  }

  .comment-time {
    font-size: 22rpx;
    color: #999;
  }

  .comment-text {
    font-size: 28rpx;
    color: #666;
    line-height: 1.6;
  }

  .comment-actions {
    display: flex;
    flex-direction: row;
    margin-top: 15rpx;
  }

  .reply-btn {
    display: flex;
    align-items: center;
    font-size: 24rpx;
    color: #999;
  }

  .reply-btn text {
    margin-left: 8rpx;
  }

  .reply-input-wrapper {
    background-color: #fff;
    padding: 20rpx;
    border-top: 1rpx solid #f0f0f0;
    margin-top: 20rpx;
  }

  .reply-input-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15rpx;
    font-size: 26rpx;
    color: #666;
  }

  .cancel-btn {
    color: #409EFF;
    font-size: 26rpx;
  }

  .reply-input-bar {
    display: flex;
    flex-direction: row;
    align-items: center;
  }

  .reply-input-bar .input {
    flex: 1;
    height: 60rpx;
    background-color: #f5f5f5;
    border-radius: 30rpx;
    padding: 0 20rpx;
    font-size: 26rpx;
  }

  .reply-input-bar .send-btn {
    margin-left: 15rpx;
    background-color: #409EFF;
    color: #fff;
    padding: 12rpx 30rpx;
    border-radius: 30rpx;
    font-size: 26rpx;
  }
  
  .reply-list {
    margin-top: 20rpx;
    padding-left: 20rpx;
    border-left: 2rpx solid #f0f0f0;
  }
  
  .reply-item {
    display: flex;
    flex-direction: row;
    margin-bottom: 20rpx;
  }
  
  .reply-avatar {
    width: 50rpx;
    height: 50rpx;
    border-radius: 50%;
    margin-right: 15rpx;
  }
  
  .reply-content {
    flex: 1;
  }
  
  .reply-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8rpx;
  }
  
  .reply-author {
    font-size: 24rpx;
    color: #333;
  }
  
  .reply-time {
    font-size: 20rpx;
    color: #999;
  }
  
  .reply-text {
    font-size: 26rpx;
    color: #666;
    line-height: 1.6;
  }

  .bottom-input {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: #fff;
    display: flex;
    flex-direction: row;
    align-items: center;
    padding: 20rpx;
    box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.1);
  }

  .input {
    flex: 1;
    height: 70rpx;
    background-color: #f5f5f5;
    border-radius: 35rpx;
    padding: 0 30rpx;
    font-size: 28rpx;
    margin-right: 20rpx;
  }

  .send-btn {
    background-color: #E6A23C;
    color: #fff;
    padding: 15rpx 40rpx;
    border-radius: 35rpx;
    font-size: 28rpx;
  }

  .menu-btn {
    position: fixed;
    top: calc(var(--status-bar-height, 0px) + 12rpx);
    right: 20rpx;
    width: 60rpx;
    height: 60rpx;
    background-color: rgba(255, 255, 255, 0.9);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 999;
    box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.1);
  }
</style>

