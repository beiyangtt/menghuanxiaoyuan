import request from '@/utils/request'

// 查询论坛帖子列表（管理员）
export function listPost(query) {
  return request({
    url: '/dreamcampus/forum/list',
    method: 'get',
    params: query
  })
}

// 删除论坛帖子（管理员）
export function delPost(postIds) {
  return request({
    url: '/dreamcampus/forum/' + postIds,
    method: 'delete'
  })
}

// 查询论坛帖子详细
export function getPost(postId) {
  return request({
    url: '/dreamcampus/forum/' + postId,
    method: 'get'
  })
}


