package com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTotalForm.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.surfaceWater.entity.QueryListReq;
import com.cj.waterresources.func.modular.surfaceWater.generator.domain.SurfaceWater;
import com.cj.waterresources.func.modular.surfaceWater.generator.service.SurfaceWaterFlowDetailService;
import com.cj.waterresources.func.modular.surfaceWater.generator.service.SurfaceWaterService;
import com.cj.waterresources.func.modular.surfaceWater.vo.TenDayVo;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanCropService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanTrunkCanalService;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.entity.WaterStorageSchedulingLzz;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.service.WaterStorageSchedulingLzzService;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTotalForm.mapper.WaterStorageSchedulingTotalFormMapper;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTotalForm.entity.WaterStorageSchedulingTotalForm;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTotalForm.service.WaterStorageSchedulingTotalFormService;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.entity.WaterStorageSchedulingTth;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.service.WaterStorageSchedulingTthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 供水计划管理总表(WaterStorageSchedulingTotalForm)表服务实现类
 *
 * @author makejava
 * @since 2024-02-18 09:43:28
 */
@Service("waterStorageSchedulingTotalFormService")
public class WaterStorageSchedulingTotalFormServiceImpl extends ServiceImpl<WaterStorageSchedulingTotalFormMapper, WaterStorageSchedulingTotalForm> implements WaterStorageSchedulingTotalFormService {

    @Autowired
    private WaterStorageSchedulingLzzService waterStorageSchedulingLzzService;

    @Autowired
    private WaterStorageSchedulingTthService waterStorageSchedulingTthService;

    @Autowired
    private SurfaceWaterService surfaceWaterService;

    @Override
    public RestResponse add(WaterStorageSchedulingTotalForm waterStorageSchedulingTotalForm) {
        List<WaterStorageSchedulingTotalForm> list = this.lambdaQuery().eq(WaterStorageSchedulingTotalForm::getPlanName, waterStorageSchedulingTotalForm.getPlanName()).list();
        if(list.size()>0){
            return RestResponse.no("请勿重复添加方案名称");
        }
        QueryListReq req = new QueryListReq();
        req.setYear(waterStorageSchedulingTotalForm.getInflowYear());
        req.setTableName("日平均流量表");
        req.setPageNo(1);
        req.setPageSize(1);
        IPage<SurfaceWater> surfaceWaterIPage = surfaceWaterService.queryList(req);
        if(surfaceWaterIPage.getTotal()<1){
            return RestResponse.no("当年无来水数据");
        }
        boolean save = this.save(waterStorageSchedulingTotalForm);
        if(save){
            return RestResponse.ok("添加成功");
        }else {
            return RestResponse.no("添加失败");
        }
    }

    @Override
    public RestResponse remove(String id) {
        boolean b = this.lambdaUpdate().eq(WaterStorageSchedulingTotalForm::getId,id).remove();
        if(b){
            waterStorageSchedulingLzzService.lambdaUpdate().eq(WaterStorageSchedulingLzz::getFormId,id).remove();
            waterStorageSchedulingTthService.lambdaUpdate().eq(WaterStorageSchedulingTth::getFormId,id).remove();
            return RestResponse.ok("删除成功");
        }else {
            return RestResponse.no("删除失败");
        }
    }
}

