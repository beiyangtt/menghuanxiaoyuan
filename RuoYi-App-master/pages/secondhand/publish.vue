<template>
  <view class="container">
    <view class="form-section">
      <view class="form-item">
        <text class="label">商品标题</text>
        <input class="input" v-model="form.goodsTitle" placeholder="请输入商品标题" />
      </view>

      <view class="form-item">
        <text class="label">商品分类</text>
        <picker 
          mode="selector" 
          :range="categories" 
          range-key="name" 
          :disabled="categoryLoading || categories.length === 0"
          @change="onCategoryChange">
          <view class="picker">
            {{ form.categoryName || '请选择分类' }}
          </view>
        </picker>
      </view>

      <view class="form-item">
        <text class="label">商品价格</text>
        <input class="input" type="digit" v-model="form.goodsPrice" placeholder="请输入价格" />
      </view>

      <view class="form-item">
        <text class="label">商品描述</text>
        <textarea class="textarea" v-model="form.goodsDesc" placeholder="请输入商品描述" />
      </view>

      <view class="form-item">
        <text class="label">商品图片</text>
        <view class="upload-area">
          <view class="image-item" v-for="(img, index) in form.images" :key="index">
            <image :src="getImageSrc(img)" mode="aspectFill" class="upload-image"></image>
            <view class="delete-btn" @click="deleteImage(index)">×</view>
          </view>
          <view class="upload-btn" @click="chooseImage" v-if="form.images.length < 9">
            <uni-icons type="plus" size="40" color="#999"></uni-icons>
            <text>添加图片</text>
          </view>
        </view>
      </view>
    </view>

    <view class="submit-btn" @click="handleSubmit">{{ isEdit ? '更新商品' : '发布商品' }}</view>
  </view>
</template>

