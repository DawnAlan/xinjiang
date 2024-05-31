package com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.bean.req.IrrigationQuotaListReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.mapper.IrrigationQuotaMapper;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.entity.IrrigationQuota;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.service.IrrigationQuotaService;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.entity.IrrigationQuotaDetails;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.service.IrrigationQuotaDetailsService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCrop;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 灌溉额度表(IrrigationQuota)表服务实现类
 *
 * @author makejava
 * @since 2023-12-22 12:49:39
 */
@Service("irrigationQuotaService")
public class IrrigationQuotaServiceImpl extends ServiceImpl<IrrigationQuotaMapper, IrrigationQuota> implements IrrigationQuotaService {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private IrrigationQuotaDetailsService irrigationQuotaDetailsService;

    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(List<IrrigationQuota> input) {

        List<String> ids = new ArrayList<>();
        for (IrrigationQuota irrigationQuota :
                input) {
            List<IrrigationQuota> list = this.lambdaQuery().eq(IrrigationQuota::getWaterUser, irrigationQuota.getWaterUser()).
                    eq(IrrigationQuota::getStation, irrigationQuota.getStation()).eq(IrrigationQuota::getYear, irrigationQuota.getYear()).
                    eq(IrrigationQuota::getIrrigationCrop, irrigationQuota.getIrrigationCrop()).eq(IrrigationQuota::getDel, 0).list();
            if (null != list && list.size() > 0) {
                for (int i = 0; i < ids.size(); i++) {
                    delete(ids.get(i));
                }
                return RestResponse.no("该作物已存在，请勿重复添加");
            }
            List<IrrigationQuotaDetails> irrigationQuotaDetailsList = new ArrayList<>();
            irrigationQuota.setId(UUIDUtils.getUUID());
            ids.add(irrigationQuota.getId());
            irrigationQuota.setCreateTime(new Date());
            irrigationQuota.setDel(0);

            irrigationQuota.setAccumulatedIrrigationArea(
                    (irrigationQuota.getAprilEarlyOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getAprilEarlyOctoberIrrigationArea()) +
                            (irrigationQuota.getAprilMidDayIrrigationArea() == null ? 0.0 : irrigationQuota.getAprilMidDayIrrigationArea()) +
                            (irrigationQuota.getAprilLateOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getAprilLateOctoberIrrigationArea()) +
                            (irrigationQuota.getMayEarlyOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getMayEarlyOctoberIrrigationArea()) +
                            (irrigationQuota.getMayMidDayIrrigationArea() == null ? 0.0 : irrigationQuota.getMayMidDayIrrigationArea()) +
                            (irrigationQuota.getMayLateOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getMayLateOctoberIrrigationArea()) +
                            (irrigationQuota.getJuneEarlyOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getJuneEarlyOctoberIrrigationArea()) +
                            (irrigationQuota.getJuneMidDayIrrigationArea() == null ? 0.0 : irrigationQuota.getJuneMidDayIrrigationArea()) +
                            (irrigationQuota.getJuneLateOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getJuneLateOctoberIrrigationArea()) +
                            (irrigationQuota.getJulyEarlyOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getJulyEarlyOctoberIrrigationArea()) +
                            (irrigationQuota.getJulyMidDayIrrigationArea() == null ? 0.0 : irrigationQuota.getJulyMidDayIrrigationArea()) +
                            (irrigationQuota.getJulyLateOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getJulyLateOctoberIrrigationArea()) +
                            (irrigationQuota.getAugustEarlyOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getAugustEarlyOctoberIrrigationArea()) +
                            (irrigationQuota.getAugustMidDayIrrigationArea() == null ? 0.0 : irrigationQuota.getAugustMidDayIrrigationArea()) +
                            (irrigationQuota.getAugustLateOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getAugustLateOctoberIrrigationArea()) +
                            (irrigationQuota.getSeptemberEarlyOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getSeptemberEarlyOctoberIrrigationArea()) +
                            (irrigationQuota.getSeptemberMidDayIrrigationArea() == null ? 0.0 : irrigationQuota.getSeptemberMidDayIrrigationArea()) +
                            (irrigationQuota.getSeptemberLateOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getSeptemberLateOctoberIrrigationArea()) +
                            (irrigationQuota.getOctoberEarlyOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getOctoberEarlyOctoberIrrigationArea()) +
                            (irrigationQuota.getOctoberMidDayIrrigationArea() == null ? 0.0 : irrigationQuota.getOctoberMidDayIrrigationArea()) +
                            (irrigationQuota.getOctoberLateOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getOctoberLateOctoberIrrigationArea()) +
                            (irrigationQuota.getNovemberEarlyOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getNovemberEarlyOctoberIrrigationArea()) +
                            (irrigationQuota.getNovemberMidDayIrrigationArea() == null ? 0.0 : irrigationQuota.getNovemberMidDayIrrigationArea()) +
                            (irrigationQuota.getNovemberLateOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getNovemberLateOctoberIrrigationArea())
            );
            irrigationQuota.setAccumulatedTotalIrrigationTurn(
                    (irrigationQuota.getAprilEarlyOctoberTurn() == null ? 0 : irrigationQuota.getAprilEarlyOctoberTurn()) +
                            (irrigationQuota.getAprilMidDayTurn() == null ? 0 : irrigationQuota.getAprilMidDayTurn()) +
                            (irrigationQuota.getAprilLateOctoberTurn() == null ? 0 : irrigationQuota.getAprilLateOctoberTurn()) +
                            (irrigationQuota.getMayEarlyOctoberTurn() == null ? 0 : irrigationQuota.getMayEarlyOctoberTurn()) +
                            (irrigationQuota.getMayMidDayTurn() == null ? 0 : irrigationQuota.getMayMidDayTurn()) +
                            (irrigationQuota.getMayLateOctoberTurn() == null ? 0 : irrigationQuota.getMayLateOctoberTurn()) +
                            (irrigationQuota.getJuneEarlyOctoberTurn() == null ? 0 : irrigationQuota.getJuneEarlyOctoberTurn()) +
                            (irrigationQuota.getJuneMidDayTurn() == null ? 0 : irrigationQuota.getJuneMidDayTurn()) +
                            (irrigationQuota.getJuneLateOctoberTurn() == null ? 0 : irrigationQuota.getJuneLateOctoberTurn()) +
                            (irrigationQuota.getJulyEarlyOctoberTurn() == null ? 0 : irrigationQuota.getJulyEarlyOctoberTurn()) +
                            (irrigationQuota.getJulyMidDayTurn() == null ? 0 : irrigationQuota.getJulyMidDayTurn()) +
                            (irrigationQuota.getJulyLateOctoberTurn() == null ? 0 : irrigationQuota.getJulyLateOctoberTurn()) +
                            (irrigationQuota.getAugustEarlyOctoberTurn() == null ? 0 : irrigationQuota.getAugustEarlyOctoberTurn()) +
                            (irrigationQuota.getAugustMidDayTurn() == null ? 0 : irrigationQuota.getAugustMidDayTurn()) +
                            (irrigationQuota.getAugustLateOctoberTurn() == null ? 0 : irrigationQuota.getAugustLateOctoberTurn()) +
                            (irrigationQuota.getSeptemberEarlyOctoberTurn() == null ? 0 : irrigationQuota.getSeptemberEarlyOctoberTurn()) +
                            (irrigationQuota.getSeptemberMidDayTurn() == null ? 0 : irrigationQuota.getSeptemberMidDayTurn()) +
                            (irrigationQuota.getSeptemberLateOctoberTurn() == null ? 0 : irrigationQuota.getSeptemberLateOctoberTurn()) +
                            (irrigationQuota.getOctoberEarlyOctoberTurn() == null ? 0 : irrigationQuota.getOctoberEarlyOctoberTurn()) +
                            (irrigationQuota.getOctoberMidDayTurn() == null ? 0 : irrigationQuota.getOctoberMidDayTurn()) +
                            (irrigationQuota.getOctoberLateOctoberTurn() == null ? 0 : irrigationQuota.getOctoberLateOctoberTurn()) +
                            (irrigationQuota.getNovemberEarlyOctoberTurn() == null ? 0 : irrigationQuota.getNovemberEarlyOctoberTurn()) +
                            (irrigationQuota.getNovemberMidDayTurn() == null ? 0 : irrigationQuota.getNovemberMidDayTurn()) +
                            (irrigationQuota.getNovemberLateOctoberTurn() == null ? 0 : irrigationQuota.getNovemberLateOctoberTurn())
            );
            irrigationQuota.setAccumulatedTotalIrrigationAmount(
                    (irrigationQuota.getAprilEarlyOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getAprilEarlyOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getAprilMidDayIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getAprilMidDayIrrigationWaterVolume()) +
                            (irrigationQuota.getAprilLateOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getAprilLateOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getMayEarlyOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getMayEarlyOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getMayMidDayIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getMayMidDayIrrigationWaterVolume()) +
                            (irrigationQuota.getMayLateOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getMayLateOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getJuneEarlyOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getJuneEarlyOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getJuneMidDayIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getJuneMidDayIrrigationWaterVolume()) +
                            (irrigationQuota.getJuneLateOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getJuneLateOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getJulyEarlyOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getJulyEarlyOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getJulyMidDayIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getJulyMidDayIrrigationWaterVolume()) +
                            (irrigationQuota.getJulyLateOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getJulyLateOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getAugustEarlyOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getAugustEarlyOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getAugustMidDayIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getAugustMidDayIrrigationWaterVolume()) +
                            (irrigationQuota.getAugustLateOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getAugustLateOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getSeptemberEarlyOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getSeptemberEarlyOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getSeptemberMidDayIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getSeptemberMidDayIrrigationWaterVolume()) +
                            (irrigationQuota.getSeptemberLateOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getSeptemberLateOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getOctoberEarlyOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getOctoberEarlyOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getOctoberMidDayIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getOctoberMidDayIrrigationWaterVolume()) +
                            (irrigationQuota.getOctoberLateOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getOctoberLateOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getNovemberEarlyOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getNovemberEarlyOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getNovemberMidDayIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getNovemberMidDayIrrigationWaterVolume()) +
                            (irrigationQuota.getNovemberLateOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getNovemberLateOctoberIrrigationWaterVolume())
            );
            irrigationQuota.setIrrigationQuota(irrigationQuota.getTotalPlannedIrrigationArea() == null ? null : irrigationQuota.getAccumulatedTotalIrrigationAmount() / irrigationQuota.getTotalPlannedIrrigationArea());
            irrigationQuota.setAverageIrrigationAmount(irrigationQuota.getAccumulatedIrrigationArea() == 0.0 ? null : (irrigationQuota.getAccumulatedTotalIrrigationAmount() / irrigationQuota.getAccumulatedIrrigationArea())*10000);

            irrigationQuota.setAprilEarlyOctoberId(UUIDUtils.getUUID());
            IrrigationQuotaDetails AprilEarlyOctober = new IrrigationQuotaDetails();
            AprilEarlyOctober.setId(irrigationQuota.getAprilEarlyOctoberId());
            AprilEarlyOctober.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-04-01"));
            AprilEarlyOctober.setStation(irrigationQuota.getStation());
            AprilEarlyOctober.setIrrigationArea(irrigationQuota.getAprilEarlyOctoberIrrigationArea());
            AprilEarlyOctober.setTurn(irrigationQuota.getAprilEarlyOctoberTurn());
            AprilEarlyOctober.setIrrigationWaterVolume(irrigationQuota.getAprilEarlyOctoberIrrigationWaterVolume());
            AprilEarlyOctober.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            AprilEarlyOctober.setWaterUser(irrigationQuota.getWaterUser());
            AprilEarlyOctober.setCropType(irrigationQuota.getCropType());
            AprilEarlyOctober.setTotalId(irrigationQuota.getId());
            AprilEarlyOctober.setYear(irrigationQuota.getYear());
            AprilEarlyOctober.setTenDays("上旬");
            irrigationQuotaDetailsList.add(AprilEarlyOctober);

            irrigationQuota.setAprilMidDayId(UUIDUtils.getUUID());
            IrrigationQuotaDetails AprilMid = new IrrigationQuotaDetails();
            AprilMid.setId(irrigationQuota.getAprilMidDayId());
            AprilMid.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-04-11"));
            AprilMid.setStation(irrigationQuota.getStation());
            AprilMid.setIrrigationArea(irrigationQuota.getAprilMidDayIrrigationArea());
            AprilMid.setTurn(irrigationQuota.getAprilMidDayTurn());
            AprilMid.setIrrigationWaterVolume(irrigationQuota.getAprilMidDayIrrigationWaterVolume());
            AprilMid.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            AprilMid.setWaterUser(irrigationQuota.getWaterUser());
            AprilMid.setCropType(irrigationQuota.getCropType());
            AprilMid.setTotalId(irrigationQuota.getId());
            AprilMid.setYear(irrigationQuota.getYear());
            AprilMid.setTenDays("中旬");
            irrigationQuotaDetailsList.add(AprilMid);

            irrigationQuota.setAprilLateOctoberId(UUIDUtils.getUUID());
            IrrigationQuotaDetails AprilLateOctober = new IrrigationQuotaDetails();
            AprilLateOctober.setId(irrigationQuota.getAprilLateOctoberId());
            AprilLateOctober.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-04-21"));
            AprilLateOctober.setStation(irrigationQuota.getStation());
            AprilLateOctober.setTurn(irrigationQuota.getAprilLateOctoberTurn());
            AprilLateOctober.setIrrigationArea(irrigationQuota.getAprilLateOctoberIrrigationArea());
            AprilLateOctober.setIrrigationWaterVolume(irrigationQuota.getAprilLateOctoberIrrigationWaterVolume());
            AprilLateOctober.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            AprilLateOctober.setWaterUser(irrigationQuota.getWaterUser());
            AprilLateOctober.setCropType(irrigationQuota.getCropType());
            AprilLateOctober.setTotalId(irrigationQuota.getId());
            AprilLateOctober.setYear(irrigationQuota.getYear());
            AprilLateOctober.setTenDays("下旬");
            irrigationQuotaDetailsList.add(AprilLateOctober);


            irrigationQuota.setMayEarlyOctoberId(UUIDUtils.getUUID());
            IrrigationQuotaDetails MayEarlyOctober = new IrrigationQuotaDetails();
            MayEarlyOctober.setId(irrigationQuota.getMayEarlyOctoberId());
            MayEarlyOctober.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-05-01"));
            MayEarlyOctober.setStation(irrigationQuota.getStation());
            MayEarlyOctober.setIrrigationArea(irrigationQuota.getMayEarlyOctoberIrrigationArea());
            MayEarlyOctober.setTurn(irrigationQuota.getMayEarlyOctoberTurn());
            MayEarlyOctober.setIrrigationWaterVolume(irrigationQuota.getMayEarlyOctoberIrrigationWaterVolume());
            MayEarlyOctober.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            MayEarlyOctober.setWaterUser(irrigationQuota.getWaterUser());
            MayEarlyOctober.setCropType(irrigationQuota.getCropType());
            MayEarlyOctober.setTotalId(irrigationQuota.getId());
            MayEarlyOctober.setYear(irrigationQuota.getYear());
            MayEarlyOctober.setTenDays("上旬");
            irrigationQuotaDetailsList.add(MayEarlyOctober);

            irrigationQuota.setMayMidDayId(UUIDUtils.getUUID());
            IrrigationQuotaDetails MayMid = new IrrigationQuotaDetails();
            MayMid.setId(irrigationQuota.getMayMidDayId());
            MayMid.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-05-11"));
            MayMid.setStation(irrigationQuota.getStation());
            MayMid.setIrrigationArea(irrigationQuota.getMayMidDayIrrigationArea());
            MayMid.setTurn(irrigationQuota.getMayMidDayTurn());
            MayMid.setIrrigationWaterVolume(irrigationQuota.getMayMidDayIrrigationWaterVolume());
            MayMid.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            MayMid.setWaterUser(irrigationQuota.getWaterUser());
            MayMid.setCropType(irrigationQuota.getCropType());
            MayMid.setTotalId(irrigationQuota.getId());
            MayMid.setYear(irrigationQuota.getYear());
            MayMid.setTenDays("中旬");
            irrigationQuotaDetailsList.add(MayMid);

            irrigationQuota.setMayLateOctoberId(UUIDUtils.getUUID());
            IrrigationQuotaDetails MayLateOctober = new IrrigationQuotaDetails();
            MayLateOctober.setId(irrigationQuota.getMayLateOctoberId());
            MayLateOctober.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-05-21"));
            MayLateOctober.setStation(irrigationQuota.getStation());
            MayLateOctober.setTurn(irrigationQuota.getMayLateOctoberTurn());
            MayLateOctober.setIrrigationArea(irrigationQuota.getMayLateOctoberIrrigationArea());
            MayLateOctober.setIrrigationWaterVolume(irrigationQuota.getMayLateOctoberIrrigationWaterVolume());
            MayLateOctober.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            MayLateOctober.setWaterUser(irrigationQuota.getWaterUser());
            MayLateOctober.setCropType(irrigationQuota.getCropType());
            MayLateOctober.setTotalId(irrigationQuota.getId());
            MayLateOctober.setYear(irrigationQuota.getYear());
            MayLateOctober.setTenDays("下旬");
            irrigationQuotaDetailsList.add(MayLateOctober);

            irrigationQuota.setJuneEarlyOctoberId(UUIDUtils.getUUID());
            IrrigationQuotaDetails JuneEarlyOctober = new IrrigationQuotaDetails();
            JuneEarlyOctober.setId(irrigationQuota.getJuneEarlyOctoberId());
            JuneEarlyOctober.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-06-01"));
            JuneEarlyOctober.setStation(irrigationQuota.getStation());
            JuneEarlyOctober.setIrrigationArea(irrigationQuota.getJuneEarlyOctoberIrrigationArea());
            JuneEarlyOctober.setTurn(irrigationQuota.getJuneEarlyOctoberTurn());
            JuneEarlyOctober.setIrrigationWaterVolume(irrigationQuota.getJuneEarlyOctoberIrrigationWaterVolume());
            JuneEarlyOctober.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            JuneEarlyOctober.setWaterUser(irrigationQuota.getWaterUser());
            JuneEarlyOctober.setCropType(irrigationQuota.getCropType());
            JuneEarlyOctober.setTotalId(irrigationQuota.getId());
            JuneEarlyOctober.setYear(irrigationQuota.getYear());
            JuneEarlyOctober.setTenDays("上旬");
            irrigationQuotaDetailsList.add(JuneEarlyOctober);

            irrigationQuota.setJuneMidDayId(UUIDUtils.getUUID());
            IrrigationQuotaDetails JuneMid = new IrrigationQuotaDetails();
            JuneMid.setId(irrigationQuota.getJuneMidDayId());
            JuneMid.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-06-11"));
            JuneMid.setStation(irrigationQuota.getStation());
            JuneMid.setIrrigationArea(irrigationQuota.getJuneMidDayIrrigationArea());
            JuneMid.setTurn(irrigationQuota.getJuneMidDayTurn());
            JuneMid.setIrrigationWaterVolume(irrigationQuota.getJuneMidDayIrrigationWaterVolume());
            JuneMid.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            JuneMid.setWaterUser(irrigationQuota.getWaterUser());
            JuneMid.setCropType(irrigationQuota.getCropType());
            JuneMid.setTotalId(irrigationQuota.getId());
            JuneMid.setYear(irrigationQuota.getYear());
            JuneMid.setTenDays("中旬");
            irrigationQuotaDetailsList.add(JuneMid);

            irrigationQuota.setJuneLateOctoberId(UUIDUtils.getUUID());
            IrrigationQuotaDetails JuneLateOctober = new IrrigationQuotaDetails();
            JuneLateOctober.setId(irrigationQuota.getJuneLateOctoberId());
            JuneLateOctober.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-06-21"));
            JuneLateOctober.setStation(irrigationQuota.getStation());
            JuneLateOctober.setIrrigationArea(irrigationQuota.getJuneLateOctoberIrrigationArea());
            JuneLateOctober.setTurn(irrigationQuota.getJuneLateOctoberTurn());
            JuneLateOctober.setIrrigationWaterVolume(irrigationQuota.getJuneLateOctoberIrrigationWaterVolume());
            JuneLateOctober.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            JuneLateOctober.setWaterUser(irrigationQuota.getWaterUser());
            JuneLateOctober.setCropType(irrigationQuota.getCropType());
            JuneLateOctober.setTotalId(irrigationQuota.getId());
            JuneLateOctober.setYear(irrigationQuota.getYear());
            JuneLateOctober.setTenDays("下旬");
            irrigationQuotaDetailsList.add(JuneLateOctober);

            irrigationQuota.setJulyEarlyOctoberId(UUIDUtils.getUUID());
            IrrigationQuotaDetails JulyEarlyOctober = new IrrigationQuotaDetails();
            JulyEarlyOctober.setId(irrigationQuota.getJulyEarlyOctoberId());
            JulyEarlyOctober.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-07-01"));
            JulyEarlyOctober.setStation(irrigationQuota.getStation());
            JulyEarlyOctober.setIrrigationArea(irrigationQuota.getJulyEarlyOctoberIrrigationArea());
            JulyEarlyOctober.setTurn(irrigationQuota.getJulyEarlyOctoberTurn());
            JulyEarlyOctober.setIrrigationWaterVolume(irrigationQuota.getJulyEarlyOctoberIrrigationWaterVolume());
            JulyEarlyOctober.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            JulyEarlyOctober.setWaterUser(irrigationQuota.getWaterUser());
            JulyEarlyOctober.setCropType(irrigationQuota.getCropType());
            JulyEarlyOctober.setTotalId(irrigationQuota.getId());
            JulyEarlyOctober.setYear(irrigationQuota.getYear());
            JulyEarlyOctober.setTenDays("上旬");
            irrigationQuotaDetailsList.add(JulyEarlyOctober);

            irrigationQuota.setJulyMidDayId(UUIDUtils.getUUID());
            IrrigationQuotaDetails JulyMid = new IrrigationQuotaDetails();
            JulyMid.setId(irrigationQuota.getJulyMidDayId());
            JulyMid.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-07-11"));
            JulyMid.setStation(irrigationQuota.getStation());
            JulyMid.setIrrigationArea(irrigationQuota.getJulyMidDayIrrigationArea());
            JulyMid.setTurn(irrigationQuota.getJulyMidDayTurn());
            JulyMid.setIrrigationWaterVolume(irrigationQuota.getJulyMidDayIrrigationWaterVolume());
            JulyMid.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            JulyMid.setWaterUser(irrigationQuota.getWaterUser());
            JulyMid.setCropType(irrigationQuota.getCropType());
            JulyMid.setTotalId(irrigationQuota.getId());
            JulyMid.setYear(irrigationQuota.getYear());
            JulyMid.setTenDays("中旬");
            irrigationQuotaDetailsList.add(JulyMid);

            irrigationQuota.setJulyLateOctoberId(UUIDUtils.getUUID());
            IrrigationQuotaDetails JulyLateOctober = new IrrigationQuotaDetails();
            JulyLateOctober.setId(irrigationQuota.getJulyLateOctoberId());
            JulyLateOctober.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-07-21"));
            JulyLateOctober.setStation(irrigationQuota.getStation());
            JulyLateOctober.setIrrigationArea(irrigationQuota.getJulyLateOctoberIrrigationArea());
            JulyLateOctober.setTurn(irrigationQuota.getJulyLateOctoberTurn());
            JulyLateOctober.setIrrigationWaterVolume(irrigationQuota.getJulyLateOctoberIrrigationWaterVolume());
            JulyLateOctober.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            JulyLateOctober.setWaterUser(irrigationQuota.getWaterUser());
            JulyLateOctober.setCropType(irrigationQuota.getCropType());
            JulyLateOctober.setTotalId(irrigationQuota.getId());
            JulyLateOctober.setYear(irrigationQuota.getYear());
            JulyLateOctober.setTenDays("下旬");
            irrigationQuotaDetailsList.add(JulyLateOctober);

            irrigationQuota.setAugustEarlyOctoberId(UUIDUtils.getUUID());
            IrrigationQuotaDetails AugustEarlyOctober = new IrrigationQuotaDetails();
            AugustEarlyOctober.setId(irrigationQuota.getAugustEarlyOctoberId());
            AugustEarlyOctober.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-08-01"));
            AugustEarlyOctober.setStation(irrigationQuota.getStation());
            AugustEarlyOctober.setIrrigationArea(irrigationQuota.getAugustEarlyOctoberIrrigationArea());
            AugustEarlyOctober.setTurn(irrigationQuota.getAprilEarlyOctoberTurn());
            AugustEarlyOctober.setIrrigationWaterVolume(irrigationQuota.getAugustEarlyOctoberIrrigationWaterVolume());
            AugustEarlyOctober.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            AugustEarlyOctober.setWaterUser(irrigationQuota.getWaterUser());
            AugustEarlyOctober.setCropType(irrigationQuota.getCropType());
            AugustEarlyOctober.setTotalId(irrigationQuota.getId());
            AugustEarlyOctober.setYear(irrigationQuota.getYear());
            AugustEarlyOctober.setTenDays("上旬");
            irrigationQuotaDetailsList.add(AugustEarlyOctober);

            irrigationQuota.setAugustMidDayId(UUIDUtils.getUUID());
            IrrigationQuotaDetails AugustMid = new IrrigationQuotaDetails();
            AugustMid.setId(irrigationQuota.getAugustMidDayId());
            AugustMid.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-08-11"));
            AugustMid.setStation(irrigationQuota.getStation());
            AugustMid.setIrrigationArea(irrigationQuota.getAugustMidDayIrrigationArea());
            AugustMid.setTurn(irrigationQuota.getAugustMidDayTurn());
            AugustMid.setIrrigationWaterVolume(irrigationQuota.getAugustMidDayIrrigationWaterVolume());
            AugustMid.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            AugustMid.setWaterUser(irrigationQuota.getWaterUser());
            AugustMid.setCropType(irrigationQuota.getCropType());
            AugustMid.setTotalId(irrigationQuota.getId());
            AugustMid.setYear(irrigationQuota.getYear());
            AugustMid.setTenDays("中旬");
            irrigationQuotaDetailsList.add(AugustMid);

            irrigationQuota.setAugustLateOctoberId(UUIDUtils.getUUID());
            IrrigationQuotaDetails AugustLateOctober = new IrrigationQuotaDetails();
            AugustLateOctober.setId(irrigationQuota.getAugustLateOctoberId());
            AugustLateOctober.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-08-21"));
            AugustLateOctober.setStation(irrigationQuota.getStation());
            AugustLateOctober.setIrrigationArea(irrigationQuota.getAugustLateOctoberIrrigationArea());
            AugustLateOctober.setTurn(irrigationQuota.getAugustLateOctoberTurn());
            AugustLateOctober.setIrrigationWaterVolume(irrigationQuota.getAugustLateOctoberIrrigationWaterVolume());
            AugustLateOctober.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            AugustLateOctober.setWaterUser(irrigationQuota.getWaterUser());
            AugustLateOctober.setCropType(irrigationQuota.getCropType());
            AugustLateOctober.setTotalId(irrigationQuota.getId());
            AugustLateOctober.setYear(irrigationQuota.getYear());
            AugustLateOctober.setTenDays("下旬");
            irrigationQuotaDetailsList.add(AugustLateOctober);

            irrigationQuota.setSeptemberEarlyOctoberId(UUIDUtils.getUUID());
            IrrigationQuotaDetails SeptemberEarlyOctober = new IrrigationQuotaDetails();
            SeptemberEarlyOctober.setId(irrigationQuota.getSeptemberEarlyOctoberId());
            SeptemberEarlyOctober.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-09-01"));
            SeptemberEarlyOctober.setStation(irrigationQuota.getStation());
            SeptemberEarlyOctober.setIrrigationArea(irrigationQuota.getSeptemberEarlyOctoberIrrigationArea());
            SeptemberEarlyOctober.setTurn(irrigationQuota.getSeptemberEarlyOctoberTurn());
            SeptemberEarlyOctober.setIrrigationWaterVolume(irrigationQuota.getSeptemberEarlyOctoberIrrigationWaterVolume());
            SeptemberEarlyOctober.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            SeptemberEarlyOctober.setWaterUser(irrigationQuota.getWaterUser());
            SeptemberEarlyOctober.setCropType(irrigationQuota.getCropType());
            SeptemberEarlyOctober.setTotalId(irrigationQuota.getId());
            SeptemberEarlyOctober.setYear(irrigationQuota.getYear());
            SeptemberEarlyOctober.setTenDays("上旬");
            irrigationQuotaDetailsList.add(SeptemberEarlyOctober);

            irrigationQuota.setSeptemberMidDayId(UUIDUtils.getUUID());
            IrrigationQuotaDetails SeptemberMid = new IrrigationQuotaDetails();
            SeptemberMid.setId(irrigationQuota.getSeptemberMidDayId());
            SeptemberMid.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-09-11"));
            SeptemberMid.setStation(irrigationQuota.getStation());
            SeptemberMid.setIrrigationArea(irrigationQuota.getSeptemberMidDayIrrigationArea());
            SeptemberMid.setTurn(irrigationQuota.getSeptemberMidDayTurn());
            SeptemberMid.setIrrigationWaterVolume(irrigationQuota.getSeptemberMidDayIrrigationWaterVolume());
            SeptemberMid.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            SeptemberMid.setWaterUser(irrigationQuota.getWaterUser());
            SeptemberMid.setCropType(irrigationQuota.getCropType());
            SeptemberMid.setTotalId(irrigationQuota.getId());
            SeptemberMid.setYear(irrigationQuota.getYear());
            SeptemberMid.setTenDays("中旬");
            irrigationQuotaDetailsList.add(SeptemberMid);

            irrigationQuota.setSeptemberLateOctoberId(UUIDUtils.getUUID());
            IrrigationQuotaDetails SeptemberLateOctober = new IrrigationQuotaDetails();
            SeptemberLateOctober.setId(irrigationQuota.getSeptemberLateOctoberId());
            SeptemberLateOctober.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-09-21"));
            SeptemberLateOctober.setStation(irrigationQuota.getStation());
            SeptemberLateOctober.setIrrigationArea(irrigationQuota.getSeptemberLateOctoberIrrigationArea());
            SeptemberLateOctober.setTurn(irrigationQuota.getSeptemberLateOctoberTurn());
            SeptemberLateOctober.setIrrigationWaterVolume(irrigationQuota.getSeptemberLateOctoberIrrigationWaterVolume());
            SeptemberLateOctober.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            SeptemberLateOctober.setWaterUser(irrigationQuota.getWaterUser());
            SeptemberLateOctober.setCropType(irrigationQuota.getCropType());
            SeptemberLateOctober.setTotalId(irrigationQuota.getId());
            SeptemberLateOctober.setYear(irrigationQuota.getYear());
            SeptemberLateOctober.setTenDays("下旬");
            irrigationQuotaDetailsList.add(SeptemberLateOctober);

            irrigationQuota.setOctoberEarlyOctoberId(UUIDUtils.getUUID());
            IrrigationQuotaDetails OctoberEarlyOctober = new IrrigationQuotaDetails();
            OctoberEarlyOctober.setId(irrigationQuota.getOctoberEarlyOctoberId());
            OctoberEarlyOctober.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-10-01"));
            OctoberEarlyOctober.setStation(irrigationQuota.getStation());
            OctoberEarlyOctober.setIrrigationArea(irrigationQuota.getOctoberEarlyOctoberIrrigationArea());
            OctoberEarlyOctober.setTurn(irrigationQuota.getOctoberEarlyOctoberTurn());
            OctoberEarlyOctober.setIrrigationWaterVolume(irrigationQuota.getOctoberEarlyOctoberIrrigationWaterVolume());
            OctoberEarlyOctober.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            OctoberEarlyOctober.setWaterUser(irrigationQuota.getWaterUser());
            OctoberEarlyOctober.setCropType(irrigationQuota.getCropType());
            OctoberEarlyOctober.setTotalId(irrigationQuota.getId());
            OctoberEarlyOctober.setYear(irrigationQuota.getYear());
            OctoberEarlyOctober.setTenDays("上旬");
            irrigationQuotaDetailsList.add(OctoberEarlyOctober);

            irrigationQuota.setOctoberMidDayId(UUIDUtils.getUUID());
            IrrigationQuotaDetails OctoberMid = new IrrigationQuotaDetails();
            OctoberMid.setId(irrigationQuota.getOctoberMidDayId());
            OctoberMid.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-10-11"));
            OctoberMid.setStation(irrigationQuota.getStation());
            OctoberMid.setIrrigationArea(irrigationQuota.getOctoberMidDayIrrigationArea());
            OctoberMid.setTurn(irrigationQuota.getOctoberMidDayTurn());
            OctoberMid.setIrrigationWaterVolume(irrigationQuota.getOctoberMidDayIrrigationWaterVolume());
            OctoberMid.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            OctoberMid.setWaterUser(irrigationQuota.getWaterUser());
            OctoberMid.setCropType(irrigationQuota.getCropType());
            OctoberMid.setTotalId(irrigationQuota.getId());
            OctoberMid.setYear(irrigationQuota.getYear());
            OctoberMid.setTenDays("中旬");
            irrigationQuotaDetailsList.add(OctoberMid);

            irrigationQuota.setOctoberLateOctoberId(UUIDUtils.getUUID());
            IrrigationQuotaDetails OctoberLateOctober = new IrrigationQuotaDetails();
            OctoberLateOctober.setId(irrigationQuota.getOctoberLateOctoberId());
            OctoberLateOctober.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-10-21"));
            OctoberLateOctober.setStation(irrigationQuota.getStation());
            OctoberLateOctober.setIrrigationArea(irrigationQuota.getOctoberLateOctoberIrrigationArea());
            OctoberLateOctober.setTurn(irrigationQuota.getOctoberLateOctoberTurn());
            OctoberLateOctober.setIrrigationWaterVolume(irrigationQuota.getOctoberLateOctoberIrrigationWaterVolume());
            OctoberLateOctober.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            OctoberLateOctober.setWaterUser(irrigationQuota.getWaterUser());
            OctoberLateOctober.setCropType(irrigationQuota.getCropType());
            OctoberLateOctober.setTotalId(irrigationQuota.getId());
            OctoberLateOctober.setYear(irrigationQuota.getYear());
            OctoberLateOctober.setTenDays("下旬");
            irrigationQuotaDetailsList.add(OctoberLateOctober);

            irrigationQuota.setNovemberEarlyOctoberId(UUIDUtils.getUUID());
            IrrigationQuotaDetails NovemberEarlyOctober = new IrrigationQuotaDetails();
            NovemberEarlyOctober.setId(irrigationQuota.getNovemberEarlyOctoberId());
            NovemberEarlyOctober.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-11-01"));
            NovemberEarlyOctober.setStation(irrigationQuota.getStation());
            NovemberEarlyOctober.setIrrigationArea(irrigationQuota.getNovemberEarlyOctoberIrrigationArea());
            NovemberEarlyOctober.setTurn(irrigationQuota.getNovemberEarlyOctoberTurn());
            NovemberEarlyOctober.setIrrigationWaterVolume(irrigationQuota.getNovemberEarlyOctoberIrrigationWaterVolume());
            NovemberEarlyOctober.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            NovemberEarlyOctober.setWaterUser(irrigationQuota.getWaterUser());
            NovemberEarlyOctober.setCropType(irrigationQuota.getCropType());
            NovemberEarlyOctober.setTotalId(irrigationQuota.getId());
            NovemberEarlyOctober.setYear(irrigationQuota.getYear());
            NovemberEarlyOctober.setTenDays("上旬");
            irrigationQuotaDetailsList.add(NovemberEarlyOctober);

            irrigationQuota.setNovemberMidDayId(UUIDUtils.getUUID());
            IrrigationQuotaDetails NovemberMid = new IrrigationQuotaDetails();
            NovemberMid.setId(irrigationQuota.getNovemberMidDayId());
            NovemberMid.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-11-11"));
            NovemberMid.setStation(irrigationQuota.getStation());
            NovemberMid.setIrrigationArea(irrigationQuota.getNovemberMidDayIrrigationArea());
            NovemberMid.setTurn(irrigationQuota.getNovemberMidDayTurn());
            NovemberMid.setIrrigationWaterVolume(irrigationQuota.getNovemberMidDayIrrigationWaterVolume());
            NovemberMid.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            NovemberMid.setWaterUser(irrigationQuota.getWaterUser());
            NovemberMid.setCropType(irrigationQuota.getCropType());
            NovemberMid.setTotalId(irrigationQuota.getId());
            NovemberMid.setYear(irrigationQuota.getYear());
            NovemberMid.setTenDays("中旬");
            irrigationQuotaDetailsList.add(NovemberMid);

            irrigationQuota.setNovemberLateOctoberId(UUIDUtils.getUUID());
            IrrigationQuotaDetails NovemberLateOctober = new IrrigationQuotaDetails();
            NovemberLateOctober.setId(irrigationQuota.getNovemberLateOctoberId());
            NovemberLateOctober.setCreateTime(sdf.parse(irrigationQuota.getYear() + "-11-21"));
            NovemberLateOctober.setStation(irrigationQuota.getStation());
            NovemberLateOctober.setIrrigationArea(irrigationQuota.getNovemberLateOctoberIrrigationArea());
            NovemberLateOctober.setTurn(irrigationQuota.getNovemberLateOctoberTurn());
            NovemberLateOctober.setIrrigationWaterVolume(irrigationQuota.getNovemberLateOctoberIrrigationWaterVolume());
            NovemberLateOctober.setIrrigationCrop(irrigationQuota.getIrrigationCrop());
            NovemberLateOctober.setWaterUser(irrigationQuota.getWaterUser());
            NovemberLateOctober.setCropType(irrigationQuota.getCropType());
            NovemberLateOctober.setTotalId(irrigationQuota.getId());
            NovemberLateOctober.setYear(irrigationQuota.getYear());
            NovemberLateOctober.setTenDays("下旬");
            irrigationQuotaDetailsList.add(NovemberLateOctober);
            boolean save = this.save(irrigationQuota);
            if (save) {
                irrigationQuotaDetailsService.saveBatch(irrigationQuotaDetailsList);
            } else {
                for (int i = 0; i < ids.size(); i++) {
                    delete(ids.get(i));
                }
                return RestResponse.no("error");
            }
        }
        return RestResponse.ok();
    }

