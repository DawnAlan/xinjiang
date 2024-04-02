package com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.surfaceWater.generator.domain.SurfaceWater;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.bean.req.SelectPaymentReq;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.entity.WaterManagementUrbanIndustry;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.mapper.IndustrialWaterFeeMapper;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.entity.IndustrialWaterFee;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.service.IndustrialWaterFeeService;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.service.WaterManagementUrbanIndustryService;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.entity.PaymentWaterFees;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.service.PaymentWaterFeesService;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.service.impl.PaymentWaterFeesServiceImpl;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.UseWaterTypeStatisticsReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res.UseWaterTypeStatisticsRes;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.res.HydrographRes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.math.RoundingMode;
import java.sql.Wrapper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.math3.util.Precision.round;

/**
 * 工业水费(IndustrialWaterFee)表服务实现类
 *
 * @author makejava
 * @since 2024-01-31 20:11:19
 */
@Service("industrialWaterFeeService")
public class IndustrialWaterFeeServiceImpl extends ServiceImpl<IndustrialWaterFeeMapper, IndustrialWaterFee> implements IndustrialWaterFeeService {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private PaymentWaterFeesService paymentWaterFeesService;
    private WaterManagementUrbanIndustryService waterManagementUrbanIndustryService;

    @Override
    public List<UseWaterTypeStatisticsRes> statistics(UseWaterTypeStatisticsReq req) {
        return this.baseMapper.statistics(req);
    }

    @Override
    public RestResponse<List<HydrographRes>> selectInfoList(SelectInfoListReq req) {
        List<HydrographRes> hydrographResList = new ArrayList<>();
        if (StringUtils.isNotEmpty(req.getTreeName())) {
            List<UseWaterTypeStatisticsRes> useWaterTypeStatisticsRes = this.baseMapper.selectInfoList(req);
            if (useWaterTypeStatisticsRes.isEmpty()) {
                return RestResponse.no("暂无数据");
            } else {
                useWaterTypeStatisticsRes.forEach(t -> {
                    HydrographRes res = new HydrographRes();
                    res.setFlow(t.getV());
                    res.setName(t.getParamName());
                    res.setTime(sdf.format(t.getRecordTime()));
                    hydrographResList.add(res);
                });
                return RestResponse.ok(hydrographResList);
            }
        } else {
            return RestResponse.no("暂无数据");
        }

    }

    @Override
    public WaterManagementUrbanIndustry selectPayment(SelectPaymentReq input) {
        WaterManagementUrbanIndustry waterManagementUrbanIndustry = new WaterManagementUrbanIndustry();
        waterManagementUrbanIndustry = waterManagementUrbanIndustryService.lambdaQuery()
                .eq(WaterManagementUrbanIndustry::getSiteCode, input.getSiteCode())
                .eq(WaterManagementUrbanIndustry::getYear, input.getYear())
                .eq(WaterManagementUrbanIndustry::getMonth, input.getMonth()).getEntity();
        if (waterManagementUrbanIndustry != null){
            return waterManagementUrbanIndustry;
        }
/*        LambdaQueryWrapper<IndustrialWaterFee> wrapper1 = Wrappers.lambdaQuery();
        wrapper1.eq(IndustrialWaterFee::getStation, input.getSiteName())
                .eq(IndustrialWaterFee::getYear, input.getYear())
                .eq(IndustrialWaterFee::getMonth, input.getMonth());
        //月总水量
        Double sumMFlow = this.baseMapper.selectList(wrapper1).stream().mapToDouble(s -> s.getFlow()).sum();*/

        LambdaQueryWrapper<IndustrialWaterFee> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(IndustrialWaterFee::getStation, input.getSiteName())
                .eq(IndustrialWaterFee::getYear, input.getYear())
                .between(IndustrialWaterFee::getMonth, 1, input.getMonth());
        //年总水量
        Double sumFlow = this.baseMapper.selectList(wrapper).stream().mapToDouble(s -> s.getFlow()).sum();
        //当前年已交费用
        Double totalPaidWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel, 0)
                .eq(PaymentWaterFees::getYear, input.getYear())
                .eq(PaymentWaterFees::getWaterUserId, input.getSiteCode())
                .eq(PaymentWaterFees::getType, "水费").list().stream().mapToDouble(s -> s.getPaymentAmount()).sum();