<script>
  import { addGoods, updateGoods, getGoodsDetail, getCategoryList } from '@/api/secondhand'
  import { uploadFiles } from '@/api/common'
  import { resolveFileUrl, normalizeUploadPath } from '@/utils/common'
  
  export default {
    data() {
      return {
        isEdit: false, // 是否是编辑模式
        goodsId: null, // 编辑时的商品ID
        categories: [],
        categoryLoading: false,
        form: {
          goodsTitle: '',
          categoryId: '',
          categoryName: '',
          goodsPrice: '',
          goodsDesc: '',
          images: []
        }
      }
    },
    onLoad(options) {
      this.loadCategories()
      // 判断是否是编辑模式
      if (options.id && options.mode === 'edit') {
        this.isEdit = true
        this.goodsId = options.id
        this.loadGoodsDetail()
      }
    },
    methods: {
      // 加载分类列表
      async loadCategories() {
        this.categoryLoading = true
        try {
          const res = await getCategoryList()
          if (res.code === 200) {
            const normalized = this.normalizeCategoryOptions(res.data ?? res.rows ?? res)
            if (normalized.length === 0) {
              this.categories = this.getFallbackCategories()
              uni.showToast({
                title: '暂无可用分类，请先在后台配置',
                icon: 'none'
              })
            } else {
              this.categories = normalized
            }
          } else {
            this.handleCategoryLoadFailure()
          }
        } catch (error) {
          console.error('加载分类失败:', error)
          this.handleCategoryLoadFailure()
        } finally {
          this.categoryLoading = false
        }
      },
      normalizeCategoryOptions(payload) {
        const list = Array.isArray(payload)
          ? payload
          : Array.isArray(payload?.data)
            ? payload.data
            : Array.isArray(payload?.rows)
              ? payload.rows
              : []
        return list
          .map(item => {
            const id = item?.categoryId ?? item?.id ?? item?.value ?? item?.dictValue
            const name = item?.categoryName ?? item?.name ?? item?.dictLabel ?? item?.label
            return {
              id,
              name: name || '未命名分类'
            }
          })
          .filter(item => item.id && item.name)
      },
      getFallbackCategories() {
        return [
          { id: 'default', name: '其他' }
        ]
      },
      handleCategoryLoadFailure() {
        if (!this.categories || this.categories.length === 0) {
          this.categories = this.getFallbackCategories()
        }
        uni.showToast({
          title: '分类加载失败，已使用默认分类',
          icon: 'none'
        })
      },
      // 加载商品详情（编辑模式）
      async loadGoodsDetail() {
        try {
          uni.showLoading({ title: '加载中...' })
          const res = await getGoodsDetail(this.goodsId)
          if (res.code === 200) {
            const data = res.data
            // 加载商品图片 - 确保从数据库加载的图片路径不会被误判为临时路径
            let images = []
            if (data.images && data.images.trim()) {
              images = data.images
                .split(',')
                .filter(url => url && url.trim())
                .map(url => {
                  const trimmed = url.trim()
                  // 如果路径是blob:开头（说明之前保存时出错了），保留以便重新上传
                  if (trimmed.toLowerCase().startsWith('blob:')) {
                    return trimmed
                  }
                  // 如果已经是完整URL，保留；如果是相对路径，确保以/开头
                  if (trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
                    return trimmed
                  }
                  // 确保是相对路径格式（/profile/...）
                  return trimmed.startsWith('/') ? trimmed : '/' + trimmed
                })
            }
            this.form = {
              goodsTitle: data.goodsTitle || '',
              categoryId: data.categoryId || '',
              categoryName: data.categoryName || '',
              goodsPrice: data.goodsPrice || '',
              goodsDesc: data.goodsDesc || '',
              images: images
            }
            // 设置导航栏标题
            uni.setNavigationBarTitle({
              title: '编辑商品'
            })
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
      onCategoryChange(e) {
        const index = Number(e.detail.value)
        if (
          Number.isInteger(index) &&
          this.categories &&
          index >= 0 &&
          index < this.categories.length
        ) {
          const selected = this.categories[index]
          this.form.categoryId = selected.id
          this.form.categoryName = selected.name
        }
      },
      isTempPath(path) {
        if (!path || typeof path !== 'string') return false
        const lower = path.toLowerCase()
        return lower.startsWith('http://tmp') ||
          lower.startsWith('https://tmp') ||
          lower.startsWith('file://') ||
          lower.startsWith('tmp/') ||
          lower.startsWith('blob:')
      },
      chooseImage() {
        uni.chooseImage({
          count: 9 - this.form.images.length,
          success: (res) => {
            this.form.images = this.form.images.concat(res.tempFilePaths)
          }
        })
      },
      deleteImage(index) {
        this.form.images.splice(index, 1)
      },
      getImageSrc(path) {
        if (!path) return ''
        if (this.isTempPath(path)) {
          return path
        }
        return resolveFileUrl(path)
      },
      async handleSubmit() {
        if (!this.form.goodsTitle) {
          uni.showToast({ title: '请输入商品标题', icon: 'none' })
          return
        }
        if (!this.form.categoryId) {
          uni.showToast({ title: '请选择分类', icon: 'none' })
          return
        }
        if (!this.form.goodsPrice) {
          uni.showToast({ title: '请输入价格', icon: 'none' })
          return
        }
        try {
          uni.showLoading({ title: this.isEdit ? '更新中...' : '发布中...' })
          
          // 上传图片
          let imageUrls = []
          if (this.form.images && this.form.images.length > 0) {
            const tempImages = []
            const persistedImages = []

            this.form.images.forEach(img => {
              if (this.isTempPath(img)) {
                tempImages.push(img)
              } else {
                persistedImages.push(img)
              }
            })
            
            let uploadedUrls = []
            if (tempImages.length > 0) {
              try {
                uni.showLoading({ title: `上传图片中...` })
                const uploadResults = await uploadFiles(tempImages)
                // 后端返回格式：{code: 200, url: "...", fileName: "/profile/..."}
                // uploadFiles 返回数组，每个元素是一个上传结果
                uploadedUrls = uploadResults
                  .map(result => {
                    // 优先使用 fileName（相对路径）
                    let path = ''
                    if (result && typeof result === 'object') {
                      path = result.fileName || result.data?.fileName || ''
                      // 如果没有 fileName，从 url 中提取
                      if (!path && (result.url || result.data?.url)) {
                        path = normalizeUploadPath(result.url || result.data?.url || '')
                      }
                    } else if (typeof result === 'string') {
                      path = normalizeUploadPath(result)
                    }
                    return normalizeUploadPath(path)
                  })
                  .filter(path => !!path && path.startsWith('/profile/'))
                if (uploadedUrls.length === 0) {
                  uni.hideLoading()
                  uni.showToast({ title: '图片上传失败，未获取到图片URL', icon: 'none' })
                  return
                }
                uni.hideLoading()
              } catch (error) {
                console.error('图片上传失败:', error)
                uni.hideLoading()
                uni.showToast({ title: '图片上传失败，请重试', icon: 'none' })
                return
              }
            }

            const normalizedExisting = persistedImages
              .map(url => normalizeUploadPath(url))
              .filter(path => !!path)

            imageUrls = [...normalizedExisting, ...uploadedUrls]
          }
          
          const submitData = {
            goodsTitle: this.form.goodsTitle,
            categoryId: this.form.categoryId,
            goodsPrice: parseFloat(this.form.goodsPrice),
            goodsDesc: this.form.goodsDesc || '',
            goodsStatus: '0', // 待售
            images: imageUrls.join(',') // 图片URL用逗号分隔
          }
          let res
          if (this.isEdit) {
            // 编辑模式，更新商品
            submitData.goodsId = this.goodsId
            res = await updateGoods(submitData)
          } else {
            // 新增模式，发布商品
            res = await addGoods(submitData)
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
            uni.showToast({ title: res.msg || (this.isEdit ? '更新失败' : '发布失败'), icon: 'none' })
          }
        } catch (error) {
          console.error(this.isEdit ? '更新商品失败:' : '发布商品失败:', error)
          uni.showToast({ title: (this.isEdit ? '更新失败' : '发布失败') + '，请重试', icon: 'none' })
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

  .upload-area {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
  }

  .image-item {
    position: relative;
    width: 200rpx;
    height: 200rpx;
    margin-right: 20rpx;
    margin-bottom: 20rpx;
  }

  .upload-image {
    width: 100%;
    height: 100%;
    border-radius: 10rpx;
  }

  .delete-btn {
    position: absolute;
    top: -10rpx;
    right: -10rpx;
    width: 40rpx;
    height: 40rpx;
    background-color: #f56c6c;
    color: #fff;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 30rpx;
  }

  .upload-btn {
    width: 200rpx;
    height: 200rpx;
    border: 2rpx dashed #ddd;
    border-radius: 10rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #999;
    font-size: 24rpx;
  }

  .submit-btn {
    margin: 40rpx 30rpx;
    background-color: #409EFF;
    color: #fff;
    text-align: center;
    padding: 30rpx;
    border-radius: 50rpx;
    font-size: 32rpx;
  }
</style>

