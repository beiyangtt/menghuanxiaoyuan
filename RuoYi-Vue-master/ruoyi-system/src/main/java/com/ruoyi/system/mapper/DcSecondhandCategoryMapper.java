package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.DcSecondhandCategory;

/**
 * 二手商品分类Mapper接口
 * 
 * @author ruoyi
 */
public interface DcSecondhandCategoryMapper 
{
    /**
     * 查询二手商品分类
     * 
     * @param categoryId 二手商品分类主键
     * @return 二手商品分类
     */
    public DcSecondhandCategory selectDcSecondhandCategoryByCategoryId(Long categoryId);

    /**
     * 查询二手商品分类列表
     * 
     * @param dcSecondhandCategory 二手商品分类
     * @return 二手商品分类集合
     */
    public List<DcSecondhandCategory> selectDcSecondhandCategoryList(DcSecondhandCategory dcSecondhandCategory);

    /**
     * 新增二手商品分类
     * 
     * @param dcSecondhandCategory 二手商品分类
     * @return 结果
     */
    public int insertDcSecondhandCategory(DcSecondhandCategory dcSecondhandCategory);

    /**
     * 修改二手商品分类
     * 
     * @param dcSecondhandCategory 二手商品分类
     * @return 结果
     */
    public int updateDcSecondhandCategory(DcSecondhandCategory dcSecondhandCategory);

    /**
     * 删除二手商品分类
     * 
     * @param categoryId 二手商品分类主键
     * @return 结果
     */
    public int deleteDcSecondhandCategoryByCategoryId(Long categoryId);

    /**
     * 批量删除二手商品分类
     * 
     * @param categoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDcSecondhandCategoryByCategoryIds(Long[] categoryIds);
}

