package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.DcSecondhandCategoryMapper;
import com.ruoyi.system.domain.DcSecondhandCategory;
import com.ruoyi.system.service.IDcSecondhandCategoryService;

/**
 * 二手商品分类Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class DcSecondhandCategoryServiceImpl implements IDcSecondhandCategoryService 
{
    @Autowired
    private DcSecondhandCategoryMapper dcSecondhandCategoryMapper;

    /**
     * 查询二手商品分类
     * 
     * @param categoryId 二手商品分类主键
     * @return 二手商品分类
     */
    @Override
    public DcSecondhandCategory selectDcSecondhandCategoryByCategoryId(Long categoryId)
    {
        return dcSecondhandCategoryMapper.selectDcSecondhandCategoryByCategoryId(categoryId);
    }

    /**
     * 查询二手商品分类列表
     * 
     * @param dcSecondhandCategory 二手商品分类
     * @return 二手商品分类
     */
    @Override
    public List<DcSecondhandCategory> selectDcSecondhandCategoryList(DcSecondhandCategory dcSecondhandCategory)
    {
        return dcSecondhandCategoryMapper.selectDcSecondhandCategoryList(dcSecondhandCategory);
    }

    /**
     * 新增二手商品分类
     * 
     * @param dcSecondhandCategory 二手商品分类
     * @return 结果
     */
    @Override
    public int insertDcSecondhandCategory(DcSecondhandCategory dcSecondhandCategory)
    {
        if (dcSecondhandCategory.getStatus() == null) {
            dcSecondhandCategory.setStatus("0");
        }
        return dcSecondhandCategoryMapper.insertDcSecondhandCategory(dcSecondhandCategory);
    }

    /**
     * 修改二手商品分类
     * 
     * @param dcSecondhandCategory 二手商品分类
     * @return 结果
     */
    @Override
    public int updateDcSecondhandCategory(DcSecondhandCategory dcSecondhandCategory)
    {
        return dcSecondhandCategoryMapper.updateDcSecondhandCategory(dcSecondhandCategory);
    }

    /**
     * 批量删除二手商品分类
     * 
     * @param categoryIds 需要删除的二手商品分类主键
     * @return 结果
     */
    @Override
    public int deleteDcSecondhandCategoryByCategoryIds(Long[] categoryIds)
    {
        return dcSecondhandCategoryMapper.deleteDcSecondhandCategoryByCategoryIds(categoryIds);
    }

    /**
     * 删除二手商品分类信息
     * 
     * @param categoryId 二手商品分类主键
     * @return 结果
     */
    @Override
    public int deleteDcSecondhandCategoryByCategoryId(Long categoryId)
    {
        return dcSecondhandCategoryMapper.deleteDcSecondhandCategoryByCategoryId(categoryId);
    }
}

