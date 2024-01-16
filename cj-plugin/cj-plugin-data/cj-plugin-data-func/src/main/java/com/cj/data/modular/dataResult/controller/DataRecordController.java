package com.cj.data.modular.dataResult.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.data.modular.dataResult.param.*;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.cj.common.annotation.CommonLog;
import com.cj.common.pojo.CommonResult;
import com.cj.common.pojo.CommonValidList;
import com.cj.data.modular.dataResult.entity.DataRecord;
import com.cj.data.modular.dataResult.service.DataRecordService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据记录表控制器
 *
 * @author Lb
 * @date  2023/10/12 17:01
 */
@Api(tags = "数据记录表控制器|DataRecord")
@RestController
@ApiSupport(author = "LB", order = 1)
@Validated
public class DataRecordController {

    @Resource
    private DataRecordService dataRecordService;

    /**
     * 获取数据记录分页
     *
     * @author Lb
     * @date  2023/10/12 17:01
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取数据记录表分页")
    @GetMapping("/data/dataRecord/page")
    public CommonResult<Page<DataRecord>> page(DataRecordPageParam dataRecordPageParam) {
        return CommonResult.data(dataRecordService.page(dataRecordPageParam));
    }


    /**
     * 获取所有数据记录
     *
     * @author Lb
     * @date  2023/10/12 17:01
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取所有数据记录")
    @PostMapping("/data/dataRecord/list")
    public CommonResult<List<DataRecord>> getList(@RequestBody DataRecordQueryParam dataRecordQueryParam) {
        long start = System.currentTimeMillis();
        System.out.println("START:" + ": cost " + (System.currentTimeMillis() - start) + "ms");
        List<DataRecord> result = dataRecordService.getList(dataRecordQueryParam);
        System.out.println("SIZE:" + result.size());
        System.out.println("getList:" + ": cost " + (System.currentTimeMillis() - start) + "ms");
        return CommonResult.data(result);
    }

    /**
     * 添加数据记录
     *
     * @author Lb
     * @date  2023/10/12 17:01
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加数据记录")
    @CommonLog("添加数据记录")
    @PostMapping("/data/dataRecord/add")
    public CommonResult<String> add(@RequestBody @Valid DataRecordAddParam dataRecordAddParam) {
        dataRecordService.add(dataRecordAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑数据记录
     *
     * @author Lb
     * @date  2023/10/12 17:01
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑数据记录")
    @CommonLog("编辑数据记录")
    @PostMapping("/data/dataRecord/edit")
    public CommonResult<String> edit(@RequestBody @Valid DataRecordEditParam dataRecordEditParam) {
        dataRecordService.edit(dataRecordEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除数据记录
     *
     * @author Lb
     * @date  2023/10/12 17:01
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除数据记录")
    @CommonLog("删除数据记录")
    @PostMapping("/data/dataRecord/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   CommonValidList<DataRecordIdParam> dataRecordIdParamList) {
        dataRecordService.delete(dataRecordIdParamList);
        return CommonResult.ok();
    }

}
