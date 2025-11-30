package com.ruoyi.web.controller.dreamcampus;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.DcForumCategory;
import com.ruoyi.system.service.IDcForumCategoryService;

/**
 * 论坛分类Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/dreamcampus/forum/category")
public class DcForumCategoryController extends BaseController
{
    @Autowired
    private IDcForumCategoryService dcForumCategoryService;

    /**
     * 查询论坛分类列表
     */
    @GetMapping("/list")
    public AjaxResult list(DcForumCategory dcForumCategory)
    {
        List<DcForumCategory> list = dcForumCategoryService.selectDcForumCategoryList(dcForumCategory);
        return success(list);
    }
}

