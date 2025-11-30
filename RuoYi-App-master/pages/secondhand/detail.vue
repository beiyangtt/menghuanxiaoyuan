<template>
  <view class="container">
    <!-- 右上角菜单按钮 -->
    <view class="menu-btn" v-if="isOwner" @click="showMenu">
      <uni-icons type="more-filled" size="24" color="#333"></uni-icons>
    </view>
    
    <!-- 商品图片轮播 -->
    <view v-if="images && images.length > 0">
      <swiper class="swiper" :indicator-dots="true" :autoplay="false" :circular="true">
        <swiper-item v-for="(img, index) in images" :key="index">
          <image :src="img" mode="aspectFill" class="swiper-image"></image>
        </swiper-item>
      </swiper>
    </view>
    <view v-else class="no-image-placeholder">
      <uni-icons type="image" size="60" color="#ddd"></uni-icons>
      <text class="placeholder-text">暂无图片</text>
    </view>

    <!-- 商品信息 -->
    <view class="goods-info">
      <text class="goods-title">{{ goodsInfo.goodsTitle }}</text>
      <text class="goods-price">¥{{ goodsInfo.goodsPrice }}</text>
      <view class="goods-meta">
        <text class="meta-item">发布时间：{{ formatTime(goodsInfo.createTime) }}</text>
        <text class="meta-item">浏览：{{ goodsInfo.viewCount }}次</text>
      </view>
    </view>

    <!-- 商品详情 -->
    <view class="detail-section">
      <text class="section-title">商品详情</text>
      <text class="detail-content">{{ goodsInfo.goodsDesc }}</text>
    </view>

    <!-- 卖家信息 -->
    <view class="seller-section">
      <text class="section-title">卖家信息</text>
      <view class="seller-info">
        <image :src="sellerInfo.avatar" mode="aspectFill" class="seller-avatar"></image>
        <view class="seller-details">
          <text class="seller-name">{{ sellerInfo.nickName }}</text>
          <text class="seller-rating">信用：{{ sellerInfo.rating }}</text>
        </view>
        <view class="contact-btn" v-if="!isOwner" @click="contactSeller">
          <uni-icons type="chat" size="20" color="#fff"></uni-icons>
          <text>联系卖家</text>
        </view>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="action-btn" @click="addToFavorite" v-if="!isOwner">
        <uni-icons type="heart" :color="isFavorite ? '#f56c6c' : '#666'" size="24"></uni-icons>
        <text>收藏</text>
      </view>
      <view class="buy-btn" :class="{ 'disabled': goodsInfo.goodsStatus === '1' }" @click="handleBuy">
        {{ isOwner ? (goodsInfo.goodsStatus === '1' ? '已售出' : '完成交易') : '立即购买' }}
      </view>
    </view>
  </view>
</template>

