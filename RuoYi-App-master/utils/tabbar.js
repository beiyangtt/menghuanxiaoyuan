import { getUnreadCount } from '@/api/message'

/**
 * 更新底部导航栏消息徽章
 * 消息页面在 tabBar 中的索引是 2（从0开始：首页=0, 论坛=1, 消息=2, 我的=3）
 */
export async function updateTabBarBadge() {
  try {
    const res = await getUnreadCount()
    if (res.code === 200) {
      const count = res.data || 0
      // 消息页面在 tabBar 中的索引是 2
      const messageIndex = 2
      
      if (count > 0) {
        // 设置徽章
        uni.setTabBarBadge({
          index: messageIndex,
          text: count > 99 ? '99+' : String(count)
        })
      } else {
        // 移除徽章
        uni.removeTabBarBadge({
          index: messageIndex
        })
      }
    }
  } catch (error) {
    console.error('更新 tabBar 徽章失败:', error)
  }
}

