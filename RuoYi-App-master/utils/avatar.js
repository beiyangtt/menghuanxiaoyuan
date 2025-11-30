import { isEmpty } from '@/utils/validate'
import { resolveFileUrl } from '@/utils/common'
import defAva from '@/static/images/profile.jpg'

/**
 * 处理头像URL
 * @param {String} avatar 头像路径（可能是相对路径或完整URL）
 * @returns {String} 处理后的完整头像URL
 */
export function processAvatarUrl(avatar) {
  if (!avatar || isEmpty(avatar)) {
    return defAva
  }
  const resolved = resolveFileUrl(avatar)
  return resolved || defAva
}