        waterManagementUrbanIndustry.setId(UUID.randomUUID().toString());
        waterManagementUrbanIndustry.setSiteCode(input.getSiteCode());
        waterManagementUrbanIndustry.setSiteName(input.getSiteName());
        waterManagementUrbanIndustry.setYear(input.getYear());
        waterManagementUrbanIndustry.setMonth(input.getMonth());
        waterManagementUrbanIndustry.setAgriculturalWaterPrice(input.getAgriculturalWaterPrice());
        waterManagementUrbanIndustry.setIndustrialWaterPrice(input.getIndustrialWaterPrice());
        waterManagementUrbanIndustry.setWaterResourceTaxes(input.getWaterResourceTaxes());
        waterManagementUrbanIndustry.setAgriculturalProportion(input.getAgriculturalProportion());
        //工业水量
        waterManagementUrbanIndustry.setIndustrialProportion(input.getIndustrialProportion());
        //年累计用水量
        Double annualCumulativeWaterConsumption = sumFlow * 8.64;
        waterManagementUrbanIndustry.setAnnualCumulativeWaterConsumption(round(annualCumulativeWaterConsumption, 3));
        //应交农业水费
        Double agriculturalWaterFeesPayable = input.getAgriculturalProportion() / 100 * sumFlow * 8.64 * input.getAgriculturalWaterPrice();
        waterManagementUrbanIndustry.setAgriculturalWaterFeesPayable(round(agriculturalWaterFeesPayable, 3));
        //应交工业水费
        Double industrialWaterFeesPayable = input.getIndustrialProportion() / 100 * sumFlow * 8.64 * input.getIndustrialWaterPrice();
        waterManagementUrbanIndustry.setIndustrialWaterFeesPayable(round(industrialWaterFeesPayable, 3));
        //应交工业水资源费
        Double waterResourceWaterFeesPayable = input.getIndustrialProportion() / 100 * sumFlow * 8.64 * input.getWaterResourceTaxes();
        waterManagementUrbanIndustry.setWaterResourceWaterFeesPayable(round(waterResourceWaterFeesPayable, 3));
        //应交水费合计
        Double totalWaterFeesPayable = agriculturalWaterFeesPayable + industrialWaterFeesPayable + waterResourceWaterFeesPayable;
        waterManagementUrbanIndustry.setTotalWaterFeesPayable(round(totalWaterFeesPayable, 3));
        //已交水费合计
        waterManagementUrbanIndustry.setTotalPaidWaterFees(totalPaidWaterFees);
        //已交农业水费 应交农业/应交水费合计*已交水费合计
        Double agriculturalPaidWaterFees = agriculturalWaterFeesPayable / totalWaterFeesPayable * totalPaidWaterFees;
        waterManagementUrbanIndustry.setAgriculturalPaidWaterFees(round(agriculturalPaidWaterFees, 3));
        //已交工业水费
        Double industrialPaidWaterFees = industrialWaterFeesPayable / totalWaterFeesPayable * totalPaidWaterFees;
        waterManagementUrbanIndustry.setIndustrialPaidWaterFees(round(industrialPaidWaterFees, 3));
        //预交工业水资源费
        Double waterResourcePaidWaterFees = waterResourceWaterFeesPayable / totalWaterFeesPayable * totalPaidWaterFees;
        waterManagementUrbanIndustry.setWaterResourcePaidWaterFees(round(waterResourcePaidWaterFees, 3));
        //盈余水费合计
        Double totalSurplusWaterFees = totalPaidWaterFees - totalWaterFeesPayable;
        waterManagementUrbanIndustry.setTotalSurplusWaterFees(round(totalSurplusWaterFees, 3));
        //盈余农业水费
        Double agriculturalSurplusWaterFees = agriculturalPaidWaterFees - agriculturalWaterFeesPayable;
        waterManagementUrbanIndustry.setAgriculturalSurplusWaterFees(round(agriculturalSurplusWaterFees, 3));
        //盈余工业水费
        Double industrialSurplusWaterFees = industrialPaidWaterFees - industrialWaterFeesPayable;
        waterManagementUrbanIndustry.setIndustrialSurplusWaterFees(round(industrialSurplusWaterFees, 3));
        //盈余水资源费
        Double waterResourceSurplusWaterFees = waterResourcePaidWaterFees - waterResourceWaterFeesPayable;
        waterManagementUrbanIndustry.setWaterResourceSurplusWaterFees(round(waterResourceSurplusWaterFees, 3));
        waterManagementUrbanIndustryService.save(waterManagementUrbanIndustry);
        return waterManagementUrbanIndustry;
    }
}

