package com.cj.waterresources.func.modular.trendsTable.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.sys.feign.SysUserFeign;
import com.cj.waterresources.func.modular.trendsTable.bean.req.QueryTrendsTableParamReq;
import com.cj.waterresources.func.modular.trendsTable.bean.req.TrendsTableParamAddReq;
import com.cj.waterresources.func.modular.trendsTable.bean.req.TrendsTableParamUpdateReq;
import com.cj.waterresources.func.modular.trendsTable.bean.res.WaterDailyParamSelectRes;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.trendsTable.mapper.TrendsTableParamMapper;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.entity.TotalIdToStation;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.service.TotalIdToStationService;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.entity.WaterPriceManagement;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.service.WaterPriceManagementService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
* @author July Lion
* @description 针对表【WATER_DAILY_PARAM(水情日报参数表)】的数据库操作Service实现
* @createDate 2023-10-27 10:41:38
*/
@Service
@Slf4j
public class TrendsTableParamServiceImpl extends ServiceImpl<TrendsTableParamMapper, TrendsTableParam>
    implements TrendsTableParamService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WaterPriceManagementService waterPriceManagementService;

    @Autowired
    private TotalIdToStationService totalIdToStationService;

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    @Override
    @Transactional(rollbackFor=Exception.class)
    public RestResponse add(TrendsTableParamAddReq req) {
        TrendsTableParam one = this.lambdaQuery().eq(TrendsTableParam::getUseType, req.getUseType()).
                eq(TrendsTableParam::getUseStation, req.getUseStation()).
                eq(TrendsTableParam::getPId,req.getPId()).
                eq(TrendsTableParam::getParamName, req.getParamName()).one();
        if(one != null){
            return RestResponse.no("该表头名称已存在，请更换名称");
        }
        TrendsTableParam param = new TrendsTableParam();
        BeanUtils.copyProperties(req, param);
        param.setId(UUIDUtils.getUUID());
        if(StringUtils.isEmpty(param.getPId())){
            param.setPId("0");
        }
        boolean save = this.save(param);
        if(save){
            if(!param.getPId().equals("0")){
                this.lambdaUpdate().set(TrendsTableParam::getIsParent, 1).eq(TrendsTableParam::getId, param.getPId()).update();
            }
            if(req.getParamName().equals("合计")){
                TotalIdToStation  totalIdToStation = new TotalIdToStation();
                totalIdToStation.setStation(param.getUseStation());
                totalIdToStation.setTotalId(param.getId());
                totalIdToStation.setUseType(param.getUseType());
                totalIdToStation.setName(param.getParamName());
                totalIdToStationService.save(totalIdToStation);
            }
            if(!req.getParamName().equals("合计") && param.getUseType()==2){
                if(StringUtils.isEmpty(req.getUseWaterType())){
                    throw new RuntimeException("请选择用水类型");
                }
            }
            if(param.getUseType()==2 && !req.getParamName().equals("合计")){
                WaterPriceManagement waterPriceManagement = new WaterPriceManagement();
                waterPriceManagement.setUseWaterType(req.getUseWaterType());
                waterPriceManagement.setId(param.getId());
                waterPriceManagement.setCreateTime(new Date());
                waterPriceManagement.setDel(0);
                waterPriceManagement.setStation(param.getUseStation());
                waterPriceManagement.setPId(param.getPId());
                waterPriceManagement.setUserName(param.getParamName());
                boolean save1 = waterPriceManagementService.save(waterPriceManagement);
                if(!save1){
                    throw new RuntimeException("保存失败");
                }else {
                    updateCache();
                    return RestResponse.ok("保存成功");
                }
            }else {
                updateCache();
                return RestResponse.ok("保存成功");
            }
        }else {
            return RestResponse.no("保存失败");
        }
    }
    @Override
    public RestResponse delete(String id) {
        try {
            TrendsTableParam byId = this.getById(id);
            if(StringUtils.isNotEmpty(byId.getIsParent())&&byId.getIsParent().equals("1")){
                boolean b = this.removeById(id);
                if(b){
                    List<String> collect = this.lambdaQuery().eq(TrendsTableParam::getPId, id).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                    if(collect.size()>0){
                        boolean b1 = this.removeBatchByIds(collect);
                        if(b1){
                            if(byId.getUseType()==2){
                                ExecutorService pool = Executors.newSingleThreadExecutor();
                                pool.submit(new Runnable() {
                                    private WaterPriceManagementService waterPriceManagementService = SpringUtil.getBean(WaterPriceManagementService.class);
                                    @Override
                                    public void run() {
                                        try {
                                            waterPriceManagementService.removeById(id);
                                            waterPriceManagementService.removeBatchByIds(collect);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                            totalIdToStationService.lambdaUpdate().eq(TotalIdToStation::getTotalId,id).remove();
                            List<String> collected = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, id).list().stream().filter(t -> t.getParamName().equals("合计")).map(TrendsTableParam::getId).collect(Collectors.toList());
                            if(null != collected && collected.size()>0){
                                totalIdToStationService.lambdaUpdate().in(TotalIdToStation::getTotalId,collected).remove();
                            }
                            updateCache();
                            return RestResponse.ok("删除成功");
                        }else {
                            return RestResponse.no("删除失败");
                        }
                    }
                    updateCache();
                    return RestResponse.ok("删除成功");
                }else {
                    return RestResponse.no("删除失败");
                }

            }else {
                boolean b = this.removeById(id);
                if(b){
                    if(byId.getUseType()==2){
                        ExecutorService pool = Executors.newSingleThreadExecutor();
                        pool.submit(new Runnable() {
                            private WaterPriceManagementService waterPriceManagementService = SpringUtil.getBean(WaterPriceManagementService.class);
                            @Override
                            public void run() {
                                try {
                                    waterPriceManagementService.removeById(id);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    totalIdToStationService.lambdaUpdate().eq(TotalIdToStation::getTotalId,id).remove();
                    updateCache();
                    return RestResponse.ok("删除成功");
                }else {
                    return RestResponse.no("删除失败");
                }
            }
        }catch (Exception e){
            log.error("删除参数节点报错:"+e.getMessage());
            return RestResponse.no("删除错误");
        }
    }

    @Override
    @Transactional(rollbackFor=RuntimeException.class)
    public RestResponse update(TrendsTableParamUpdateReq req) {
        try {
            if(req.getParam().getOrderNum()==null){
                return RestResponse.no("orderNum is blank");
            }
            this.lambdaUpdate().set(TrendsTableParam::getOrderNum,req.getParam().getOrderNum()).eq(TrendsTableParam::getId,req.getParam().getId()).update();
            TrendsTableParam byId = this.getById(req.getParam().getId());
            if(byId.getParamName().equals(req.getParam().getParamName())){
                if(req.getParam().getUseType()==2){
                    if(StringUtils.isNotEmpty(req.getUseWaterType())){
                        boolean update = waterPriceManagementService.lambdaUpdate().set(WaterPriceManagement::getUseWaterType, StringUtils.isEmpty(req.getUseWaterType())?null:req.getUseWaterType()).
                                eq(WaterPriceManagement::getId, req.getParam().getId()).update();
                        if(update){
                            updateCache();
                            return RestResponse.ok("修改成功");
                        }else {
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return RestResponse.no("修改失败");
                        }
                    }else {
                        if(!req.getParam().getParamName().equals("合计")){
                            boolean update = waterPriceManagementService.lambdaUpdate().set(WaterPriceManagement::getUseWaterType, StringUtils.isEmpty(req.getUseWaterType())?null:req.getUseWaterType()).
                                    eq(WaterPriceManagement::getId, req.getParam().getId()).update();
                            if(update){
                                updateCache();
                                return RestResponse.ok("修改成功");
                            }else {
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                return RestResponse.no("修改失败");
                            }
                        }else {
                            updateCache();
                            return RestResponse.ok("修改成功");
                        }
                    }
                }else {
                    updateCache();
                    return RestResponse.ok("修改成功");
                }
            }else {
                TrendsTableParam one = this.lambdaQuery().eq(TrendsTableParam::getUseType, req.getParam().getUseType()).
                        eq(TrendsTableParam::getUseStation, req.getParam().getUseStation()).
                        eq(TrendsTableParam::getPId,req.getParam().getPId()).
                        eq(TrendsTableParam::getParamName, req.getParam().getParamName()).one();
                if(one != null){
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return RestResponse.no("该表头名称已存在，请更换名称");
                }else {
                    boolean b = this.updateById(req.getParam());
                    if(b){
                        if(req.getParam().getUseType()==2){
                            if(!req.getParam().getParamName().equals("合计")) {
                                boolean update = waterPriceManagementService.lambdaUpdate().
                                        set(WaterPriceManagement::getUserName, req.getParam().getParamName()).
                                        set(WaterPriceManagement::getUseWaterType,  StringUtils.isEmpty(req.getUseWaterType())?null:req.getUseWaterType()).
                                        eq(WaterPriceManagement::getId, req.getParam().getId()).update();
                                if(update){
                                    updateCache();
                                    return RestResponse.ok("修改成功");
                                }else {
                                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                    return RestResponse.no("修改失败");
                                }
                            }else {
                                if (StringUtils.isEmpty(req.getUseWaterType())) {
                                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                    return RestResponse.no("请选择用水类型");
                                }else {
                                    boolean update = waterPriceManagementService.lambdaUpdate().
                                            set(WaterPriceManagement::getUserName, req.getParam().getParamName()).
                                            set(WaterPriceManagement::getUseWaterType,  StringUtils.isEmpty(req.getUseWaterType())?null:req.getUseWaterType()).
                                            eq(WaterPriceManagement::getId, req.getParam().getId()).update();
                                    if(update){
                                        updateCache();
                                        return RestResponse.ok("修改成功");
                                    }else {
                                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                        return RestResponse.no("修改失败");
                                    }
                                }
                            }
                        }else {
                            updateCache();
                            return RestResponse.ok("修改成功");
                        }
                    }else {
                        return RestResponse.no("修改失败");
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("修改参数节点报错:"+e.getMessage());
            return RestResponse.no("修改错误");
        }
    }

    @Override
    public RestResponse<List<WaterDailyParamSelectRes>> select(QueryTrendsTableParamReq req) {
        List<WaterDailyParamSelectRes> resultList = new ArrayList<>();
        List<TrendsTableParam> list = this.lambdaQuery().eq(TrendsTableParam::getUseType,req.getUseType()).eq(TrendsTableParam::getUseStation,req.getUseStation()).list();
        List<TrendsTableParam> collect = list.stream().filter(t -> t.getPId().equals("0")).collect(Collectors.toList());
        for (TrendsTableParam param:collect){
            WaterDailyParamSelectRes tempRes = new WaterDailyParamSelectRes();
            BeanUtils.copyProperties(param,tempRes);
            resultList.add(tempRes);
        }
        getParamTree(resultList,list);
        if(null != resultList && resultList.size()>0){
            return RestResponse.ok(resultList);
        }else {
            return RestResponse.no("暂无数据");
        }

    }

    @Override
    public RestResponse<List<TrendsTableParam>> selectNoParent() {
        List<TrendsTableParam> list1 = this.list();
        List<String> pid = list1.stream().map(TrendsTableParam::getPId).distinct().collect(Collectors.toList());
        List<TrendsTableParam> result = list1.stream().filter(t -> !pid.contains(t.getId())).collect(Collectors.toList());
        if(result.size()>0){
            return RestResponse.ok(result);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    public void getParamTree(List<WaterDailyParamSelectRes> resultList,List<TrendsTableParam> list){
        if(resultList.size()>0){
            for(WaterDailyParamSelectRes res : resultList){
                List<TrendsTableParam> collect = list.stream().filter(t -> t.getPId().equals(res.getId())).collect(Collectors.toList());
                if(collect.size()>0){
                    List<WaterDailyParamSelectRes> tempList = new ArrayList<>();
                    for (TrendsTableParam param:collect){
                        WaterDailyParamSelectRes tempRes = new WaterDailyParamSelectRes();
                        BeanUtils.copyProperties(param,tempRes);
                        WaterPriceManagement byId = waterPriceManagementService.getById(tempRes.getId());
                        tempRes.setUseWaterType(byId != null ? byId.getUseWaterType():"");
                        tempList.add(tempRes);
                    }
                    WaterPriceManagement byId = waterPriceManagementService.getById(res.getId());
                    res.setUseWaterType(byId != null ? byId.getUseWaterType():"");
                    res.setChildren(tempList);
                    getParamTree(tempList,list);
                }else {
                    WaterPriceManagement byId = waterPriceManagementService.getById(res.getId());
                    res.setUseWaterType(byId != null ? byId.getUseWaterType():"");
                }
            }
        }
    }

    public void updateCache(){
        List<TrendsTableParam> listed = this.list();
        redisUtil.set("trendsTableParam:list", JSONObject.toJSONString(listed));
        for (TrendsTableParam param:listed){
            redisUtil.set("trendsTableParam:name:"+param.getId(), param.getParamName());
            redisUtil.set("trendsTableParam:object:"+param.getId(), JSONObject.toJSONString(param));
        }
    }
}




