package com.ruoyi.web.controller.dreamcampus;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.DcSecondhandGoods;
import com.ruoyi.system.service.IDcSecondhandGoodsService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 二手商品Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/dreamcampus/secondhand")
public class DcSecondhandGoodsController extends BaseController
{
    @Autowired
    private IDcSecondhandGoodsService dcSecondhandGoodsService;

    /**
     * 查询二手商品列表
     */
    @GetMapping("/list")
    public TableDataInfo list(DcSecondhandGoods dcSecondhandGoods)
    {
        startPage();
        List<DcSecondhandGoods> list = dcSecondhandGoodsService.selectDcSecondhandGoodsList(dcSecondhandGoods);
        return getDataTable(list);
    }

    /**
     * 获取二手商品详细信息
     */
    @GetMapping(value = "/{goodsId}")
    public AjaxResult getInfo(@PathVariable("goodsId") Long goodsId)
    {
        // 增加浏览次数
        dcSecondhandGoodsService.incrementViewCount(goodsId);
        return success(dcSecondhandGoodsService.selectDcSecondhandGoodsByGoodsId(goodsId));
    }

    /**
     * 新增二手商品
     */
    @Log(title = "二手商品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DcSecondhandGoods dcSecondhandGoods)
    {
        // 设置发布用户ID为当前登录用户
        dcSecondhandGoods.setUserId(SecurityUtils.getUserId());
        dcSecondhandGoods.setCreateBy(SecurityUtils.getUsername());
        return toAjax(dcSecondhandGoodsService.insertDcSecondhandGoods(dcSecondhandGoods));
    }

    /**
     * 修改二手商品
     */
    @Log(title = "二手商品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DcSecondhandGoods dcSecondhandGoods)
    {
        // 验证是否为商品发布者
        DcSecondhandGoods goods = dcSecondhandGoodsService.selectDcSecondhandGoodsByGoodsId(dcSecondhandGoods.getGoodsId());
        if (goods == null || !goods.getUserId().equals(SecurityUtils.getUserId())) {
            return error("只能修改自己发布的商品");
        }
        dcSecondhandGoods.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(dcSecondhandGoodsService.updateDcSecondhandGoods(dcSecondhandGoods));
    }

    /**
     * 删除二手商品
     */
    @Log(title = "二手商品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{goodsIds}")
    public AjaxResult remove(@PathVariable Long[] goodsIds)
    {
        Long currentUserId = SecurityUtils.getUserId();
        boolean isAdmin = SecurityUtils.isAdmin(currentUserId);
        
        // 管理员可以删除任何商品，普通用户只能删除自己发布的商品
        if (!isAdmin) {
            for (Long goodsId : goodsIds) {
                DcSecondhandGoods goods = dcSecondhandGoodsService.selectDcSecondhandGoodsByGoodsId(goodsId);
                if (goods == null || !goods.getUserId().equals(currentUserId)) {
                    return error("只能删除自己发布的商品");
                }
            }
        }
        return toAjax(dcSecondhandGoodsService.deleteDcSecondhandGoodsByGoodsIds(goodsIds));
    }

    /**
     * 完成交易（将商品状态改为已售）
     */
    @Log(title = "二手商品", businessType = BusinessType.UPDATE)
    @PutMapping("/complete/{goodsId}")
    public AjaxResult completeTrade(@PathVariable Long goodsId)
    {
        // 验证是否为商品发布者
        DcSecondhandGoods goods = dcSecondhandGoodsService.selectDcSecondhandGoodsByGoodsId(goodsId);
        if (goods == null || !goods.getUserId().equals(SecurityUtils.getUserId())) {
            return error("只能操作自己发布的商品");
        }
        // 更新商品状态为已售
        goods.setGoodsStatus("1");
        goods.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(dcSecondhandGoodsService.updateDcSecondhandGoods(goods));
    }

    /**
     * 查询热门二手商品（按浏览次数排序，状态为待售）
     */
    @GetMapping("/hot")
    public AjaxResult getHotGoods(@RequestParam(defaultValue = "10") int limit)
    {
        List<DcSecondhandGoods> list = dcSecondhandGoodsService.selectHotSecondhandGoodsList(limit);
        return success(list);
    }

    /**
     * 查询我发布的商品列表
     */
    @GetMapping("/my/list")
    public TableDataInfo getMyGoodsList()
    {
        startPage();
        DcSecondhandGoods query = new DcSecondhandGoods();
        query.setUserId(SecurityUtils.getUserId());
        List<DcSecondhandGoods> list = dcSecondhandGoodsService.selectDcSecondhandGoodsList(query);
        return getDataTable(list);
    }

    /**
     * 查询收藏的商品列表
     */
    @GetMapping("/favorite/list")
    public AjaxResult getFavoriteList()
    {
        Long userId = SecurityUtils.getUserId();
        List<DcSecondhandGoods> list = dcSecondhandGoodsService.selectFavoriteGoodsList(userId);
        return success(list);
    }

    /**
     * 查询我的订单列表（暂时返回空列表）
     */
    @GetMapping("/order/my/list")
    public TableDataInfo getMyOrderList()
    {
        startPage();
        Long userId = SecurityUtils.getUserId();
        // TODO: 后续实现订单查询功能，暂时返回空列表
        List<DcSecondhandGoods> list = dcSecondhandGoodsService.selectMyOrderList(userId);
        return getDataTable(list);
    }

    /**
     * 查询收藏数量
     */
    @GetMapping("/favorite/count")
    public AjaxResult getFavoriteCount()
    {
        Long userId = SecurityUtils.getUserId();
        int count = dcSecondhandGoodsService.countFavoriteGoods(userId);
        return success(count);
    }

    /**
     * 查询我发布的商品数量
     */
    @GetMapping("/my/count")
    public AjaxResult getMyGoodsCount()
    {
        Long userId = SecurityUtils.getUserId();
        int count = dcSecondhandGoodsService.countMyGoods(userId);
        return success(count);
    }
}

