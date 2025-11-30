import request from '@/utils/request'

// 查询聊天会话列表
export function getChatSessionList() {
  return request({
    url: '/dreamcampus/chat/session/list',
    method: 'get'
  })
}

// 获取或创建会话
export function getChatSession(otherUserId) {
  return request({
    url: '/dreamcampus/chat/session/get/' + otherUserId,
    method: 'get',
    silent: true // 静默模式，不显示错误提示
  })
}

// 查询聊天消息列表
export function getChatMessageList(sessionId) {
  return request({
    url: '/dreamcampus/chat/message/list/' + sessionId,
    method: 'get',
    silent: true // 静默模式，不显示错误提示
  })
}

// 发送聊天消息
export function sendChatMessage(data) {
  return request({
    url: '/dreamcampus/chat/message/send',
    method: 'post',
    data: data
  })
}

// 标记聊天消息为已读
export function markChatAsRead(sessionId) {
  return request({
    url: '/dreamcampus/chat/message/read/' + sessionId,
    method: 'put',
    silent: true // 静默模式，不显示错误提示
  })
}

// 查询消息列表
export function getMessageList(messageType) {
  return request({
    url: '/dreamcampus/message/list',
    method: 'get',
    params: { messageType: messageType }
  })
}

// 标记消息为已读
export function markMessageAsRead(messageId) {
  return request({
    url: '/dreamcampus/message/read/' + messageId,
    method: 'put'
  })
}

// 删除系统消息
export function deleteSystemMessage(messageId) {
  return request({
    url: '/dreamcampus/message/' + messageId,
    method: 'delete'
  })
}

// 查询未读消息数
export function getUnreadCount(messageType) {
  return request({
    url: '/dreamcampus/message/unread/count',
    method: 'get',
    params: { messageType: messageType }
  })
}

// 删除聊天会话（仅当前用户）
export function deleteChatSession(sessionId) {
  return request({
    url: '/dreamcampus/chat/session/' + sessionId,
    method: 'delete'
  })
}

// 发送评论通知
export function sendCommentNotification(data) {
  return request({
    url: '/dreamcampus/message/comment/notify',
    method: 'post',
    data: data
  })
}