<script>
  import { getGoodsDetail, deleteGoods, completeTrade } from '@/api/secondhand'
  import store from '@/store'
  import { processAvatarUrl } from '@/utils/avatar'
  import { resolveFileUrl, formatDateTime } from '@/utils/common'
  
  export default {
    data() {
      return {
        goodsId: null,
        isFavorite: false,
        images: [],
        isOwner: false, // 是否是发布者
        currentUserId: null, // 当前登录用户ID
        goodsInfo: {
          goodsTitle: '',
          goodsPrice: '',
          createTime: '',
          viewCount: 0,
          goodsDesc: '',
          goodsStatus: '0', // 商品状态
          userId: null // 发布者ID
        },
        sellerInfo: {
          nickName: '',
          avatar: '/static/images/profile.jpg',
          rating: '5.0'
        }
      }
    },
    onLoad(options) {
      if (options.id) {
        this.goodsId = options.id
        // 获取当前登录用户ID
        this.currentUserId = store.getters.id
        this.loadGoodsDetail()
      }
    },
    methods: {
      // 加载商品详情
      async loadGoodsDetail() {
        try {
          uni.showLoading({ title: '加载中...' })
          const res = await getGoodsDetail(this.goodsId)
          if (res.code === 200) {
            const data = res.data
            this.goodsInfo = {
              goodsTitle: data.goodsTitle || '',
              goodsPrice: data.goodsPrice || 0,
              createTime: data.createTime || '',
              viewCount: data.viewCount || 0,
              goodsDesc: data.goodsDesc || '',
              goodsStatus: data.goodsStatus || '0',
              userId: data.userId || null
            }
            this.sellerInfo = {
              nickName: data.nickName || '用户',
              avatar: processAvatarUrl(data.avatar),
              rating: '5.0'
            }
            // 判断是否是发布者
            this.isOwner = this.currentUserId && this.goodsInfo.userId && 
                          String(this.currentUserId) === String(this.goodsInfo.userId)
            
            // 如果商品已售出且不是发布者，不允许访问
            if (this.goodsInfo.goodsStatus === '1' && !this.isOwner) {
              uni.hideLoading()
              uni.showToast({
                title: '该商品已售出',
                icon: 'none',
                duration: 2000
              })
              setTimeout(() => {
                uni.navigateBack()
              }, 2000)
              return
            }
            
            // 加载商品图片（如果没有图片则不显示）
            if (data.images && data.images.trim()) {
              const rawImages = data.images.split(',').filter(url => url && url.trim())
              
              this.images = rawImages
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
            } else {
              this.images = []
            }
          } else {
            uni.showToast({
              title: res.msg || '加载失败',
              icon: 'none'
            })
          }
        } catch (error) {
          console.error('加载商品详情失败:', error)
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
          itemList: ['重新编辑', '删除商品'],
          success: (res) => {
            if (res.tapIndex === 0) {
              // 重新编辑
              this.editGoods()
            } else if (res.tapIndex === 1) {
              // 删除商品
              this.deleteGoods()
            }
          }
        })
      },
      // 编辑商品
      editGoods() {
        uni.navigateTo({
          url: `/pages/secondhand/publish?id=${this.goodsId}&mode=edit`
        })
      },
      // 删除商品
      deleteGoods() {
        uni.showModal({
          title: '提示',
          content: '确定要删除该商品吗？删除后无法恢复。',
          success: async (res) => {
            if (res.confirm) {
              try {
                uni.showLoading({ title: '删除中...' })
                const result = await deleteGoods(this.goodsId)
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
                console.error('删除商品失败:', error)
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
      contactSeller() {
        // 跳转到聊天界面
        uni.navigateTo({
          url: `/pages/message/chat?userId=${this.goodsInfo.userId}`
        })
      },
      addToFavorite() {
        this.isFavorite = !this.isFavorite
        uni.showToast({
          title: this.isFavorite ? '已收藏' : '已取消收藏',
          icon: 'success'
        })
      },
      async handleBuy() {
        // 如果是发布者，执行完成交易
        if (this.isOwner) {
          if (this.goodsInfo.goodsStatus === '1') {
            uni.showToast({
              title: '该商品已售出',
              icon: 'none'
            })
            return
          }
          uni.showModal({
            title: '提示',
            content: '确定该商品已完成交易吗？完成后其他用户将无法购买。',
            success: async (res) => {
              if (res.confirm) {
                try {
                  uni.showLoading({ title: '处理中...' })
                  const result = await completeTrade(this.goodsId)
                  if (result.code === 200) {
                    uni.showToast({
                      title: '交易完成',
                      icon: 'success'
                    })
                    // 重新加载商品详情
                    this.loadGoodsDetail()
                  } else {
                    uni.showToast({
                      title: result.msg || '操作失败',
                      icon: 'none'
                    })
                  }
                } catch (error) {
                  console.error('完成交易失败:', error)
                  uni.showToast({
                    title: '操作失败，请重试',
                    icon: 'none'
                  })
                } finally {
                  uni.hideLoading()
                }
              }
            }
          })
        } else {
          // 其他用户点击立即购买
          uni.showToast({
            title: '购买功能待开发，请与用户私聊',
            icon: 'none',
            duration: 2000
          })
        }
      },
      formatTime(time) {
        return formatDateTime(time)
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

  .swiper {
    width: 100%;
    height: 500rpx;
    background-color: #fff;
  }

  .swiper-image {
    width: 100%;
    height: 100%;
  }

  .no-image-placeholder {
    width: 100%;
    height: 500rpx;
    background-color: #f5f5f5;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }

  .no-image-placeholder .placeholder-text {
    font-size: 28rpx;
    color: #999;
    margin-top: 20rpx;
  }

  .goods-info {
    background-color: #fff;
    padding: 30rpx;
    margin-top: 20rpx;
  }

  .goods-title {
    font-size: 36rpx;
    color: #333;
    font-weight: bold;
    display: block;
    margin-bottom: 20rpx;
  }

  .goods-price {
    font-size: 48rpx;
    color: #f56c6c;
    font-weight: bold;
    display: block;
    margin-bottom: 20rpx;
  }

  .goods-meta {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
  }

  .meta-item {
    font-size: 24rpx;
    color: #999;
  }

  .detail-section {
    background-color: #fff;
    padding: 30rpx;
    margin-top: 20rpx;
  }

  .section-title {
    font-size: 32rpx;
    color: #333;
    font-weight: bold;
    display: block;
    margin-bottom: 20rpx;
  }

  .detail-content {
    font-size: 28rpx;
    color: #666;
    line-height: 1.8;
    display: block;
  }

  .seller-section {
    background-color: #fff;
    padding: 30rpx;
    margin-top: 20rpx;
  }

  .seller-info {
    display: flex;
    flex-direction: row;
    align-items: center;
  }

  .seller-avatar {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    margin-right: 20rpx;
  }

  .seller-details {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  .seller-name {
    font-size: 30rpx;
    color: #333;
    margin-bottom: 10rpx;
  }

  .seller-rating {
    font-size: 24rpx;
    color: #999;
  }

  .contact-btn {
    background-color: #409EFF;
    color: #fff;
    padding: 15rpx 30rpx;
    border-radius: 30rpx;
    display: flex;
    align-items: center;
    font-size: 26rpx;
  }

  .bottom-bar {
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

  .action-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-right: 30rpx;
    font-size: 24rpx;
    color: #666;
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

  .buy-btn {
    flex: 1;
    background-color: #409EFF;
    color: #fff;
    text-align: center;
    padding: 25rpx;
    border-radius: 50rpx;
    font-size: 32rpx;
  }

  .buy-btn.disabled {
    background-color: #ccc;
    color: #999;
  }
</style>

