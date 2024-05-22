package com.cj.waterresources.func.modular.waterPrice.waterDistributionRatio.controller;

import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.trendsTable.bean.req.QueryTrendsTableParamReq;
import com.cj.waterresources.func.modular.trendsTable.bean.req.TrendsTableParamAddReq;
import com.cj.waterresources.func.modular.trendsTable.bean.req.TrendsTableParamUpdateReq;
import com.cj.waterresources.func.modular.trendsTable.bean.res.WaterDailyParamSelectRes;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.entity.TotalIdToStation;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.service.TotalIdToStationService;
import com.cj.waterresources.func.modular.waterPrice.waterDistributionRatio.entity.WaterDistributionRatio;
import com.cj.waterresources.func.modular.waterPrice.waterDistributionRatio.service.WaterDistributionRatioService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.WaterFeeStatisticsDetailsSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 配水比例(WaterDistributionRatio)表控制层
 *
 * @author makejava
 * @since 2023-12-15 18:08:30
 */
@Api(tags = "水费缴纳管理-配水比例")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("waterDistributionRatio")
public class WaterDistributionRatioController{

    @Autowired
    private WaterDistributionRatioService waterDistributionRatioService;

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    @Autowired
    private TotalIdToStationService totalIdToStationService;

    private Object lock = new Object();


