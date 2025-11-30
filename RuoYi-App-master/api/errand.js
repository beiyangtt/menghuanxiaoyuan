import request from '@/utils/request'

// 查询跑腿任务列表
export function getTaskList(params) {
  return request({
    url: '/dreamcampus/errand/list',
    method: 'get',
    params: params
  })
}

// 查询跑腿任务详情
export function getTaskDetail(taskId) {
  return request({
    url: '/dreamcampus/errand/' + taskId,
    method: 'get'
  })
}

// 新增跑腿任务
export function addTask(data) {
  return request({
    url: '/dreamcampus/errand',
    method: 'post',
    data: data
  })
}

// 修改跑腿任务
export function updateTask(data) {
  return request({
    url: '/dreamcampus/errand',
    method: 'put',
    data: data
  })
}

// 删除跑腿任务
export function deleteTask(taskIds) {
  return request({
    url: '/dreamcampus/errand/' + taskIds,
    method: 'delete'
  })
}

// 接受任务
export function acceptTask(taskId) {
  return request({
    url: '/dreamcampus/errand/accept/' + taskId,
    method: 'put'
  })
}

// 完成任务
export function completeTask(taskId) {
  return request({
    url: '/dreamcampus/errand/complete/' + taskId,
    method: 'put'
  })
}

// 查询热门跑腿任务
export function getHotTasks(limit = 10) {
  return request({
    url: '/dreamcampus/errand/hot',
    method: 'get',
    params: { limit: limit }
  })
}

