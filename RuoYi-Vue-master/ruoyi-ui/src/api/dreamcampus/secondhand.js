import request from '@/utils/request'

// 查询二手商品列表（管理员）
export function listGoods(query) {
  return request({
    url: '/dreamcampus/secondhand/list',
    method: 'get',
    params: query
  })
}

// 删除二手商品（管理员）
export function delGoods(goodsIds) {
  return request({
    url: '/dreamcampus/secondhand/' + goodsIds,
    method: 'delete'
  })
}

// 查询二手商品详细
export function getGoods(goodsId) {
  return request({
    url: '/dreamcampus/secondhand/' + goodsId,
    method: 'get'
  })
}


