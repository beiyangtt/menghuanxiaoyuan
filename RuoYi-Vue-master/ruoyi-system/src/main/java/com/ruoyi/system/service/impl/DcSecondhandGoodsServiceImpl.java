package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.DcSecondhandGoodsMapper;
import com.ruoyi.system.domain.DcSecondhandGoods;
import com.ruoyi.system.service.IDcSecondhandGoodsService;

/**
 * 二手商品Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class DcSecondhandGoodsServiceImpl implements IDcSecondhandGoodsService 
{
    @Autowired
    private DcSecondhandGoodsMapper dcSecondhandGoodsMapper;

    /**
     * 查询二手商品
     * 
     * @param goodsId 二手商品主键
     * @return 二手商品
     */
    @Override
    public DcSecondhandGoods selectDcSecondhandGoodsByGoodsId(Long goodsId)
    {
        return dcSecondhandGoodsMapper.selectDcSecondhandGoodsByGoodsId(goodsId);
    }

    /**
     * 查询二手商品列表
     * 
     * @param dcSecondhandGoods 二手商品
     * @return 二手商品
     */
    @Override
    public List<DcSecondhandGoods> selectDcSecondhandGoodsList(DcSecondhandGoods dcSecondhandGoods)
    {
        return dcSecondhandGoodsMapper.selectDcSecondhandGoodsList(dcSecondhandGoods);
    }

    /**
     * 新增二手商品
     * 
     * @param dcSecondhandGoods 二手商品
     * @return 结果
     */
    @Override
    public int insertDcSecondhandGoods(DcSecondhandGoods dcSecondhandGoods)
    {
        // 设置默认值
        if (dcSecondhandGoods.getGoodsStatus() == null) {
            dcSecondhandGoods.setGoodsStatus("0"); // 默认待售
        }
        if (dcSecondhandGoods.getViewCount() == null) {
            dcSecondhandGoods.setViewCount(0);
        }
        if (dcSecondhandGoods.getLikeCount() == null) {
            dcSecondhandGoods.setLikeCount(0);
        }
        if (dcSecondhandGoods.getCollectCount() == null) {
            dcSecondhandGoods.setCollectCount(0);
        }
        if (dcSecondhandGoods.getDelFlag() == null) {
            dcSecondhandGoods.setDelFlag("0");
        }
        return dcSecondhandGoodsMapper.insertDcSecondhandGoods(dcSecondhandGoods);
    }

    /**
     * 修改二手商品
     * 
     * @param dcSecondhandGoods 二手商品
     * @return 结果
     */
    @Override
    public int updateDcSecondhandGoods(DcSecondhandGoods dcSecondhandGoods)
    {
        return dcSecondhandGoodsMapper.updateDcSecondhandGoods(dcSecondhandGoods);
    }

    /**
     * 批量删除二手商品
     * 
     * @param goodsIds 需要删除的二手商品主键
     * @return 结果
     */
    @Override
    public int deleteDcSecondhandGoodsByGoodsIds(Long[] goodsIds)
    {
        return dcSecondhandGoodsMapper.deleteDcSecondhandGoodsByGoodsIds(goodsIds);
    }

    /**
     * 删除二手商品信息
     * 
     * @param goodsId 二手商品主键
     * @return 结果
     */
    @Override
    public int deleteDcSecondhandGoodsByGoodsId(Long goodsId)
    {
        return dcSecondhandGoodsMapper.deleteDcSecondhandGoodsByGoodsId(goodsId);
    }

    /**
     * 增加浏览次数
     * 
     * @param goodsId 商品ID
     * @return 结果
     */
    @Override
    public int incrementViewCount(Long goodsId)
    {
        return dcSecondhandGoodsMapper.incrementViewCount(goodsId);
    }

    /**
     * 查询热门二手商品（按浏览次数排序，状态为待售）
     * 
     * @param limit 限制数量
     * @return 二手商品集合
     */
    @Override
    public List<DcSecondhandGoods> selectHotSecondhandGoodsList(int limit)
    {
        return dcSecondhandGoodsMapper.selectHotSecondhandGoodsList(limit);
    }

    /**
     * 查询收藏的商品列表
     * 
     * @param userId 用户ID
     * @return 二手商品集合
     */
    @Override
    public List<DcSecondhandGoods> selectFavoriteGoodsList(Long userId)
    {
        return dcSecondhandGoodsMapper.selectFavoriteGoodsList(userId);
    }

    /**
     * 查询我的订单列表（暂时返回空列表，后续实现）
     * 
     * @param userId 用户ID
     * @return 订单列表（暂时返回空列表）
     */
    @Override
    public List<DcSecondhandGoods> selectMyOrderList(Long userId)
    {
        // TODO: 后续实现订单查询功能
        return new java.util.ArrayList<>();
    }

    /**
     * 查询收藏数量
     * 
     * @param userId 用户ID
     * @return 收藏数量
     */
    @Override
    public int countFavoriteGoods(Long userId)
    {
        return dcSecondhandGoodsMapper.countFavoriteGoods(userId);
    }

    /**
     * 查询我发布的商品数量
     * 
     * @param userId 用户ID
     * @return 商品数量
     */
    @Override
    public int countMyGoods(Long userId)
    {
        return dcSecondhandGoodsMapper.countMyGoods(userId);
    }
}

