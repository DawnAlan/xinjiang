package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.TrunkCanalSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.mapper.YearWaterUsePlanTrunkCanalMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanTrunkCanal;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanTrunkCanalService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 干渠年用水计划(YearWaterUsePlanTrunkCanal)表服务实现类
 *
 * @author makejava
 * @since 2023-12-01 18:26:47
 */
@Service("yearWaterUsePlanTrunkCanalService")
public class YearWaterUsePlanTrunkCanalServiceImpl extends ServiceImpl<YearWaterUsePlanTrunkCanalMapper, YearWaterUsePlanTrunkCanal> implements YearWaterUsePlanTrunkCanalService {

    @Override
    public RestResponse<List<YearWaterUsePlanTrunkCanal>> selectList(TrunkCanalSelectListReq req) {
        List<YearWaterUsePlanTrunkCanal> list = this.lambdaQuery().
                eq(StringUtils.isNotEmpty(req.getArea()),YearWaterUsePlanTrunkCanal::getArea, req.getArea()).
                eq(req.getYear()!=null,YearWaterUsePlanTrunkCanal::getYear, req.getYear()).
                eq(YearWaterUsePlanTrunkCanal::getDel,0).
                list();
        if(null!=list && list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    public RestResponse updateTrunkCanal(YearWaterUsePlanTrunkCanal yearWaterUsePlanTrunkCanal) {
        yearWaterUsePlanTrunkCanal.setUpdateTime(new Date());
        boolean b = this.updateById(yearWaterUsePlanTrunkCanal);
        if(b){
            return RestResponse.ok("更新成功");
        }else {
            return RestResponse.no("更新失败");
        }
    }

    @Override
    public RestResponse addTrunkCanal(YearWaterUsePlanTrunkCanal yearWaterUsePlanTrunkCanal) {
        yearWaterUsePlanTrunkCanal.setId(UUIDUtils.getUUID());
        yearWaterUsePlanTrunkCanal.setDel(0);
        yearWaterUsePlanTrunkCanal.setCreateTime(new Date());
        boolean save = this.save(yearWaterUsePlanTrunkCanal);
        if(save){
            return RestResponse.ok("新增成功");
        }else {
            return RestResponse.no("新增失败");
        }
    }
}

