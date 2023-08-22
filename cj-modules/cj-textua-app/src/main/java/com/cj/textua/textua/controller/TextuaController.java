package com.cj.textua.textua.controller;

/**
 * @创建人 yancheng
 * @创建时间 2023-08-22 08:50
 * @描述
 */

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cj.common.pojo.CommonResult;
import com.cj.textua.textua.entity.FiducialBase;
import com.cj.textua.textua.entity.FiducialParam;
import com.cj.textua.textua.param.FiducialBaseAddParam;
import com.cj.textua.textua.param.TextuaBaseEditParam;
import com.cj.textua.textua.param.TextuaBaseParam;
import com.cj.textua.textua.param.TextuaExtraParam;
import com.cj.textua.textua.service.FiducialBaseService;
import com.cj.textua.textua.service.FiducialParamService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考证参数信息表(跟字段配置表关联)控制器
 *
 * @author yancheng
 * @date  2023/08/21 20:50
 */
@Api(tags = "考证管理")
@ApiSupport(author = "CJ_TEAM", order = 1)
@RestController
@Validated
public class TextuaController {

    @Resource
    private FiducialBaseService fiducialBaseService;

    @Resource
    private FiducialParamService fiducialParamService;


    /**
     * 添加考证
     *
     * @author yancheng
     * @date  2023/08/21 20:32
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("添加考证")
    @SaCheckPermission("/textua/add")
    @PostMapping("/textua/add")
    @SaIgnore
    public CommonResult<String> add(@RequestBody @Valid TextuaBaseParam textuaBaseParam) {

        FiducialBase fiducialBase = BeanUtil.toBean(textuaBaseParam, FiducialBase.class);
        fiducialBaseService.save(fiducialBase);
        if(CollectionUtils.isNotEmpty(textuaBaseParam.getExtraParam())){
            List<FiducialParam> fiducialParams = new ArrayList<>();
            List<TextuaExtraParam> extraParam = textuaBaseParam.getExtraParam();
            for (TextuaExtraParam textuaExtraParam : extraParam) {
                FiducialParam fiducialParam = new FiducialParam();
                fiducialParam.setPointid(fiducialBase.getId());
                fiducialParam.setFieldname(textuaExtraParam.getFieldname());
                fiducialParam.setFieldvalue(textuaExtraParam.getFieldvalue());
                fiducialParams.add(fiducialParam);
            }
            fiducialParamService.saveBatch(fiducialParams);
        }

        return CommonResult.ok();
    }


    /**
     * 添加考证
     *
     * @author yancheng
     * @date  2023/08/21 20:32
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("map添加考证")
    @SaCheckPermission("/textua/addMap")
    @PostMapping("/textua/addMap")
    @SaIgnore
    public CommonResult<String> addMap(@RequestBody Map<String, Object> map) {



        System.out.println("xxxx====="+map);

        return CommonResult.ok();

    }

    /**
     * 删除考证
     *
     * @author yancheng
     * @date  2023/08/21 20:32
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("删除考证")
    @SaCheckPermission("/textua/delete")
    @PostMapping("/textua/delete")
    @SaIgnore
    public CommonResult<String> delete(@RequestParam("id") String id) {


        boolean remove = fiducialParamService.remove(Wrappers.<FiducialParam>query().lambda().eq(FiducialParam::getPointid, id));

        if(remove){
            fiducialBaseService.removeById(id);
        }

        return CommonResult.ok();
    }


    /**
     * 修改考证
     *
     * @author yancheng
     * @date  2023/08/21 20:32
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("修改考证")
    @SaCheckPermission("/textua/edit")
    @PostMapping("/textua/edit")
    @SaIgnore
    public CommonResult<String> edit(@RequestBody @Valid TextuaBaseEditParam textuaBaseEditParam) {

        boolean remove = fiducialParamService.remove(Wrappers.<FiducialParam>query().lambda().eq(FiducialParam::getPointid, textuaBaseEditParam.getId()));

        if(remove){
            fiducialBaseService.removeById(textuaBaseEditParam.getId());
        }


        FiducialBase fiducialBase = BeanUtil.toBean(textuaBaseEditParam, FiducialBase.class);
        fiducialBaseService.save(fiducialBase);

        if(CollectionUtils.isNotEmpty(textuaBaseEditParam.getExtraParam())){
            List<FiducialParam> fiducialParams = new ArrayList<>();
            List<TextuaExtraParam> extraParam = textuaBaseEditParam.getExtraParam();
            for (TextuaExtraParam textuaExtraParam : extraParam) {
                FiducialParam fiducialParam = new FiducialParam();
                fiducialParam.setPointid(fiducialBase.getId());
                fiducialParam.setFieldname(textuaExtraParam.getFieldname());
                fiducialParam.setFieldname(textuaExtraParam.getFieldvalue());
                fiducialParams.add(fiducialParam);
            }
            fiducialParamService.saveBatch(fiducialParams);
        }

        return CommonResult.ok();
    }

    /**
     * 查看详情
     *
     * @author yancheng
     * @date  2023/08/21 20:32
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("查看详情")
    @SaCheckPermission("/textua/detail")
    @PostMapping("/textua/detail")
    @SaIgnore
    public CommonResult<Map<String, Object>> detail(@RequestParam("id") String id) {
        Map<String, Object> map = new HashMap<>();

        FiducialBase fiducialBase = fiducialBaseService.getById(id);

        map.put("pointname",fiducialBase.getPointname());

        map.put("instrumentname",fiducialBase.getInstrumentname());


        List<FiducialParam> list = fiducialParamService.list(Wrappers.<FiducialParam>query().lambda().eq(FiducialParam::getPointid, fiducialBase.getId()));
        for (FiducialParam fiducialParam : list) {
            map.put(fiducialParam.getFieldname(),fiducialParam.getFieldvalue());
        }


        return CommonResult.data(map);
    }


    /**
     * 模糊查询
     *
     * @author yancheng
     * @date  2023/08/21 20:32
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("模糊查询")
    @SaCheckPermission("/textua/like")
    @PostMapping("/textua/like")
    @SaIgnore
    public CommonResult<List<FiducialBase>> like(@RequestParam("PointName") String PointName) {


        List<FiducialBase> list = fiducialBaseService.list(Wrappers.<FiducialBase>query().lambda().like(StringUtils.isNotBlank(PointName), FiducialBase::getPointname, PointName));

        return CommonResult.data(list);
    }
}
