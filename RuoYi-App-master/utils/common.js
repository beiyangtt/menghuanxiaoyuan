import config from '@/config'
import { isEmpty, isHttp } from '@/utils/validate'

const apiBase = (config.baseUrl || '').replace(/\/+$/, '')

/**
* 显示消息提示框
* @param content 提示的标题
*/
export function toast(content) {
  uni.showToast({
    icon: 'none',
    title: content
  })
}

/**
* 显示模态弹窗
* @param content 提示的标题
*/
export function showConfirm(content) {
  return new Promise((resolve, reject) => {
    uni.showModal({
      title: '提示',
      content: content,
      cancelText: '取消',
      confirmText: '确定',
      success: function(res) {
        resolve(res)
      }
    })
  })
}

/**
* 参数处理
* @param params 参数
*/
export function tansParams(params) {
  let result = ''
  for (const propName of Object.keys(params)) {
    const value = params[propName]
    var part = encodeURIComponent(propName) + "="
    if (value !== null && value !== "" && typeof (value) !== "undefined") {
      if (typeof value === 'object') {
        for (const key of Object.keys(value)) {
          if (value[key] !== null && value[key] !== "" && typeof (value[key]) !== 'undefined') {
            let params = propName + '[' + key + ']'
            var subPart = encodeURIComponent(params) + "="
            result += subPart + encodeURIComponent(value[key]) + "&"
          }
        }
      } else {
        result += part + encodeURIComponent(value) + "&"
      }
    }
  }
  return result
}

/**
 * 将上传返回的路径转换为标准的 /profile/... 形式
 * 用于存储到数据库，确保只保存相对路径
 * @param {String} url
 * @returns {String}
 */
