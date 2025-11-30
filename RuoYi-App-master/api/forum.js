import request from '@/utils/request'

// 查询论坛帖子列表
export function getPostList(params) {
  return request({
    url: '/dreamcampus/forum/list',
    method: 'get',
    params: params
  })
}

// 查询论坛帖子详情
export function getPostDetail(postId) {
  return request({
    url: '/dreamcampus/forum/' + postId,
    method: 'get'
  })
}

// 新增论坛帖子
export function addPost(data) {
  return request({
    url: '/dreamcampus/forum',
    method: 'post',
    data: data
  })
}

// 修改论坛帖子
export function updatePost(data) {
  return request({
    url: '/dreamcampus/forum',
    method: 'put',
    data: data
  })
}

// 删除论坛帖子
export function deletePost(postIds) {
  return request({
    url: '/dreamcampus/forum/' + postIds,
    method: 'delete'
  })
}

// 点赞/取消点赞
export function toggleLike(postId) {
  return request({
    url: '/dreamcampus/forum/like/' + postId,
    method: 'put'
  })
}

// 添加评论
export function addComment(data) {
  return request({
    url: '/dreamcampus/forum/comment',
    method: 'post',
    data: data
  })
}

// 查询论坛分类列表
export function getCategoryList() {
  return request({
    url: '/dreamcampus/forum/category/list',
    method: 'get'
  })
}

// 查询评论列表
export function getCommentList(postId) {
  return request({
    url: '/dreamcampus/forum/comment/list/' + postId,
    method: 'get'
  })
}

