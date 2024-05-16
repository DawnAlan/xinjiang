package com.cj.waterresources.func.modular.overallSituationUnitMgr.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.DataSynchronization.api.DataSynchronizationApi;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.bean.res.OverallSituationUnitMgrTreeRes;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.mapper.OverallSituationUnitMgrMapper;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.entity.OverallSituationUnitMgr;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.service.OverallSituationUnitMgrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局单位管理(OverallSituationUnitMgr)表服务实现类
 *
 * @author makejava
 * @since 2024-02-21 11:11:46
 */
@Service("overallSituationUnitMgrService")
public class OverallSituationUnitMgrServiceImpl extends ServiceImpl<OverallSituationUnitMgrMapper, OverallSituationUnitMgr> implements OverallSituationUnitMgrService {

    @Autowired
    private DataSynchronizationApi dataSynchronizationApi;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public RestResponse add(OverallSituationUnitMgr overallSituationUnitMgr) {
        if(StringUtils.isNotBlank(overallSituationUnitMgr.getMonitorId())){
            List<OverallSituationUnitMgr> list1 = this.lambdaQuery().eq(OverallSituationUnitMgr::getMonitorId, overallSituationUnitMgr.getMonitorId()).list();
            if(!list1.isEmpty()){
                return RestResponse.no("请勿重复绑定监测点");
            }
        }
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        overallSituationUnitMgr.setId(UUIDUtils.getUUID());
        overallSituationUnitMgr.setCreateTime(new Date());
        overallSituationUnitMgr.setCreateBy(saBaseLoginUser.getName());
        boolean save = this.save(overallSituationUnitMgr);
        if(save){
            List<OverallSituationUnitMgr> list = this.list();
            redisUtil.set("overallSituationUnitMgr:list", JSONObject.toJSONString(list));
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse delete(String id) {
        boolean b = this.removeById(id);
        if(b){
            List<OverallSituationUnitMgr> list = this.list();
            redisUtil.set("overallSituationUnitMgr:list", JSONObject.toJSONString(list));
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse update(OverallSituationUnitMgr overallSituationUnitMgr) {
        if(StringUtils.isNotBlank(overallSituationUnitMgr.getMonitorId())){
            OverallSituationUnitMgr byId = this.getById(overallSituationUnitMgr.getId());
            if(!byId.getMonitorId().equals(overallSituationUnitMgr.getMonitorId())){
                List<OverallSituationUnitMgr> list1 = this.lambdaQuery().eq(OverallSituationUnitMgr::getMonitorId, overallSituationUnitMgr.getMonitorId()).list();
                if(!list1.isEmpty()){
                    return RestResponse.no("请勿重复绑定监测点");
                }
            }
        }
        boolean b = this.updateById(overallSituationUnitMgr);
        if(b){
            List<OverallSituationUnitMgr> list = this.list();
            redisUtil.set("overallSituationUnitMgr:list", JSONObject.toJSONString(list));
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse selectTree() {
        String overall = (String) redisUtil.get("overallSituationUnitMgr:list");
        if(StringUtils.isEmpty(overall)){
            List<OverallSituationUnitMgr> list = this.list();
            redisUtil.set("overallSituationUnitMgr:list", JSONObject.toJSONString(list));
            overall = JSONObject.toJSONString(list);
        }
        List<OverallSituationUnitMgr> list = JSONObject.parseArray(overall, OverallSituationUnitMgr.class);
        List<OverallSituationUnitMgrTreeRes> resultList = new ArrayList<>();
        for(OverallSituationUnitMgr param :list){
            if(param.getPId().equals("0")){
                OverallSituationUnitMgrTreeRes res = new OverallSituationUnitMgrTreeRes();
                BeanUtils.copyProperties(param,res);
                resultList.add(res);
            }
        }
        getParamTree(resultList,list);
        if(null != resultList && resultList.size()>0){
            return RestResponse.ok(resultList);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    public RestResponse updateMonitor(Integer treeType) {
        String s = dataSynchronizationApi.updateMonitor(treeType);
        if(s.equals("200")){
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    public void getParamTree(List<OverallSituationUnitMgrTreeRes> resultList, List<OverallSituationUnitMgr> list){
        if(resultList.size()>0){
            for(OverallSituationUnitMgrTreeRes res : resultList){
                List<OverallSituationUnitMgr> collect = list.stream().filter(t -> t.getPId().equals(res.getId())).collect(Collectors.toList());
                if(collect.size()>0){
                    List<OverallSituationUnitMgrTreeRes> tempList = new ArrayList<>();
                    for (OverallSituationUnitMgr param:collect){
                        OverallSituationUnitMgrTreeRes tempRes = new OverallSituationUnitMgrTreeRes();
                        BeanUtils.copyProperties(param,tempRes);
                        tempList.add(tempRes);
                    }
                    res.setChildren(tempList);
                    getParamTree(tempList,list);
                }
            }
        }
    }
}

