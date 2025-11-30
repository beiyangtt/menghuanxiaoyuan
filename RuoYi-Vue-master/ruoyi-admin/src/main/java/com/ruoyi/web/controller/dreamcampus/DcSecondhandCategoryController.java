package com.ruoyi.web.controller.dreamcampus;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.DcSecondhandCategory;
import com.ruoyi.system.service.IDcSecondhandCategoryService;

/**
 * 二手商品分类Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/dreamcampus/secondhand/category")
public class DcSecondhandCategoryController extends BaseController
{
    @Autowired
    private IDcSecondhandCategoryService dcSecondhandCategoryService;

    /**
     * 查询二手商品分类列表（移动端接口，返回启用状态的分类）
     */
    @GetMapping("/list")
    public AjaxResult list()
    {
        DcSecondhandCategory category = new DcSecondhandCategory();
        category.setStatus("0"); // 只查询正常状态的分类
        List<DcSecondhandCategory> list = dcSecondhandCategoryService.selectDcSecondhandCategoryList(category);
        return success(list);
    }
}

