package com.cj.waterresources.func.modular.overallSituationUnitMgr.controller;

import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.bean.res.OverallSituationUnitMgrTreeRes;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.entity.OverallSituationUnitMgr;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.service.OverallSituationUnitMgrService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * 全局单位管理(OverallSituationUnitMgr)表控制层
 *
 * @author makejava
 * @since 2024-02-21 11:11:45
 */
@Api(tags = "全局单位管理")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@RestController
@Validated
@RequestMapping("overallSituationUnitMgr")
public class OverallSituationUnitMgrController{
    /**
     * 服务对象
     */
    @Resource
    private OverallSituationUnitMgrService overallSituationUnitMgrService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("全局单位管理新增")
    @CommonLog(value = "全局单位管理新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody OverallSituationUnitMgr overallSituationUnitMgr) {
        return overallSituationUnitMgrService.add(overallSituationUnitMgr);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("全局单位管理删除")
    @CommonLog(value = "全局单位管理删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam("id") String id) {
        return overallSituationUnitMgrService.delete(id);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("全局单位管理修改")
    @CommonLog(value = "全局单位管理修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody OverallSituationUnitMgr overallSituationUnitMgr) {
        return overallSituationUnitMgrService.update(overallSituationUnitMgr);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("全局单位管理查询tree")
    @CommonLog(value = "全局单位管理查询tree")
    @PostMapping("/select")
    public RestResponse<List<OverallSituationUnitMgrTreeRes>> select() {
        return overallSituationUnitMgrService.selectTree();
    }
    @ApiOperationSupport(order = 5)
    @ApiOperation("全局单位管理更新监测点数据")
    @CommonLog(value = "全局单位管理更新监测点数据")
    @PostMapping("/updateMonitor")
    public RestResponse updateMonitor() {
        return overallSituationUnitMgrService.updateMonitor();
    }
}

