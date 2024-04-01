package com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.entity.WaterManagementUrbanIndustry;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.service.WaterManagementUrbanIndustryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 税费管理-城市工业(WaterManagementUrbanIndustry)表控制层
 *
 * @author makejava
 * @since 2024-04-01 10:52:02
 */
@RestController
@RequestMapping("waterManagementUrbanIndustry")
public class WaterManagementUrbanIndustryController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private WaterManagementUrbanIndustryService waterManagementUrbanIndustryService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param waterManagementUrbanIndustry 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<WaterManagementUrbanIndustry> page, WaterManagementUrbanIndustry waterManagementUrbanIndustry) {
        return success(this.waterManagementUrbanIndustryService.page(page, new QueryWrapper<>(waterManagementUrbanIndustry)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.waterManagementUrbanIndustryService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param waterManagementUrbanIndustry 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody WaterManagementUrbanIndustry waterManagementUrbanIndustry) {
        return success(this.waterManagementUrbanIndustryService.save(waterManagementUrbanIndustry));
    }

    /**
     * 修改数据
     *
     * @param waterManagementUrbanIndustry 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody WaterManagementUrbanIndustry waterManagementUrbanIndustry) {
        return success(this.waterManagementUrbanIndustryService.updateById(waterManagementUrbanIndustry));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.waterManagementUrbanIndustryService.removeByIds(idList));
    }
}