    @ApiOperationSupport(order = 1)
    @ApiOperation("水费缴纳管理-配水比例新增")
    @CommonLog(value = "水费缴纳管理-配水比例新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody List<WaterDistributionRatio> waterDistributionRatios) {
        WaterDistributionRatio waterDistributionRatio = waterDistributionRatios.get(0);
        synchronized (lock){
            WaterDistributionRatio ratio = waterDistributionRatioService.lambdaQuery().eq(WaterDistributionRatio::getStation, waterDistributionRatio.getStation()).
                    eq(WaterDistributionRatio::getYear, waterDistributionRatio.getYear()).
                    eq(WaterDistributionRatio::getMonth, waterDistributionRatio.getMonth()).
                    eq(WaterDistributionRatio::getTenDays, waterDistributionRatio.getTenDays()).one();
            if(null ==ratio){
                waterDistributionRatios.forEach(t->{t.setId(UUIDUtils.getUUID());});
                List<TotalIdToStation> list = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 2).eq(TotalIdToStation::getStation, waterDistributionRatios.get(0).getStation()).list();
                //计算行合计
                if(null != list && list.size()>0){
                    Double total = 0.0;
                    List<String> collect = list.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                    for(String id:collect){
                        Double value = 0.0;
                        TrendsTableParam tableParam = trendsTableParamService.getById(id);
                        if(!tableParam.getPId().equals("0")){
                            List<TrendsTableParam> noTotalList = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, tableParam.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                            for(TrendsTableParam param:noTotalList){
                                for(WaterDistributionRatio t:waterDistributionRatios){
                                    if(t.getTableHeadId().equals(param.getId())){
                                        value+=t.getV()==null?0.0:t.getV();
                                    }
                                    List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
                                    if(null != listed && listed.size()>0){
                                        for (TrendsTableParam param1:listed){
                                            if(t.getTableHeadId().equals(param1.getId())){
                                                value+=t.getV()==null?0.0:t.getV();
                                            }
                                        }
                                    }
                                }
                            }
                            for(WaterDistributionRatio t:waterDistributionRatios){
                                if(t.getTableHeadId().equals(id)){
                                    t.setV(value);
                                }
                            }

                        }
                    }
                    for(WaterDistributionRatio t:waterDistributionRatios){
                        if(!list.stream().map(TotalIdToStation::getTotalId).collect(Collectors.toList()).contains(t.getTableHeadId())){
                            total+=t.getV()==null?0.0:t.getV();
                        }
                    }
                    TrendsTableParam one = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, "0").eq(TrendsTableParam::getUseType,2).in(TrendsTableParam::getId, collect).one();
                    if(null != one){
                        for(WaterDistributionRatio t:waterDistributionRatios){
                            if(t.getTableHeadId().equals(one.getId())){
                                t.setV(total);
                            }
                        }
                    }
                }
                boolean b = waterDistributionRatioService.saveBatch(waterDistributionRatios);
                if(b) {
                    return RestResponse.ok("新增成功");
                }else {
                    return RestResponse.no("新增失败");
                }
            }
        }
        throw new RuntimeException("正在新建请勿重复插入！");
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("水费缴纳管理-配水比例修改")
    @CommonLog(value = "水费缴纳管理-配水比例修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody List<WaterDistributionRatio> waterDistributionRatios) {
        List<TotalIdToStation> list = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 2).eq(TotalIdToStation::getStation, waterDistributionRatios.get(0).getStation()).list();
        //计算行合计
        if(null != list && list.size()>0){
            Double total = 0.0;
            List<String> collect = list.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
            for(String id:collect){
                Double value = 0.0;
                TrendsTableParam tableParam = trendsTableParamService.getById(id);
                if(!tableParam.getPId().equals("0")){
                    List<TrendsTableParam> noTotalList = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, tableParam.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                    for(TrendsTableParam param:noTotalList){
                        for(WaterDistributionRatio t:waterDistributionRatios){
                            if(t.getTableHeadId().equals(param.getId())){
                                value+=t.getV()==null?0.0:t.getV();
                            }
                            List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
                            if(null != listed && listed.size()>0){
                                for (TrendsTableParam param1:listed){
                                    if(t.getTableHeadId().equals(param1.getId())){
                                        value+=t.getV()==null?0.0:t.getV();
                                    }
                                }
                            }
                        }
                    }
                    for(WaterDistributionRatio t:waterDistributionRatios){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }

                }
            }
            for(WaterDistributionRatio t:waterDistributionRatios){
                if(!list.stream().map(TotalIdToStation::getTotalId).collect(Collectors.toList()).contains(t.getTableHeadId())){
                    total+=t.getV()==null?0.0:t.getV();
                }
            }
            TrendsTableParam one = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, "0").eq(TrendsTableParam::getUseType,2).in(TrendsTableParam::getId, collect).one();
            if(null != one){
                for(WaterDistributionRatio t:waterDistributionRatios){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        boolean b = waterDistributionRatioService.updateBatchById(waterDistributionRatios);
        if(b) {
            return RestResponse.ok("修改除成功");
        }else {
            return RestResponse.no("修改失败");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("水费缴纳管理-配水比例查询列表")
    @CommonLog(value = "水费缴纳管理-配水比例查询列表")
    @PostMapping("/select")
    public RestResponse<List<WaterDistributionRatio>> select(@RequestBody WaterFeeStatisticsDetailsSelectListReq req) {
        List<WaterDistributionRatio> list = waterDistributionRatioService.lambdaQuery().eq(StringUtils.isNotEmpty(req.getStation()), WaterDistributionRatio::getStation, req.getStation()).
                eq(req.getYear() != null, WaterDistributionRatio::getYear, req.getYear()).
                eq(req.getMonth() != null, WaterDistributionRatio::getMonth, req.getMonth()).
                eq(StringUtils.isNotEmpty(req.getTenDays()), WaterDistributionRatio::getTenDays, req.getTenDays()).list();
        if(null != list && list.size()>0) {
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("水费缴纳管理-配水比例新增（渠首）")
    @CommonLog(value = "水费缴纳管理-配水比例新增（渠首）")
    @PostMapping("/addForQS")
    public RestResponse addForQS(@RequestBody List<WaterDistributionRatio> waterDistributionRatios) {
        WaterDistributionRatio waterDistributionRatio = waterDistributionRatios.get(0);
        synchronized (lock){
            WaterDistributionRatio ratio = waterDistributionRatioService.lambdaQuery().eq(WaterDistributionRatio::getStation, waterDistributionRatio.getStation()).
                    eq(WaterDistributionRatio::getYear, waterDistributionRatio.getYear()).
                    eq(WaterDistributionRatio::getMonth, waterDistributionRatio.getMonth()).
                    eq(WaterDistributionRatio::getTenDays, waterDistributionRatio.getTenDays()).one();
            if(null ==ratio){
                waterDistributionRatios.forEach(t->{t.setId(UUIDUtils.getUUID());});
                List<TotalIdToStation> list = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, waterDistributionRatios.get(0).getStation()).list();
                //计算行合计
                if(null != list && list.size()>0){
                    Double total = 0.0;
                    List<String> collect = list.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                    for(String id:collect){
                        Double value = 0.0;
                        TrendsTableParam tableParam = trendsTableParamService.getById(id);
                        if(!tableParam.getPId().equals("0")){
                            List<TrendsTableParam> noTotalList = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, tableParam.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                            for(TrendsTableParam param:noTotalList){
                                for(WaterDistributionRatio t:waterDistributionRatios){
                                    if(t.getTableHeadId().equals(param.getId())){
                                        value+=t.getV()==null?0.0:t.getV();
                                    }
                                    List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
                                    if(null != listed && listed.size()>0){
                                        for (TrendsTableParam param1:listed){
                                            if(t.getTableHeadId().equals(param1.getId())){
                                                value+=t.getV()==null?0.0:t.getV();
                                            }
                                        }
                                    }
                                }
                            }
                            for(WaterDistributionRatio t:waterDistributionRatios){
                                if(t.getTableHeadId().equals(id)){
                                    t.setV(value);
                                }
                            }

                        }
                    }
                    for(WaterDistributionRatio t:waterDistributionRatios){
                        if(!list.stream().map(TotalIdToStation::getTotalId).collect(Collectors.toList()).contains(t.getTableHeadId())){
                            total+=t.getV()==null?0.0:t.getV();
                        }
                    }
                    TrendsTableParam one = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, "0").eq(TrendsTableParam::getUseType,2).in(TrendsTableParam::getId, collect).one();
                    if(null != one){
                        for(WaterDistributionRatio t:waterDistributionRatios){
                            if(t.getTableHeadId().equals(one.getId())){
                                t.setV(total);
                            }
                        }
                    }
                }
                boolean b = waterDistributionRatioService.saveBatch(waterDistributionRatios);
                if(b) {
                    return RestResponse.ok("新增成功");
                }else {
                    return RestResponse.no("新增失败");
                }
            }
        }
        throw new RuntimeException("正在新建请勿重复插入！");
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("水费缴纳管理-配水比例修改（渠首）")
    @CommonLog(value = "水费缴纳管理-配水比例修改（渠首）")
    @PostMapping("/updateForQS")
    public RestResponse updateForQS(@RequestBody List<WaterDistributionRatio> waterDistributionRatios) {
        List<TotalIdToStation> list = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, waterDistributionRatios.get(0).getStation()).list();
        //计算行合计
        if(null != list && list.size()>0){
            Double total = 0.0;
            List<String> collect = list.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
            for(String id:collect){
                Double value = 0.0;
                TrendsTableParam tableParam = trendsTableParamService.getById(id);
                if(!tableParam.getPId().equals("0")){
                    List<TrendsTableParam> noTotalList = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, tableParam.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                    for(TrendsTableParam param:noTotalList){
                        for(WaterDistributionRatio t:waterDistributionRatios){
                            if(t.getTableHeadId().equals(param.getId())){
                                value+=t.getV()==null?0.0:t.getV();
                            }
                            List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
                            if(null != listed && listed.size()>0){
                                for (TrendsTableParam param1:listed){
                                    if(t.getTableHeadId().equals(param1.getId())){
                                        value+=t.getV()==null?0.0:t.getV();
                                    }
                                }
                            }
                        }
                    }
                    for(WaterDistributionRatio t:waterDistributionRatios){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }

                }
            }
            for(WaterDistributionRatio t:waterDistributionRatios){
                if(!list.stream().map(TotalIdToStation::getTotalId).collect(Collectors.toList()).contains(t.getTableHeadId())){
                    total+=t.getV()==null?0.0:t.getV();
                }
            }
            TrendsTableParam one = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, "0").eq(TrendsTableParam::getUseType,2).in(TrendsTableParam::getId, collect).one();
            if(null != one){
                for(WaterDistributionRatio t:waterDistributionRatios){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        boolean b = waterDistributionRatioService.updateBatchById(waterDistributionRatios);
        if(b) {
            return RestResponse.ok("修改除成功");
        }else {
            return RestResponse.no("修改失败");
        }
    }
}