    @Override
    public RestResponse delete(String id) {
        boolean update = this.lambdaUpdate().set(IrrigationQuota::getDel, 1).eq(IrrigationQuota::getId, id).update();
        boolean remove = irrigationQuotaDetailsService.lambdaUpdate().eq(IrrigationQuotaDetails::getTotalId, id).remove();
        if (update && remove) {
            return RestResponse.ok();
        } else {
            return RestResponse.no("error");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @SneakyThrows
    public RestResponse update(List<IrrigationQuota> input) {
        for(IrrigationQuota irrigationQuota : input) {
            irrigationQuota.setAccumulatedIrrigationArea(
                    (irrigationQuota.getAprilEarlyOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getAprilEarlyOctoberIrrigationArea()) +
                            (irrigationQuota.getAprilMidDayIrrigationArea() == null ? 0.0 : irrigationQuota.getAprilMidDayIrrigationArea()) +
                            (irrigationQuota.getAprilLateOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getAprilLateOctoberIrrigationArea()) +
                            (irrigationQuota.getMayEarlyOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getMayEarlyOctoberIrrigationArea()) +
                            (irrigationQuota.getMayMidDayIrrigationArea() == null ? 0.0 : irrigationQuota.getMayMidDayIrrigationArea()) +
                            (irrigationQuota.getMayLateOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getMayLateOctoberIrrigationArea()) +
                            (irrigationQuota.getJuneEarlyOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getJuneEarlyOctoberIrrigationArea()) +
                            (irrigationQuota.getJuneMidDayIrrigationArea() == null ? 0.0 : irrigationQuota.getJuneMidDayIrrigationArea()) +
                            (irrigationQuota.getJuneLateOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getJuneLateOctoberIrrigationArea()) +
                            (irrigationQuota.getJulyEarlyOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getJulyEarlyOctoberIrrigationArea()) +
                            (irrigationQuota.getJulyMidDayIrrigationArea() == null ? 0.0 : irrigationQuota.getJulyMidDayIrrigationArea()) +
                            (irrigationQuota.getJulyLateOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getJulyLateOctoberIrrigationArea()) +
                            (irrigationQuota.getAugustEarlyOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getAugustEarlyOctoberIrrigationArea()) +
                            (irrigationQuota.getAugustMidDayIrrigationArea() == null ? 0.0 : irrigationQuota.getAugustMidDayIrrigationArea()) +
                            (irrigationQuota.getAugustLateOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getAugustLateOctoberIrrigationArea()) +
                            (irrigationQuota.getSeptemberEarlyOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getSeptemberEarlyOctoberIrrigationArea()) +
                            (irrigationQuota.getSeptemberMidDayIrrigationArea() == null ? 0.0 : irrigationQuota.getSeptemberMidDayIrrigationArea()) +
                            (irrigationQuota.getSeptemberLateOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getSeptemberLateOctoberIrrigationArea()) +
                            (irrigationQuota.getOctoberEarlyOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getOctoberEarlyOctoberIrrigationArea()) +
                            (irrigationQuota.getOctoberMidDayIrrigationArea() == null ? 0.0 : irrigationQuota.getOctoberMidDayIrrigationArea()) +
                            (irrigationQuota.getOctoberLateOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getOctoberLateOctoberIrrigationArea()) +
                            (irrigationQuota.getNovemberEarlyOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getNovemberEarlyOctoberIrrigationArea()) +
                            (irrigationQuota.getNovemberMidDayIrrigationArea() == null ? 0.0 : irrigationQuota.getNovemberMidDayIrrigationArea()) +
                            (irrigationQuota.getNovemberLateOctoberIrrigationArea() == null ? 0.0 : irrigationQuota.getNovemberLateOctoberIrrigationArea())
            );
            irrigationQuota.setAccumulatedTotalIrrigationTurn(
                    (irrigationQuota.getAprilEarlyOctoberTurn() == null ? 0 : irrigationQuota.getAprilEarlyOctoberTurn()) +
                            (irrigationQuota.getAprilMidDayTurn() == null ? 0 : irrigationQuota.getAprilMidDayTurn()) +
                            (irrigationQuota.getAprilLateOctoberTurn() == null ? 0 : irrigationQuota.getAprilLateOctoberTurn()) +
                            (irrigationQuota.getMayEarlyOctoberTurn() == null ? 0 : irrigationQuota.getMayEarlyOctoberTurn()) +
                            (irrigationQuota.getMayMidDayTurn() == null ? 0 : irrigationQuota.getMayMidDayTurn()) +
                            (irrigationQuota.getMayLateOctoberTurn() == null ? 0 : irrigationQuota.getMayLateOctoberTurn()) +
                            (irrigationQuota.getJuneEarlyOctoberTurn() == null ? 0 : irrigationQuota.getJuneEarlyOctoberTurn()) +
                            (irrigationQuota.getJuneMidDayTurn() == null ? 0 : irrigationQuota.getJuneMidDayTurn()) +
                            (irrigationQuota.getJuneLateOctoberTurn() == null ? 0 : irrigationQuota.getJuneLateOctoberTurn()) +
                            (irrigationQuota.getJulyEarlyOctoberTurn() == null ? 0 : irrigationQuota.getJulyEarlyOctoberTurn()) +
                            (irrigationQuota.getJulyMidDayTurn() == null ? 0 : irrigationQuota.getJulyMidDayTurn()) +
                            (irrigationQuota.getJulyLateOctoberTurn() == null ? 0 : irrigationQuota.getJulyLateOctoberTurn()) +
                            (irrigationQuota.getAugustEarlyOctoberTurn() == null ? 0 : irrigationQuota.getAugustEarlyOctoberTurn()) +
                            (irrigationQuota.getAugustMidDayTurn() == null ? 0 : irrigationQuota.getAugustMidDayTurn()) +
                            (irrigationQuota.getAugustLateOctoberTurn() == null ? 0 : irrigationQuota.getAugustLateOctoberTurn()) +
                            (irrigationQuota.getSeptemberEarlyOctoberTurn() == null ? 0 : irrigationQuota.getSeptemberEarlyOctoberTurn()) +
                            (irrigationQuota.getSeptemberMidDayTurn() == null ? 0 : irrigationQuota.getSeptemberMidDayTurn()) +
                            (irrigationQuota.getSeptemberLateOctoberTurn() == null ? 0 : irrigationQuota.getSeptemberLateOctoberTurn()) +
                            (irrigationQuota.getOctoberEarlyOctoberTurn() == null ? 0 : irrigationQuota.getOctoberEarlyOctoberTurn()) +
                            (irrigationQuota.getOctoberMidDayTurn() == null ? 0 : irrigationQuota.getOctoberMidDayTurn()) +
                            (irrigationQuota.getOctoberLateOctoberTurn() == null ? 0 : irrigationQuota.getOctoberLateOctoberTurn()) +
                            (irrigationQuota.getNovemberEarlyOctoberTurn() == null ? 0 : irrigationQuota.getNovemberEarlyOctoberTurn()) +
                            (irrigationQuota.getNovemberMidDayTurn() == null ? 0 : irrigationQuota.getNovemberMidDayTurn()) +
                            (irrigationQuota.getNovemberLateOctoberTurn() == null ? 0 : irrigationQuota.getNovemberLateOctoberTurn())
            );
            irrigationQuota.setAccumulatedTotalIrrigationAmount(
                    (irrigationQuota.getAprilEarlyOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getAprilEarlyOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getAprilMidDayIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getAprilMidDayIrrigationWaterVolume()) +
                            (irrigationQuota.getAprilLateOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getAprilLateOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getMayEarlyOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getMayEarlyOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getMayMidDayIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getMayMidDayIrrigationWaterVolume()) +
                            (irrigationQuota.getMayLateOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getMayLateOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getJuneEarlyOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getJuneEarlyOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getJuneMidDayIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getJuneMidDayIrrigationWaterVolume()) +
                            (irrigationQuota.getJuneLateOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getJuneLateOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getJulyEarlyOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getJulyEarlyOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getJulyMidDayIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getJulyMidDayIrrigationWaterVolume()) +
                            (irrigationQuota.getJulyLateOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getJulyLateOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getAugustEarlyOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getAugustEarlyOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getAugustMidDayIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getAugustMidDayIrrigationWaterVolume()) +
                            (irrigationQuota.getAugustLateOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getAugustLateOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getSeptemberEarlyOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getSeptemberEarlyOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getSeptemberMidDayIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getSeptemberMidDayIrrigationWaterVolume()) +
                            (irrigationQuota.getSeptemberLateOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getSeptemberLateOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getOctoberEarlyOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getOctoberEarlyOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getOctoberMidDayIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getOctoberMidDayIrrigationWaterVolume()) +
                            (irrigationQuota.getOctoberLateOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getOctoberLateOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getNovemberEarlyOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getNovemberEarlyOctoberIrrigationWaterVolume()) +
                            (irrigationQuota.getNovemberMidDayIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getNovemberMidDayIrrigationWaterVolume()) +
                            (irrigationQuota.getNovemberLateOctoberIrrigationWaterVolume() == null ? 0.0 : irrigationQuota.getNovemberLateOctoberIrrigationWaterVolume())
            );
            irrigationQuota.setIrrigationQuota(irrigationQuota.getTotalPlannedIrrigationArea() == null ? null : irrigationQuota.getAccumulatedTotalIrrigationAmount() / irrigationQuota.getTotalPlannedIrrigationArea());
            irrigationQuota.setAverageIrrigationAmount(irrigationQuota.getAccumulatedIrrigationArea() == 0.0 ? null : (irrigationQuota.getAccumulatedTotalIrrigationAmount() / irrigationQuota.getAccumulatedIrrigationArea()));
        }

        Boolean update = this.updateBatchById(input);
        if (update) {
            List<IrrigationQuotaDetails> irrigationQuotaDetailsList = new ArrayList<>();
            for(IrrigationQuota irrigationQuota : input){
                IrrigationQuotaDetails AprilEarlyOctober = new IrrigationQuotaDetails();
                AprilEarlyOctober.setId(irrigationQuota.getAprilEarlyOctoberId());
                AprilEarlyOctober.setTurn(irrigationQuota.getAprilEarlyOctoberTurn());
                AprilEarlyOctober.setIrrigationArea(irrigationQuota.getAprilEarlyOctoberIrrigationArea());
                AprilEarlyOctober.setIrrigationWaterVolume(irrigationQuota.getAprilEarlyOctoberIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(AprilEarlyOctober);

                IrrigationQuotaDetails AprilMid = new IrrigationQuotaDetails();
                AprilMid.setId(irrigationQuota.getAprilMidDayId());
                AprilMid.setTurn(irrigationQuota.getAprilMidDayTurn());
                AprilMid.setIrrigationArea(irrigationQuota.getAprilMidDayIrrigationArea());
                AprilMid.setIrrigationWaterVolume(irrigationQuota.getAprilMidDayIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(AprilMid);

                IrrigationQuotaDetails AprilLateOctober = new IrrigationQuotaDetails();
                AprilLateOctober.setId(irrigationQuota.getAprilLateOctoberId());
                AprilLateOctober.setTurn(irrigationQuota.getAprilLateOctoberTurn());
                AprilLateOctober.setIrrigationArea(irrigationQuota.getAprilLateOctoberIrrigationArea());
                AprilLateOctober.setIrrigationWaterVolume(irrigationQuota.getAprilLateOctoberIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(AprilLateOctober);


                IrrigationQuotaDetails MayEarlyOctober = new IrrigationQuotaDetails();
                MayEarlyOctober.setId(irrigationQuota.getMayEarlyOctoberId());
                MayEarlyOctober.setTurn(irrigationQuota.getMayEarlyOctoberTurn());
                MayEarlyOctober.setIrrigationArea(irrigationQuota.getMayEarlyOctoberIrrigationArea());
                MayEarlyOctober.setIrrigationWaterVolume(irrigationQuota.getMayEarlyOctoberIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(MayEarlyOctober);

                IrrigationQuotaDetails MayMid = new IrrigationQuotaDetails();
                MayMid.setId(irrigationQuota.getMayMidDayId());
                MayMid.setTurn(irrigationQuota.getMayMidDayTurn());
                MayMid.setIrrigationArea(irrigationQuota.getMayMidDayIrrigationArea());
                MayMid.setIrrigationWaterVolume(irrigationQuota.getMayMidDayIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(MayMid);

                IrrigationQuotaDetails MayLateOctober = new IrrigationQuotaDetails();
                MayLateOctober.setId(irrigationQuota.getMayLateOctoberId());
                MayLateOctober.setTurn(irrigationQuota.getMayLateOctoberTurn());
                MayLateOctober.setIrrigationArea(irrigationQuota.getMayLateOctoberIrrigationArea());
                MayLateOctober.setIrrigationWaterVolume(irrigationQuota.getMayLateOctoberIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(MayLateOctober);

                IrrigationQuotaDetails JuneEarlyOctober = new IrrigationQuotaDetails();
                JuneEarlyOctober.setId(irrigationQuota.getJuneEarlyOctoberId());
                JuneEarlyOctober.setTurn(irrigationQuota.getJuneEarlyOctoberTurn());
                JuneEarlyOctober.setIrrigationArea(irrigationQuota.getJuneEarlyOctoberIrrigationArea());
                JuneEarlyOctober.setIrrigationWaterVolume(irrigationQuota.getJuneEarlyOctoberIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(JuneEarlyOctober);

                IrrigationQuotaDetails JuneMid = new IrrigationQuotaDetails();
                JuneMid.setId(irrigationQuota.getJuneMidDayId());
                JuneMid.setTurn(irrigationQuota.getJuneMidDayTurn());
                JuneMid.setIrrigationArea(irrigationQuota.getJuneMidDayIrrigationArea());
                JuneMid.setIrrigationWaterVolume(irrigationQuota.getJuneMidDayIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(JuneMid);

                IrrigationQuotaDetails JuneLateOctober = new IrrigationQuotaDetails();
                JuneLateOctober.setId(irrigationQuota.getJuneLateOctoberId());
                JuneLateOctober.setTurn(irrigationQuota.getJuneLateOctoberTurn());
                JuneLateOctober.setIrrigationArea(irrigationQuota.getJuneLateOctoberIrrigationArea());
                JuneLateOctober.setIrrigationWaterVolume(irrigationQuota.getJuneLateOctoberIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(JuneLateOctober);

                IrrigationQuotaDetails JulyEarlyOctober = new IrrigationQuotaDetails();
                JulyEarlyOctober.setId(irrigationQuota.getJulyEarlyOctoberId());
                JulyEarlyOctober.setTurn(irrigationQuota.getJulyEarlyOctoberTurn());
                JulyEarlyOctober.setIrrigationArea(irrigationQuota.getJulyEarlyOctoberIrrigationArea());
                JulyEarlyOctober.setIrrigationWaterVolume(irrigationQuota.getJulyEarlyOctoberIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(JulyEarlyOctober);

                IrrigationQuotaDetails JulyMid = new IrrigationQuotaDetails();
                JulyMid.setId(irrigationQuota.getJulyMidDayId());
                JulyMid.setTurn(irrigationQuota.getJulyMidDayTurn());
                JulyMid.setIrrigationArea(irrigationQuota.getJulyMidDayIrrigationArea());
                JulyMid.setIrrigationWaterVolume(irrigationQuota.getJulyMidDayIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(JulyMid);

                IrrigationQuotaDetails JulyLateOctober = new IrrigationQuotaDetails();
                JulyLateOctober.setId(irrigationQuota.getJulyLateOctoberId());
                JulyLateOctober.setTurn(irrigationQuota.getJulyLateOctoberTurn());
                JulyLateOctober.setIrrigationArea(irrigationQuota.getJulyLateOctoberIrrigationArea());
                JulyLateOctober.setIrrigationWaterVolume(irrigationQuota.getJulyLateOctoberIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(JulyLateOctober);

                IrrigationQuotaDetails AugustEarlyOctober = new IrrigationQuotaDetails();
                AugustEarlyOctober.setId(irrigationQuota.getAugustEarlyOctoberId());
                AugustEarlyOctober.setTurn(irrigationQuota.getAprilEarlyOctoberTurn());
                AugustEarlyOctober.setIrrigationArea(irrigationQuota.getAugustEarlyOctoberIrrigationArea());
                AugustEarlyOctober.setIrrigationWaterVolume(irrigationQuota.getAugustEarlyOctoberIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(AugustEarlyOctober);

                IrrigationQuotaDetails AugustMid = new IrrigationQuotaDetails();
                AugustMid.setId(irrigationQuota.getAugustMidDayId());
                AugustMid.setTurn(irrigationQuota.getAugustMidDayTurn());
                AugustMid.setIrrigationArea(irrigationQuota.getAugustMidDayIrrigationArea());
                AugustMid.setIrrigationWaterVolume(irrigationQuota.getAugustMidDayIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(AugustMid);

                IrrigationQuotaDetails AugustLateOctober = new IrrigationQuotaDetails();
                AugustLateOctober.setId(irrigationQuota.getAugustLateOctoberId());
                AugustLateOctober.setTurn(irrigationQuota.getAugustLateOctoberTurn());
                AugustLateOctober.setIrrigationArea(irrigationQuota.getAugustLateOctoberIrrigationArea());
                AugustLateOctober.setIrrigationWaterVolume(irrigationQuota.getAugustLateOctoberIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(AugustLateOctober);

                IrrigationQuotaDetails SeptemberEarlyOctober = new IrrigationQuotaDetails();
                SeptemberEarlyOctober.setId(irrigationQuota.getSeptemberEarlyOctoberId());
                SeptemberEarlyOctober.setTurn(irrigationQuota.getSeptemberEarlyOctoberTurn());
                SeptemberEarlyOctober.setIrrigationArea(irrigationQuota.getSeptemberEarlyOctoberIrrigationArea());
                SeptemberEarlyOctober.setIrrigationWaterVolume(irrigationQuota.getSeptemberEarlyOctoberIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(SeptemberEarlyOctober);

                IrrigationQuotaDetails SeptemberMid = new IrrigationQuotaDetails();
                SeptemberMid.setId(irrigationQuota.getSeptemberMidDayId());
                SeptemberMid.setTurn(irrigationQuota.getSeptemberMidDayTurn());
                SeptemberMid.setIrrigationArea(irrigationQuota.getSeptemberMidDayIrrigationArea());
                SeptemberMid.setIrrigationWaterVolume(irrigationQuota.getSeptemberMidDayIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(SeptemberMid);

                IrrigationQuotaDetails SeptemberLateOctober = new IrrigationQuotaDetails();
                SeptemberLateOctober.setId(irrigationQuota.getSeptemberLateOctoberId());
                SeptemberLateOctober.setTurn(irrigationQuota.getSeptemberLateOctoberTurn());
                SeptemberLateOctober.setIrrigationArea(irrigationQuota.getSeptemberLateOctoberIrrigationArea());
                SeptemberLateOctober.setIrrigationWaterVolume(irrigationQuota.getSeptemberLateOctoberIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(SeptemberLateOctober);

                IrrigationQuotaDetails OctoberEarlyOctober = new IrrigationQuotaDetails();
                OctoberEarlyOctober.setId(irrigationQuota.getOctoberEarlyOctoberId());
                OctoberEarlyOctober.setTurn(irrigationQuota.getOctoberEarlyOctoberTurn());
                OctoberEarlyOctober.setIrrigationArea(irrigationQuota.getOctoberEarlyOctoberIrrigationArea());
                OctoberEarlyOctober.setIrrigationWaterVolume(irrigationQuota.getOctoberEarlyOctoberIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(OctoberEarlyOctober);

                IrrigationQuotaDetails OctoberMid = new IrrigationQuotaDetails();
                OctoberMid.setId(irrigationQuota.getOctoberMidDayId());
                OctoberMid.setTurn(irrigationQuota.getOctoberMidDayTurn());
                OctoberMid.setIrrigationArea(irrigationQuota.getOctoberMidDayIrrigationArea());
                OctoberMid.setIrrigationWaterVolume(irrigationQuota.getOctoberMidDayIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(OctoberMid);

                IrrigationQuotaDetails OctoberLateOctober = new IrrigationQuotaDetails();
                OctoberLateOctober.setId(irrigationQuota.getOctoberLateOctoberId());
                OctoberLateOctober.setTurn(irrigationQuota.getOctoberLateOctoberTurn());
                OctoberLateOctober.setIrrigationArea(irrigationQuota.getOctoberLateOctoberIrrigationArea());
                OctoberLateOctober.setIrrigationWaterVolume(irrigationQuota.getOctoberLateOctoberIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(OctoberLateOctober);

                IrrigationQuotaDetails NovemberEarlyOctober = new IrrigationQuotaDetails();
                NovemberEarlyOctober.setId(irrigationQuota.getNovemberEarlyOctoberId());
                NovemberEarlyOctober.setTurn(irrigationQuota.getNovemberEarlyOctoberTurn());
                NovemberEarlyOctober.setIrrigationArea(irrigationQuota.getNovemberEarlyOctoberIrrigationArea());
                NovemberEarlyOctober.setIrrigationWaterVolume(irrigationQuota.getNovemberEarlyOctoberIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(NovemberEarlyOctober);

                IrrigationQuotaDetails NovemberMid = new IrrigationQuotaDetails();
                NovemberMid.setId(irrigationQuota.getNovemberMidDayId());
                NovemberMid.setTurn(irrigationQuota.getNovemberMidDayTurn());
                NovemberMid.setIrrigationArea(irrigationQuota.getNovemberMidDayIrrigationArea());
                NovemberMid.setIrrigationWaterVolume(irrigationQuota.getNovemberMidDayIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(NovemberMid);

                IrrigationQuotaDetails NovemberLateOctober = new IrrigationQuotaDetails();
                NovemberLateOctober.setId(irrigationQuota.getNovemberLateOctoberId());
                NovemberLateOctober.setTurn(irrigationQuota.getNovemberLateOctoberTurn());
                NovemberLateOctober.setIrrigationArea(irrigationQuota.getNovemberLateOctoberIrrigationArea());
                NovemberLateOctober.setIrrigationWaterVolume(irrigationQuota.getNovemberLateOctoberIrrigationWaterVolume());
                irrigationQuotaDetailsList.add(NovemberLateOctober);
            }
            List<IrrigationQuotaDetails> collect = irrigationQuotaDetailsList.stream().filter(t -> t.getIrrigationArea() != null || t.getIrrigationWaterVolume() != null).collect(Collectors.toList());
            irrigationQuotaDetailsService.updateBatchById(collect);
            return RestResponse.ok();
        } else {
            return RestResponse.no("error");
        }
    }

    @SneakyThrows
    @Override
    public RestResponse<List<IrrigationQuota>> selectList(IrrigationQuotaListReq req) {
        Integer lastYear = req.getYear()-1;
        Date startTime = sdf.parse(lastYear+"-01-01");
        Date endTime = sdf.parse(lastYear+"-"+req.getMonth()+"-"+(req.getTenDays().equals("上旬")?"01":(req.getTenDays().equals("中旬")?"11":"21")));
        List<IrrigationQuota> lastYearList = this.lambdaQuery().eq(IrrigationQuota::getYear, lastYear).eq(IrrigationQuota::getStation, req.getStation()).eq(IrrigationQuota::getWaterUser, req.getWaterUser()).list();
        List<IrrigationQuotaDetails> lastYearDetailsList = irrigationQuotaDetailsService.lambdaQuery().
                eq(IrrigationQuotaDetails::getYear, lastYear).
                eq(IrrigationQuotaDetails::getStation, req.getStation()).
                eq(IrrigationQuotaDetails::getWaterUser, req.getWaterUser()).
                //apply("CREATE_TIME between "+sdf.format(startTime)+" and "+sdf.format(endTime)).
                apply("CREATE_TIME BETWEEN '"+sdf.format(startTime)+"' and '"+sdf.format(endTime)+"'").
                list();
        List<IrrigationQuota> list = this.lambdaQuery().eq(req.getYear() != null, IrrigationQuota::getYear, req.getYear()).
                eq(StringUtils.isNotEmpty(req.getCropType()), IrrigationQuota::getCropType, req.getCropType()).
                eq(StringUtils.isNotEmpty(req.getStation()), IrrigationQuota::getStation, req.getStation()).
                eq(StringUtils.isNotEmpty(req.getWaterUser()), IrrigationQuota::getWaterUser, req.getWaterUser()).
                eq(IrrigationQuota::getDel, 0).
                list();
        list.forEach(irrigationQuota -> {
            if(!lastYearList.isEmpty()){
                irrigationQuota.setTotalPlannedIrrigationAreaLastYear(
                        lastYearList.stream().filter(t->t.getTotalPlannedIrrigationArea() !=null && t.getStation().equals(irrigationQuota.getStation()) && t.getWaterUser().equals(irrigationQuota.getWaterUser()) && t.getIrrigationCrop().equals(irrigationQuota.getIrrigationCrop())).
                                map(IrrigationQuota::getTotalPlannedIrrigationArea).reduce(Double::sum).orElse(0.0)
                );
            }
            if(!lastYearDetailsList.isEmpty()){
                irrigationQuota.setAccumulatedIrrigationAreaLastYear(
                        lastYearDetailsList.stream().filter(t->t.getIrrigationArea() !=null && t.getStation().equals(irrigationQuota.getStation()) && t.getWaterUser().equals(irrigationQuota.getWaterUser()) && t.getIrrigationCrop().equals(irrigationQuota.getIrrigationCrop())).
                                map(IrrigationQuotaDetails::getIrrigationArea).reduce(Double::sum).orElse(0.0)
                );
                irrigationQuota.setAccumulatedTotalIrrigationAmountLastYear(
                        lastYearDetailsList.stream().filter(t->t.getIrrigationWaterVolume() !=null && t.getStation().equals(irrigationQuota.getStation()) && t.getWaterUser().equals(irrigationQuota.getWaterUser()) && t.getIrrigationCrop().equals(irrigationQuota.getIrrigationCrop())).
                                map(IrrigationQuotaDetails::getIrrigationWaterVolume).reduce(Double::sum).orElse(0.0)
                );
                irrigationQuota.setAccumulatedTotalIrrigationTurnLastYear(
                        lastYearDetailsList.stream().filter(t->t.getTurn() !=null && t.getStation().equals(irrigationQuota.getStation()) && t.getWaterUser().equals(irrigationQuota.getWaterUser()) && t.getIrrigationCrop().equals(irrigationQuota.getIrrigationCrop())).
                                mapToInt(IrrigationQuotaDetails::getTurn).sum()
                );
            }

        });
        if (null != list && list.size() > 0) {
            return RestResponse.ok(list);
        } else {
            return RestResponse.no("暂无数据");
        }
    }
}

