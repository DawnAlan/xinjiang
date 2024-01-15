package com.cj.project.modular.fiducial.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.common.annotation.CommonLog;
import com.cj.common.pojo.CommonResult;
import com.cj.common.pojo.CommonValidList;
import com.cj.project.api.fiducial.entity.FiducialBase;
import com.cj.project.api.fiducial.entity.FiducialPara;
import com.cj.project.api.fiducial.param.*;
import com.cj.project.modular.fiducial.result.FiducialResult;
import com.cj.project.modular.fiducial.service.FiducialBaseService;
import com.cj.project.modular.fiducial.service.FiducialParaService;
import com.cj.project.modular.fiducial.service.FiducialService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * 测点考证控制器
 *
 * @author Lb
 * @date  2023/09/04 12:25
 */
@Api(tags = "测点考证控制器")
@ApiSupport(author = "CJ_TEAM", order = 1)
@RestController
@Validated
public class FiducialController {

    @Resource
    private FiducialBaseService fiducialBaseService;

    @Resource
    private FiducialParaService fiducialParaService;

    @Resource
    private FiducialService fiducialService;

    /**
     * 查询测点考证
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("查询测点考证")
    @GetMapping ("/project/fiducial/get")
    public CommonResult<FiducialResult> getOne(FiducialQueryParam fiducialQueryParam) {
        //Base //String projectCode, String instrumentType,String pointName
        FiducialBase fiducialBase = fiducialBaseService.getOne(fiducialQueryParam);
        //Para
        if (ObjectUtil.isNotEmpty(fiducialBase)) {
            List<FiducialPara> fiducialParas = fiducialParaService
                    .getList(FiducialIdParam.builder().id(fiducialBase.getId()).build());
            FiducialResult result = fiducialService.ToFiducial(fiducialBase, fiducialParas);
            return CommonResult.data(result);
        } else {
            return CommonResult.data(null);
        }
    }

    /**
     * 批量查询测点考证
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("批量查询测点考证")
    @PostMapping("/project/fiducial/getBatch")
    public CommonResult<List<FiducialResult>> getBatch(@RequestBody FiducialQueryParam fiducialQueryParam) {
        List<FiducialResult> fiducialResults = new ArrayList<>();
        //base.page
        List<FiducialBase> fiducialBases = fiducialBaseService.getBatch(fiducialQueryParam);
        //para
        for (FiducialBase fiducialBase : fiducialBases
        ) {
            List<FiducialPara> fiducialParas = fiducialParaService
                    .getList(FiducialIdParam.builder().id(fiducialBase.getId()).build());
            FiducialResult fiducialResult = fiducialService.ToFiducial(fiducialBase,fiducialParas);
            fiducialResults.add(fiducialResult);
        }

        return CommonResult.data(fiducialResults);
    }

    /**
     * 获取测点考证分页
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("获取测点考证分页")
    @GetMapping("/project/fiducial/page")
    public CommonResult<Page<FiducialResult>> page(FiducialPageParam fiducialPageParam) {
        //base.page
        Page<FiducialBase> fiducialBases = fiducialBaseService.page(fiducialPageParam);
        Page<FiducialResult> result = fiducialService.page(fiducialBases);
        //para
        List<FiducialResult> fiducialResults = new ArrayList<>();
        for (FiducialBase fiducialBase : fiducialBases.getRecords()
        ) {
            List<FiducialPara> fiducialParas = fiducialParaService
                    .getList(FiducialIdParam.builder().id(fiducialBase.getId()).build());
            FiducialResult fiducialResult = fiducialService.ToFiducial(fiducialBase,fiducialParas);
            fiducialResults.add(fiducialResult);
        }
        result.setRecords(fiducialResults);
        return CommonResult.data(result);
    }


    /**
     * 添加测点考证
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("添加测点考证")
    @CommonLog("添加测点考证")
    @PostMapping("/project/fiducial/add")
    public CommonResult<String> add(@RequestBody @Valid FiducialAddParam fiducialAddParam) {
        fiducialService.add(fiducialAddParam.getProjectCode(),fiducialAddParam.getInstrumentType(),fiducialAddParam.getDetail().stream().findFirst().get());
        return CommonResult.ok();
    }

    /**
     * 批量添加测点考证
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("批量添加测点考证")
    @CommonLog("批量添加测点考证")
    @PostMapping("/project/fiducial/addBatch")
    public CommonResult<String> addBatch(@RequestBody @Valid List<FiducialAddParam> fiducialAddParamList) {
        fiducialService.adds(fiducialAddParamList);
        // fiducialBaseService.add(fiducialBaseAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑测点考证表
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("编辑测点考证")
    @CommonLog("编辑测点考证")
    @PostMapping("/project/fiducial/edit")
    public CommonResult<String> edit(@RequestBody @Valid FiducialBaseEditParam fiducialBaseEditParam) {
        fiducialBaseService.edit(fiducialBaseEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除测点考证表
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("删除测点考证")
    @CommonLog("删除测点考证")
    @PostMapping("/project/fiducial/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   CommonValidList<String> fiducialIdList) {
        fiducialService.delete(fiducialIdList);
        return CommonResult.ok();
    }

}
