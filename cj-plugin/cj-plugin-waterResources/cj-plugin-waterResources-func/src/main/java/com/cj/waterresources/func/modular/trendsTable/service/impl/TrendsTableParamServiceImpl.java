package com.cj.waterresources.func.modular.trendsTable.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.sys.feign.SysUserFeign;
import com.cj.waterresources.func.modular.trendsTable.bean.req.QueryTrendsTableParamReq;
import com.cj.waterresources.func.modular.trendsTable.bean.res.WaterDailyParamSelectRes;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.trendsTable.mapper.TrendsTableParamMapper;
import com.cj.common.util.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    private SysUserFeign sysUserFeign;

    @Override
    public RestResponse add(TrendsTableParam param) {
        try {
            param.setId(UUIDUtils.getUUID());
            if(StringUtils.isEmpty(param.getPId())){
                param.setPId("0");
            }
            boolean save = this.save(param);
            if(save){
                if(!param.getPId().equals("0")){
                    this.lambdaUpdate().set(TrendsTableParam::getIsParent, 1).eq(TrendsTableParam::getId, param.getPId()).update();
                }
                return RestResponse.ok("保存成功");
            }else {
                return RestResponse.no("保存失败");
            }
        }catch (Exception e){
            log.error("保存参数节点报错:"+e.getMessage());
            return RestResponse.no("保存错误");
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
                            return RestResponse.ok("删除成功");
                        }else {
                            return RestResponse.no("删除失败");
                        }
                    }
                    return RestResponse.ok("删除成功");
                }else {
                    return RestResponse.no("删除失败");
                }

            }else {
                boolean b = this.removeById(id);
                if(b){
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
    public RestResponse update(TrendsTableParam param) {
        try {
            boolean b = this.updateById(param);
            if(b){
                return RestResponse.ok("修改成功");
            }else {
                return RestResponse.no("修改失败");
            }
        }catch (Exception e){
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
        return RestResponse.ok(resultList);
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
                        tempList.add(tempRes);
                    }
                    res.setChildren(tempList);
                    getParamTree(tempList,list);
                }
            }
        }
    }
}




