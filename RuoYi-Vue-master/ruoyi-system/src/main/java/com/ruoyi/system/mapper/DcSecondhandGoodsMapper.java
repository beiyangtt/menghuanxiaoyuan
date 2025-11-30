package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.DcSecondhandGoods;

/**
 * 二手商品Mapper接口
 * 
 * @author ruoyi
 */
public interface DcSecondhandGoodsMapper 
{
    /**
     * 查询二手商品
     * 
     * @param goodsId 二手商品主键
     * @return 二手商品
     */
    public DcSecondhandGoods selectDcSecondhandGoodsByGoodsId(Long goodsId);

    /**
     * 查询二手商品列表
     * 
     * @param dcSecondhandGoods 二手商品
     * @return 二手商品集合
     */
    public List<DcSecondhandGoods> selectDcSecondhandGoodsList(DcSecondhandGoods dcSecondhandGoods);

    /**
     * 新增二手商品
     * 
     * @param dcSecondhandGoods 二手商品
     * @return 结果
     */
    public int insertDcSecondhandGoods(DcSecondhandGoods dcSecondhandGoods);

    /**
     * 修改二手商品
     * 
     * @param dcSecondhandGoods 二手商品
     * @return 结果
     */
    public int updateDcSecondhandGoods(DcSecondhandGoods dcSecondhandGoods);

    /**
     * 删除二手商品
     * 
     * @param goodsId 二手商品主键
     * @return 结果
     */
    public int deleteDcSecondhandGoodsByGoodsId(Long goodsId);

    /**
     * 批量删除二手商品
     * 
     * @param goodsIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDcSecondhandGoodsByGoodsIds(Long[] goodsIds);

    /**
     * 增加浏览次数
     * 
     * @param goodsId 商品ID
     * @return 结果
     */
    public int incrementViewCount(Long goodsId);

    /**
     * 查询热门二手商品（按浏览次数排序，状态为待售）
     * 
     * @param limit 限制数量
     * @return 二手商品集合
     */
    public List<DcSecondhandGoods> selectHotSecondhandGoodsList(int limit);

    /**
     * 查询收藏的商品列表
     * 
     * @param userId 用户ID
     * @return 二手商品集合
     */
    public List<DcSecondhandGoods> selectFavoriteGoodsList(Long userId);

    /**
     * 查询收藏数量
     * 
     * @param userId 用户ID
     * @return 收藏数量
     */
    public int countFavoriteGoods(Long userId);

    /**
     * 查询我发布的商品数量
     * 
     * @param userId 用户ID
     * @return 商品数量
     */
    public int countMyGoods(Long userId);
}