export function normalizeUploadPath(url) {
  if (!url || isEmpty(url)) {
    return ''
  }
  let result = String(url).trim()
  
  // 如果已经是标准的 /profile/... 格式，直接返回
  if (result.startsWith('/profile/')) {
    return result
  }
  
  // 如果是完整HTTP URL，提取相对路径
  if (isHttp(result)) {
    // 提取 /profile/ 部分
    const profileMatch = result.match(/\/profile\/[^?#]*/)
    if (profileMatch) {
      result = profileMatch[0]
      return result
    }
    // 如果没有 /profile/，尝试提取 /prod-api/profile/ 部分
    const prodApiMatch = result.match(/\/prod-api(\/profile\/[^?#]*)/)
    if (prodApiMatch) {
      result = prodApiMatch[1]
      return result
    }
    // 如果都没有，返回空（说明不是有效的上传路径）
    return ''
  }
  
  // 移除 /prod-api 前缀（如果有）
  if (result.startsWith('/prod-api')) {
    result = result.substring('/prod-api'.length)
  }
  
  // 如果已经是 /profile/ 开头，直接返回
  if (result.startsWith('/profile/')) {
    return result
  }
  
  // 尝试查找 /profile/ 的位置
  const profileIndex = result.indexOf('/profile/')
  if (profileIndex !== -1) {
    result = result.substring(profileIndex)
    return result
  }
  
  // 如果都不匹配，且不是以 / 开头，可能是相对路径
  // 但为了安全，不自动添加 /profile/，返回空
  // 因为可能是无效的路径
  if (!result.startsWith('/')) {
    return ''
  }
  
  // 如果是以 / 开头但不是 /profile/，可能是其他路径
  // 为了安全，返回空
  return ''
}

/**
 * 将文件路径转换为当前环境可访问的完整URL
 * @param {String} url - 可能是相对路径或完整URL
 * @returns {String}
 */
export function resolveFileUrl(url) {
  if (!url || isEmpty(url)) {
    return ''
  }
  
  // 处理相对路径
  let path = String(url).trim()
  
  // 如果已经是完整HTTP URL，检查是否需要转换
  if (isHttp(path)) {
    // 如果是完整URL，提取 /profile/ 部分
    const profileMatch = path.match(/\/profile\/[^?#]*/)
    if (profileMatch) {
      path = profileMatch[0]
    } else {
      // 如果没有 /profile/，可能是其他URL，直接返回
      return path
    }
  }
  
  // 移除可能的 /prod-api 前缀
  if (path.startsWith('/prod-api')) {
    path = path.substring('/prod-api'.length)
  }
  
  // 确保路径以 /profile/ 开头
  if (!path.startsWith('/profile/')) {
    // 尝试查找 /profile/ 的位置
    const profileIndex = path.indexOf('/profile/')
    if (profileIndex !== -1) {
      path = path.substring(profileIndex)
    } else {
      // 如果没有找到 /profile/，尝试添加
      // 但需要确保路径格式正确
      if (path.startsWith('/')) {
        // 如果已经是绝对路径，尝试添加 /profile
        if (!path.startsWith('/profile')) {
          path = '/profile' + path
        }
      } else {
        // 相对路径，添加 /profile/
        path = '/profile/' + path
      }
    }
  }
  
  // 拼接 baseUrl（如果存在）
  if (apiBase) {
    // 确保 apiBase 和 path 正确拼接
    const base = apiBase.endsWith('/') ? apiBase.slice(0, -1) : apiBase
    const finalPath = path.startsWith('/') ? path : '/' + path
    return `${base}${finalPath}`
  }
  
  return path
}

/**
 * 解析时间 - 修复时区问题
 * 
 * 问题根源：
 * 后端返回的时间可能是服务器本地时间（UTC+8），但被错误地当作UTC时间返回（带Z）
 * 前端解析时，浏览器会将带Z的时间当作UTC，自动转换为本地时间（+8），导致时间被多转换一次
 * 
 * 修复策略：
 * 1. 对于带Z的UTC时间字符串，去掉Z标记，让浏览器按本地时间解析
 * 2. 解析后检测时间差，如果接近7-8小时，说明时间被错误处理，需要加上偏移修正
 */
function normalizeDateInput(time) {
  if (time === undefined || time === null || time === '') {
    return null
  }
  if (time instanceof Date) {
    return time
  }
  if (typeof time === 'number') {
    // 时间戳：秒级转毫秒级
    const timestamp = time < 10000000000 ? time * 1000 : time
    const date = new Date(timestamp)
    return isNaN(date.getTime()) ? null : date
  }
  
  let value = String(time).trim()
  if (!value) {
    return null
  }
  
  let date = null
  
  // 优先处理ISO 8601格式（带时区信息）
  if (value.includes('T')) {
    // 如果包含 T，说明是ISO格式
    if (value.endsWith('Z')) {
      // 带Z的UTC时间字符串
      // 去掉Z标记，让浏览器按本地时间解析
      const valueWithoutZ = value.slice(0, -1)
      date = new Date(valueWithoutZ)
      if (isNaN(date.getTime())) {
        // 如果去掉Z后解析失败，尝试原值解析
        date = new Date(value)
      }
    } else if (value.includes('+') || value.match(/-\d{2}:\d{2}$/)) {
      // 有其他时区信息（如 +08:00 或 -05:00），正常解析
      date = new Date(value)
    } else {
      // 没有时区信息的ISO格式，浏览器会按本地时区解析
      date = new Date(value)
    }
  } else {
    // 如果后端返回 yyyy-MM-dd HH:mm:ss 格式（无时区）
    const dateTimeMatch = value.match(/^(\d{4})-(\d{2})-(\d{2})\s+(\d{2}):(\d{2}):(\d{2})(?:\.(\d+))?$/)
    if (dateTimeMatch) {
      // 将空格替换为T，构造ISO格式字符串
      const localString = `${dateTimeMatch[1]}-${dateTimeMatch[2]}-${dateTimeMatch[3]}T${dateTimeMatch[4]}:${dateTimeMatch[5]}:${dateTimeMatch[6]}${dateTimeMatch[7] ? '.' + dateTimeMatch[7].padEnd(3, '0') : ''}`
      date = new Date(localString)
    } else {
      // 最后的兜底：尝试直接解析
      date = new Date(value)
    }
  }
  
  // 如果解析失败，返回null
  if (!date || isNaN(date.getTime())) {
    return null
  }
  
  // 关键修复：检测并修正时间偏差
  // 问题：刚发布的内容显示"7-8小时前"，说明时间被错误处理了
  // 如果解析后的时间距离现在接近7-8小时，说明是刚创建的内容被错误处理了
  const now = Date.now()
  const timeValue = date.getTime()
  const diff = now - timeValue
  
  // 如果时间差为负数（未来时间），不需要修正
  if (diff < 0) {
    return date
  }
  
  // 1小时的毫秒数
  const oneHourMs = 60 * 60 * 1000
  // 6小时的毫秒数（最小检测范围）
  const sixHoursMs = 6 * 60 * 60 * 1000
  // 10小时的毫秒数（最大检测范围）
  const tenHoursMs = 10 * 60 * 60 * 1000
  // 30分钟容差
  const tolerance = 30 * 60 * 1000
  
  // 如果时间差在6-10小时之间，很可能是刚创建的内容被错误处理了
  // 修正策略：加上实际的时间差，使得修正后时间接近当前时间（即显示"刚刚"）
  // 例如：如果时间差是7小时，加上7小时，时间就变成当前时间
  if (diff >= (sixHoursMs - tolerance) && diff <= (tenHoursMs + tolerance)) {
    // 计算需要加上的小时数（向下取整到小时）
    const hoursDiff = Math.floor(diff / oneHourMs)
    const offsetMs = hoursDiff * oneHourMs
    const correctedTime = timeValue + offsetMs
    const correctedDiff = Math.abs(now - correctedTime)
    
    // 修正后，如果时间差在合理范围内（小于1小时），说明修正成功
    // 这说明原本应该是"刚刚"的内容被错误地显示为"7-8小时前"
    if (correctedDiff < oneHourMs) {
      date = new Date(correctedTime)
    }
  }
  
  return date
}

function pad2(num) {
  return num < 10 ? `0${num}` : `${num}`
}

/**
 * 将时间格式化为 YYYY-MM-DD HH:mm
 * @param {Date|String|Number} time
 * @param {String} pattern
 * @returns {String}
 */
export function formatDateTime(time, pattern = 'YYYY-MM-DD HH:mm') {
  const date = normalizeDateInput(time)
  if (!date) return ''
  const year = date.getFullYear()
  const month = pad2(date.getMonth() + 1)
  const day = pad2(date.getDate())
  const hour = pad2(date.getHours())
  const minute = pad2(date.getMinutes())
  const second = pad2(date.getSeconds())
  return pattern
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hour)
    .replace('mm', minute)
    .replace('ss', second)
}

/**
 * 友好时间展示，自动使用"刚刚/分钟前/小时前/天前"描述
 * @param {Date|String|Number} time
 * @param {{fallbackToDate?: boolean}} options
 * @returns {String}
 */
export function formatRelativeTime(time, options = {}) {
  const { fallbackToDate = true } = options
  const date = normalizeDateInput(time)
  if (!date) return ''
  
  // 计算时间差（毫秒）
  const now = Date.now()
  const timeValue = date.getTime()
  const diff = now - timeValue
  
  // 如果时间差为负数（未来时间），返回格式化日期
  if (diff < 0) {
    return fallbackToDate ? formatDateTime(date) : ''
  }
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  
  if (diff < minute) return '刚刚'
  if (diff < hour) return `${Math.floor(diff / minute)}分钟前`
  if (diff < day) return `${Math.floor(diff / hour)}小时前`
  if (diff < 7 * day) return `${Math.floor(diff / day)}天前`
  return fallbackToDate ? formatDateTime(date) : ''
}

/**
 * 将任意时间解析为本地Date对象
 * @param {Date|String|Number} time
 * @returns {Date|null}
 */
export function toLocalDate(time) {
  return normalizeDateInput(time)
}
