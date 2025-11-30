package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.DcForumCategoryMapper;
import com.ruoyi.system.domain.DcForumCategory;
import com.ruoyi.system.service.IDcForumCategoryService;

/**
 * 论坛分类Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class DcForumCategoryServiceImpl implements IDcForumCategoryService 
{
    @Autowired
    private DcForumCategoryMapper dcForumCategoryMapper;

    /**
     * 查询论坛分类列表
     * 
     * @param dcForumCategory 论坛分类
     * @return 论坛分类
     */
    @Override
    public List<DcForumCategory> selectDcForumCategoryList(DcForumCategory dcForumCategory)
    {
        return dcForumCategoryMapper.selectDcForumCategoryList(dcForumCategory);
    }
}

