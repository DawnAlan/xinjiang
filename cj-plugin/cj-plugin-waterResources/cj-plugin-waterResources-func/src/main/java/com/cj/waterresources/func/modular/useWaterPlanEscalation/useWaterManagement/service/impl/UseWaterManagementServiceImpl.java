package com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.MonthWaterUsePlanService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.req.UseWaterManagementAddReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.req.UseWaterManagementBindIdReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.req.UseWaterManagementQueryReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.res.UseWaterManagementQueryRes;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.mapper.UseWaterManagementMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.entity.UseWaterManagement;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.service.UseWaterManagementService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanTrunkCanal;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanTrunkCanalService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用水单位管理(UseWaterManagement)表服务实现类
 *
 * @author makejava
 * @since 2023-11-28 17:14:42
 */
@Service("useWaterManagementService")
public class UseWaterManagementServiceImpl extends ServiceImpl<UseWaterManagementMapper, UseWaterManagement> implements UseWaterManagementService {

    @Override
    public RestResponse insert(UseWaterManagementAddReq req) {
        try {
            List<UseWaterManagement> list = this.lambdaQuery().
                    eq(UseWaterManagement::getUnitName, req.getUnitName()).
                    eq(UseWaterManagement::getUseWaterPlan,req.getUseWaterPlan()).
                    eq(UseWaterManagement::getArea,req.getArea()).
                    eq(UseWaterManagement::getDel,0).
                    list();
            if(list.size()>0){
                return RestResponse.no("该名称已重复");
            }

            UseWaterManagement useWaterManagement  = new UseWaterManagement();
            BeanUtils.copyProperties(req, useWaterManagement);
            useWaterManagement.setId(UUIDUtils.getUUID());
            useWaterManagement.setDel(0);
            useWaterManagement.setCreateTime(new Date());
            boolean save = this.save(useWaterManagement);
            if(save){
                return RestResponse.ok("添加成功");
            }else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return RestResponse.no("添加失败");
            }
        }catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return RestResponse.no("添加失败");
        }
    }

    @Override
    public RestResponse delete(String id,String useWaterPlan) {
        List<UseWaterManagement> list = this.lambdaQuery().eq(UseWaterManagement::getPId, id).list();
        boolean update = false;
        if(list.isEmpty()){
            update = this.lambdaUpdate().eq(UseWaterManagement::getId, id).remove();
        }else {
            List<String> strings = list.stream().map(UseWaterManagement::getId).collect(Collectors.toList());
            strings.add(id);
            update = this.lambdaUpdate().in(UseWaterManagement::getId, strings).remove();
        }
        if(update){
            return RestResponse.ok("删除成功");
        }else {
            return RestResponse.no("删除失败");
        }
    }

    @Override
    public RestResponse<List<UseWaterManagementQueryRes>> select(UseWaterManagementQueryReq req) {
        List<UseWaterManagementQueryRes> useWaterManagementQueryRes = this.baseMapper.selectListByReq(req);
        List<UseWaterManagementQueryRes> collect = useWaterManagementQueryRes.stream().filter(t -> null != t.getPId() && t.getPId().equals("0")).collect(Collectors.toList());
        if(null != collect && collect.size()>0){
            if(req.getUseWaterPlan().equals("日用水计划")){
                getParamTreeForDay(collect, useWaterManagementQueryRes);
            }else {
                getParamTree(collect, useWaterManagementQueryRes);
            }
            if(null != useWaterManagementQueryRes && useWaterManagementQueryRes.size()>0){
                return RestResponse.ok(collect);
            }else {
                return RestResponse.no("暂无数据");
            }
        }else {
            if(null != useWaterManagementQueryRes && useWaterManagementQueryRes.size()>0){
                return RestResponse.ok(useWaterManagementQueryRes);
            }else {
                return RestResponse.no("暂无数据");
            }
        }
    }

    @Override
    public RestResponse bindId(UseWaterManagementBindIdReq req) {
        boolean update = this.lambdaUpdate().set(UseWaterManagement::getBindId, req.getBindId()).eq(UseWaterManagement::getId, req.getId()).update();
        if(update) {
            return RestResponse.ok("绑定成功");
        }else {
            return RestResponse.no("绑定失败");
        }
    }

    public void getParamTree(List<UseWaterManagementQueryRes> resultList, List<UseWaterManagementQueryRes> list){
        if(resultList.size()>0){
            for(UseWaterManagementQueryRes res : resultList){
                List<UseWaterManagementQueryRes> collect = list.stream().filter(t -> t.getPId().equals(res.getId())).collect(Collectors.toList());
                if(collect.size()>0){
                    List<UseWaterManagementQueryRes> tempList = new ArrayList<>();
                    for (UseWaterManagementQueryRes param:collect){
                        UseWaterManagementQueryRes tempRes = new UseWaterManagementQueryRes();
                        BeanUtils.copyProperties(param,tempRes);
                        tempList.add(tempRes);
                    }
                    res.setChildren(tempList);
                    getParamTree(tempList,list);
                }
            }
        }
    }

    public void getParamTreeForDay(List<UseWaterManagementQueryRes> resultList, List<UseWaterManagementQueryRes> list){
        if(resultList.size()>0){
            for(UseWaterManagementQueryRes res : resultList){
                List<UseWaterManagementQueryRes> collect = list.stream().filter(t -> t.getPId().equals(res.getUnitId())).collect(Collectors.toList());
                if(collect.size()>0){
                    List<UseWaterManagementQueryRes> tempList = new ArrayList<>();
                    for (UseWaterManagementQueryRes param:collect){
                        UseWaterManagementQueryRes tempRes = new UseWaterManagementQueryRes();
                        BeanUtils.copyProperties(param,tempRes);
                        tempList.add(tempRes);
                    }
                    res.setChildren(tempList);
                    getParamTreeForDay(tempList,list);
                }
            }
        }
    }
}

