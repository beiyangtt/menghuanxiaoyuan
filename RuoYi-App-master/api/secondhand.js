import request from '@/utils/request'

// 查询二手商品列表
export function getGoodsList(params) {
  return request({
    url: '/dreamcampus/secondhand/list',
    method: 'get',
    params: params
  })
}

// 查询二手商品详情
export function getGoodsDetail(goodsId) {
  return request({
    url: '/dreamcampus/secondhand/' + goodsId,
    method: 'get'
  })
}

// 新增二手商品
export function addGoods(data) {
  return request({
    url: '/dreamcampus/secondhand',
    method: 'post',
    data: data
  })
}

// 修改二手商品
export function updateGoods(data) {
  return request({
    url: '/dreamcampus/secondhand',
    method: 'put',
    data: data
  })
}

// 删除二手商品
export function deleteGoods(goodsIds) {
  return request({
    url: '/dreamcampus/secondhand/' + goodsIds,
    method: 'delete'
  })
}

// 查询商品分类列表
export function getCategoryList() {
  return request({
    url: '/dreamcampus/secondhand/category/list',
    method: 'get'
  })
}

// 完成交易（将商品状态改为已售）
export function completeTrade(goodsId) {
  return request({
    url: '/dreamcampus/secondhand/complete/' + goodsId,
    method: 'put'
  })
}

// 查询热门二手商品
export function getHotGoods(limit = 10) {
  return request({
    url: '/dreamcampus/secondhand/hot',
    method: 'get',
    params: { limit: limit }
  })
}

// 查询收藏的商品列表
export function getFavoriteList() {
  return request({
    url: '/dreamcampus/secondhand/favorite/list',
    method: 'get'
  })
}

// 查询我发布的商品列表
export function getMyGoodsList() {
  return request({
    url: '/dreamcampus/secondhand/my/list',
    method: 'get'
  })
}

// 查询我的订单列表
export function getMyOrderList() {
  return request({
    url: '/dreamcampus/secondhand/order/my/list',
    method: 'get'
  })
}

// 查询收藏数量
export function getFavoriteCount() {
  return request({
    url: '/dreamcampus/secondhand/favorite/count',
    method: 'get'
  })
}

// 查询我发布的商品数量
export function getMyGoodsCount() {
  return request({
    url: '/dreamcampus/secondhand/my/count',
    method: 'get'
  })
}

