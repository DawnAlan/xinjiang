package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.TrunkCanalSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanTrunkCanal;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.mapper.YearWaterUsePlanTrunkCanalOwnerMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanTrunkCanalOwner;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanTrunkCanalOwnerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * (YearWaterUsePlanTrunkCanalOwner)表服务实现类
 *
 * @author makejava
 * @since 2024-03-22 19:40:53
 */
@Service("yearWaterUsePlanTrunkCanalOwnerService")
public class YearWaterUsePlanTrunkCanalOwnerServiceImpl extends ServiceImpl<YearWaterUsePlanTrunkCanalOwnerMapper, YearWaterUsePlanTrunkCanalOwner> implements YearWaterUsePlanTrunkCanalOwnerService {

    @Override
    public RestResponse<List<YearWaterUsePlanTrunkCanalOwner>> selectList(TrunkCanalSelectListReq req) {
        List<YearWaterUsePlanTrunkCanalOwner> list = this.lambdaQuery().
                eq(StringUtils.isNotEmpty(req.getArea()),YearWaterUsePlanTrunkCanalOwner::getArea, req.getArea()).
                eq(req.getYear()!=null,YearWaterUsePlanTrunkCanalOwner::getYear, req.getYear()).
                eq(YearWaterUsePlanTrunkCanalOwner::getDel,0).
                list();
        if(null!=list && list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    public RestResponse addTrunkCanal(YearWaterUsePlanTrunkCanalOwner yearWaterUsePlanTrunkCanalOwner) {
        yearWaterUsePlanTrunkCanalOwner.setId(UUIDUtils.getUUID());
        yearWaterUsePlanTrunkCanalOwner.setDel(0);
        yearWaterUsePlanTrunkCanalOwner.setCreateTime(new Date());
        boolean save = this.save(yearWaterUsePlanTrunkCanalOwner);
        if(save){
            return RestResponse.ok("新增成功");
        }else {
            return RestResponse.no("新增失败");
        }
    }
}

