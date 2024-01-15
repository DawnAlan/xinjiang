package com.cj.data.modular.dataResult.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.data.modular.dataResult.entity.DataValue;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.cj.common.annotation.CommonLog;
import com.cj.common.pojo.CommonResult;
import com.cj.common.pojo.CommonValidList;
import com.cj.data.modular.dataResult.service.DataValueService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 数据成果表控制器|DataValue
 *
 * @author Lb
 * @date  2023/10/23 16:51
 */
@Api(tags = "数据成果表控制器|DataValue")
@ApiSupport(author = "LB", order = 1)
@RestController
@Validated
public class DataValueController {

    @Resource
    private DataValueService dataValueService;

    /**
     * 获取数据成果表分页
     *
     * @author Lb
     * @date  2023/10/23 16:51
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取数据成果表分页")
    @PostMapping("/data/dataValue/page")
    public CommonResult<Page<DataValue>> page(@RequestBody List<String> dataIdParamList, Integer current, Integer size) {
        return CommonResult.data(dataValueService.page(dataIdParamList,current,size));
    }

    /**
     * 获取数据成果表详情
     *
     * @author Lb
     * @date  2023/10/23 16:51
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("获取数据成果表详情")
    @PostMapping("/data/dataValue/detail")
    public CommonResult<List<DataValue>> detail(CommonValidList<String> dataIdParamList) {
        return CommonResult.data(dataValueService.getList(dataIdParamList));
    }

    /**
     * 删除数据成果表
     *
     * @author Lb
     * @date  2023/10/23 16:51
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("删除数据成果表")
    @CommonLog("删除数据成果表")
    @PostMapping("/data/dataValue/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   CommonValidList<String> dataIdParamList) {
        dataValueService.delete(dataIdParamList);
        return CommonResult.ok();
    }

}
