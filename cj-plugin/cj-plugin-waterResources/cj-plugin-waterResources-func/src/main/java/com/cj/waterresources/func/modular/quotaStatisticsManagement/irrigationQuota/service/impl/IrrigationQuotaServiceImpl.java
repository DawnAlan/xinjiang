package com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.bean.req.IrrigationQuotaListReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.mapper.IrrigationQuotaMapper;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.entity.IrrigationQuota;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.service.IrrigationQuotaService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCrop;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 灌溉额度表(IrrigationQuota)表服务实现类
 *
 * @author makejava
 * @since 2023-12-22 12:49:39
 */
@Service("irrigationQuotaService")
public class IrrigationQuotaServiceImpl extends ServiceImpl<IrrigationQuotaMapper, IrrigationQuota> implements IrrigationQuotaService {

    @Override
    public RestResponse add(IrrigationQuota irrigationQuota) {
        List<IrrigationQuota> list = this.lambdaQuery().eq(IrrigationQuota::getWaterUser, irrigationQuota.getWaterUser()).
                eq(IrrigationQuota::getStation, irrigationQuota.getStation()).eq(IrrigationQuota::getYear,irrigationQuota.getYear()).
                eq(IrrigationQuota::getIrrigationCrop, irrigationQuota.getIrrigationCrop()).eq(IrrigationQuota::getDel,0).list();
        if(null!= list && list.size()>0){
            return RestResponse.no("该作物已存在，请勿重复添加");
        }
        irrigationQuota.setId(UUIDUtils.getUUID());
        irrigationQuota.setCreateTime(new Date());
        irrigationQuota.setDel(0);
        irrigationQuota.setAccumulatedIrrigationArea(
                (irrigationQuota.getAprilEarlyOctoberIrrigationArea()==null?0.0:irrigationQuota.getAprilEarlyOctoberIrrigationArea())+
                (irrigationQuota.getAprilMidDayIrrigationArea()==null?0.0:irrigationQuota.getAprilMidDayIrrigationArea())+
                (irrigationQuota.getAprilLateOctoberIrrigationArea()==null?0.0:irrigationQuota.getAprilLateOctoberIrrigationArea())+
                (irrigationQuota.getMayEarlyOctoberIrrigationArea()==null?0.0:irrigationQuota.getMayEarlyOctoberIrrigationArea())+
                (irrigationQuota.getMayMidDayIrrigationArea()==null?0.0:irrigationQuota.getMayMidDayIrrigationArea())+
                (irrigationQuota.getMayLateOctoberIrrigationArea()==null?0.0:irrigationQuota.getMayLateOctoberIrrigationArea())+
                (irrigationQuota.getJuneEarlyOctoberIrrigationArea()==null?0.0:irrigationQuota.getJuneEarlyOctoberIrrigationArea())+
                (irrigationQuota.getJuneMidDayIrrigationArea()==null?0.0:irrigationQuota.getJuneMidDayIrrigationArea())+
                (irrigationQuota.getJuneLateOctoberIrrigationArea()==null?0.0:irrigationQuota.getJuneLateOctoberIrrigationArea())+
                (irrigationQuota.getJulyEarlyOctoberIrrigationArea()==null?0.0:irrigationQuota.getJulyEarlyOctoberIrrigationArea())+
                (irrigationQuota.getJulyMidDayIrrigationArea()==null?0.0:irrigationQuota.getJulyMidDayIrrigationArea())+
                (irrigationQuota.getJulyLateOctoberIrrigationArea()==null?0.0:irrigationQuota.getJulyLateOctoberIrrigationArea())+
                (irrigationQuota.getAugustEarlyOctoberIrrigationArea()==null?0.0:irrigationQuota.getAugustEarlyOctoberIrrigationArea())+
                (irrigationQuota.getAugustMidDayIrrigationArea()==null?0.0:irrigationQuota.getAugustMidDayIrrigationArea())+
                (irrigationQuota.getAugustLateOctoberIrrigationArea()==null?0.0:irrigationQuota.getAugustLateOctoberIrrigationArea())+
                (irrigationQuota.getSeptemberEarlyOctoberIrrigationArea()==null?0.0:irrigationQuota.getSeptemberEarlyOctoberIrrigationArea())+
                (irrigationQuota.getSeptemberMidDayIrrigationArea()==null?0.0:irrigationQuota.getSeptemberMidDayIrrigationArea())+
                (irrigationQuota.getSeptemberLateOctoberIrrigationArea()==null?0.0:irrigationQuota.getSeptemberLateOctoberIrrigationArea())+
                (irrigationQuota.getOctoberEarlyOctoberIrrigationArea()==null?0.0:irrigationQuota.getOctoberEarlyOctoberIrrigationArea())+
                (irrigationQuota.getOctoberMidDayIrrigationArea()==null?0.0:irrigationQuota.getOctoberMidDayIrrigationArea())+
                (irrigationQuota.getOctoberLateOctoberIrrigationArea()==null?0.0:irrigationQuota.getOctoberLateOctoberIrrigationArea())+
                (irrigationQuota.getNovemberEarlyOctoberIrrigationArea()==null?0.0:irrigationQuota.getNovemberEarlyOctoberIrrigationArea())+
                (irrigationQuota.getNovemberMidDayIrrigationArea()==null?0.0:irrigationQuota.getNovemberMidDayIrrigationArea())+
                (irrigationQuota.getNovemberLateOctoberIrrigationArea()==null?0.0:irrigationQuota.getNovemberLateOctoberIrrigationArea())
        );
        irrigationQuota.setAccumulatedTotalIrrigationAmount(
                (irrigationQuota.getAprilEarlyOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getAprilEarlyOctoberIrrigationWaterVolume())+
                (irrigationQuota.getAprilMidDayIrrigationWaterVolume()==null?0.0:irrigationQuota.getAprilMidDayIrrigationWaterVolume())+
                (irrigationQuota.getAprilLateOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getAprilLateOctoberIrrigationWaterVolume())+
                (irrigationQuota.getMayEarlyOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getMayEarlyOctoberIrrigationWaterVolume())+
                (irrigationQuota.getMayMidDayIrrigationWaterVolume()==null?0.0:irrigationQuota.getMayMidDayIrrigationWaterVolume())+
                (irrigationQuota.getMayLateOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getMayLateOctoberIrrigationWaterVolume())+
                (irrigationQuota.getJuneEarlyOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getJuneEarlyOctoberIrrigationWaterVolume())+
                (irrigationQuota.getJuneMidDayIrrigationWaterVolume()==null?0.0:irrigationQuota.getJuneMidDayIrrigationWaterVolume())+
                (irrigationQuota.getJuneLateOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getJuneLateOctoberIrrigationWaterVolume())+
                (irrigationQuota.getJulyEarlyOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getJulyEarlyOctoberIrrigationWaterVolume())+
                (irrigationQuota.getJulyMidDayIrrigationWaterVolume()==null?0.0:irrigationQuota.getJulyMidDayIrrigationWaterVolume())+
                (irrigationQuota.getJulyLateOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getJulyLateOctoberIrrigationWaterVolume())+
                (irrigationQuota.getAugustEarlyOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getAugustEarlyOctoberIrrigationWaterVolume())+
                (irrigationQuota.getAugustMidDayIrrigationWaterVolume()==null?0.0:irrigationQuota.getAugustMidDayIrrigationWaterVolume())+
                (irrigationQuota.getAugustLateOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getAugustLateOctoberIrrigationWaterVolume())+
                (irrigationQuota.getSeptemberEarlyOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getSeptemberEarlyOctoberIrrigationWaterVolume())+
                (irrigationQuota.getSeptemberMidDayIrrigationWaterVolume()==null?0.0:irrigationQuota.getSeptemberMidDayIrrigationWaterVolume())+
                (irrigationQuota.getSeptemberLateOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getSeptemberLateOctoberIrrigationWaterVolume())+
                (irrigationQuota.getOctoberEarlyOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getOctoberEarlyOctoberIrrigationWaterVolume())+
                (irrigationQuota.getOctoberMidDayIrrigationWaterVolume()==null?0.0:irrigationQuota.getOctoberMidDayIrrigationWaterVolume())+
                (irrigationQuota.getOctoberLateOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getOctoberLateOctoberIrrigationWaterVolume())+
                (irrigationQuota.getNovemberEarlyOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getNovemberEarlyOctoberIrrigationWaterVolume())+
                (irrigationQuota.getNovemberMidDayIrrigationWaterVolume()==null?0.0:irrigationQuota.getNovemberMidDayIrrigationWaterVolume())+
                (irrigationQuota.getNovemberLateOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getNovemberLateOctoberIrrigationWaterVolume())
        );
        boolean save = this.save(irrigationQuota);
        if(save){
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse delete(String id) {
        boolean update = this.lambdaUpdate().set(IrrigationQuota::getDel, 1).eq(IrrigationQuota::getId, id).update();
        if(update){
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse update(IrrigationQuota irrigationQuota) {
        irrigationQuota.setAccumulatedIrrigationArea(
                (irrigationQuota.getAprilEarlyOctoberIrrigationArea()==null?0.0:irrigationQuota.getAprilEarlyOctoberIrrigationArea())+
                        (irrigationQuota.getAprilMidDayIrrigationArea()==null?0.0:irrigationQuota.getAprilMidDayIrrigationArea())+
                        (irrigationQuota.getAprilLateOctoberIrrigationArea()==null?0.0:irrigationQuota.getAprilLateOctoberIrrigationArea())+
                        (irrigationQuota.getMayEarlyOctoberIrrigationArea()==null?0.0:irrigationQuota.getMayEarlyOctoberIrrigationArea())+
                        (irrigationQuota.getMayMidDayIrrigationArea()==null?0.0:irrigationQuota.getMayMidDayIrrigationArea())+
                        (irrigationQuota.getMayLateOctoberIrrigationArea()==null?0.0:irrigationQuota.getMayLateOctoberIrrigationArea())+
                        (irrigationQuota.getJuneEarlyOctoberIrrigationArea()==null?0.0:irrigationQuota.getJuneEarlyOctoberIrrigationArea())+
                        (irrigationQuota.getJuneMidDayIrrigationArea()==null?0.0:irrigationQuota.getJuneMidDayIrrigationArea())+
                        (irrigationQuota.getJuneLateOctoberIrrigationArea()==null?0.0:irrigationQuota.getJuneLateOctoberIrrigationArea())+
                        (irrigationQuota.getJulyEarlyOctoberIrrigationArea()==null?0.0:irrigationQuota.getJulyEarlyOctoberIrrigationArea())+
                        (irrigationQuota.getJulyMidDayIrrigationArea()==null?0.0:irrigationQuota.getJulyMidDayIrrigationArea())+
                        (irrigationQuota.getJulyLateOctoberIrrigationArea()==null?0.0:irrigationQuota.getJulyLateOctoberIrrigationArea())+
                        (irrigationQuota.getAugustEarlyOctoberIrrigationArea()==null?0.0:irrigationQuota.getAugustEarlyOctoberIrrigationArea())+
                        (irrigationQuota.getAugustMidDayIrrigationArea()==null?0.0:irrigationQuota.getAugustMidDayIrrigationArea())+
                        (irrigationQuota.getAugustLateOctoberIrrigationArea()==null?0.0:irrigationQuota.getAugustLateOctoberIrrigationArea())+
                        (irrigationQuota.getSeptemberEarlyOctoberIrrigationArea()==null?0.0:irrigationQuota.getSeptemberEarlyOctoberIrrigationArea())+
                        (irrigationQuota.getSeptemberMidDayIrrigationArea()==null?0.0:irrigationQuota.getSeptemberMidDayIrrigationArea())+
                        (irrigationQuota.getSeptemberLateOctoberIrrigationArea()==null?0.0:irrigationQuota.getSeptemberLateOctoberIrrigationArea())+
                        (irrigationQuota.getOctoberEarlyOctoberIrrigationArea()==null?0.0:irrigationQuota.getOctoberEarlyOctoberIrrigationArea())+
                        (irrigationQuota.getOctoberMidDayIrrigationArea()==null?0.0:irrigationQuota.getOctoberMidDayIrrigationArea())+
                        (irrigationQuota.getOctoberLateOctoberIrrigationArea()==null?0.0:irrigationQuota.getOctoberLateOctoberIrrigationArea())+
                        (irrigationQuota.getNovemberEarlyOctoberIrrigationArea()==null?0.0:irrigationQuota.getNovemberEarlyOctoberIrrigationArea())+
                        (irrigationQuota.getNovemberMidDayIrrigationArea()==null?0.0:irrigationQuota.getNovemberMidDayIrrigationArea())+
                        (irrigationQuota.getNovemberLateOctoberIrrigationArea()==null?0.0:irrigationQuota.getNovemberLateOctoberIrrigationArea())
        );
        irrigationQuota.setAccumulatedTotalIrrigationAmount(
                (irrigationQuota.getAprilEarlyOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getAprilEarlyOctoberIrrigationWaterVolume())+
                        (irrigationQuota.getAprilMidDayIrrigationWaterVolume()==null?0.0:irrigationQuota.getAprilMidDayIrrigationWaterVolume())+
                        (irrigationQuota.getAprilLateOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getAprilLateOctoberIrrigationWaterVolume())+
                        (irrigationQuota.getMayEarlyOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getMayEarlyOctoberIrrigationWaterVolume())+
                        (irrigationQuota.getMayMidDayIrrigationWaterVolume()==null?0.0:irrigationQuota.getMayMidDayIrrigationWaterVolume())+
                        (irrigationQuota.getMayLateOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getMayLateOctoberIrrigationWaterVolume())+
                        (irrigationQuota.getJuneEarlyOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getJuneEarlyOctoberIrrigationWaterVolume())+
                        (irrigationQuota.getJuneMidDayIrrigationWaterVolume()==null?0.0:irrigationQuota.getJuneMidDayIrrigationWaterVolume())+
                        (irrigationQuota.getJuneLateOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getJuneLateOctoberIrrigationWaterVolume())+
                        (irrigationQuota.getJulyEarlyOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getJulyEarlyOctoberIrrigationWaterVolume())+
                        (irrigationQuota.getJulyMidDayIrrigationWaterVolume()==null?0.0:irrigationQuota.getJulyMidDayIrrigationWaterVolume())+
                        (irrigationQuota.getJulyLateOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getJulyLateOctoberIrrigationWaterVolume())+
                        (irrigationQuota.getAugustEarlyOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getAugustEarlyOctoberIrrigationWaterVolume())+
                        (irrigationQuota.getAugustMidDayIrrigationWaterVolume()==null?0.0:irrigationQuota.getAugustMidDayIrrigationWaterVolume())+
                        (irrigationQuota.getAugustLateOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getAugustLateOctoberIrrigationWaterVolume())+
                        (irrigationQuota.getSeptemberEarlyOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getSeptemberEarlyOctoberIrrigationWaterVolume())+
                        (irrigationQuota.getSeptemberMidDayIrrigationWaterVolume()==null?0.0:irrigationQuota.getSeptemberMidDayIrrigationWaterVolume())+
                        (irrigationQuota.getSeptemberLateOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getSeptemberLateOctoberIrrigationWaterVolume())+
                        (irrigationQuota.getOctoberEarlyOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getOctoberEarlyOctoberIrrigationWaterVolume())+
                        (irrigationQuota.getOctoberMidDayIrrigationWaterVolume()==null?0.0:irrigationQuota.getOctoberMidDayIrrigationWaterVolume())+
                        (irrigationQuota.getOctoberLateOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getOctoberLateOctoberIrrigationWaterVolume())+
                        (irrigationQuota.getNovemberEarlyOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getNovemberEarlyOctoberIrrigationWaterVolume())+
                        (irrigationQuota.getNovemberMidDayIrrigationWaterVolume()==null?0.0:irrigationQuota.getNovemberMidDayIrrigationWaterVolume())+
                        (irrigationQuota.getNovemberLateOctoberIrrigationWaterVolume()==null?0.0:irrigationQuota.getNovemberLateOctoberIrrigationWaterVolume())
        );
        boolean b = this.updateById(irrigationQuota);
        if(b){
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse<List<IrrigationQuota>> selectList(IrrigationQuotaListReq req) {
        List<IrrigationQuota> list = this.lambdaQuery().eq(req.getYear() != null, IrrigationQuota::getYear, req.getYear()).
                eq(StringUtils.isNotEmpty(req.getCropType()), IrrigationQuota::getCropType, req.getCropType()).
                eq(StringUtils.isNotEmpty(req.getStation()),IrrigationQuota::getStation,req.getStation()).
                eq(StringUtils.isNotEmpty(req.getWaterUser()), IrrigationQuota::getWaterUser, req.getWaterUser()).
                eq(IrrigationQuota::getDel,0).
                list();
        if(null!=list && list.size()>0) {
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }
}

