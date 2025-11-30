package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.DcForumCategory;

/**
 * 论坛分类Mapper接口
 * 
 * @author ruoyi
 */
public interface DcForumCategoryMapper 
{
    /**
     * 查询论坛分类列表
     * 
     * @param dcForumCategory 论坛分类
     * @return 论坛分类集合
     */
    public List<DcForumCategory> selectDcForumCategoryList(DcForumCategory dcForumCategory);
}

