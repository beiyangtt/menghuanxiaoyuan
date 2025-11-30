package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.DcForumCategory;

/**
 * 论坛分类Service接口
 * 
 * @author ruoyi
 */
public interface IDcForumCategoryService 
{
    /**
     * 查询论坛分类列表
     * 
     * @param dcForumCategory 论坛分类
     * @return 论坛分类集合
     */
    public List<DcForumCategory> selectDcForumCategoryList(DcForumCategory dcForumCategory);
}

