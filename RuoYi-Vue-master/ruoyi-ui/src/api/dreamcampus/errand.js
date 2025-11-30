import request from '@/utils/request'

// 查询跑腿任务列表（管理员）
export function listTask(query) {
  return request({
    url: '/dreamcampus/errand/list',
    method: 'get',
    params: query
  })
}

// 删除跑腿任务（管理员）
export function delTask(taskIds) {
  return request({
    url: '/dreamcampus/errand/' + taskIds,
    method: 'delete'
  })
}

// 查询跑腿任务详细
export function getTask(taskId) {
  return request({
    url: '/dreamcampus/errand/' + taskId,
    method: 'get'
  })
}


