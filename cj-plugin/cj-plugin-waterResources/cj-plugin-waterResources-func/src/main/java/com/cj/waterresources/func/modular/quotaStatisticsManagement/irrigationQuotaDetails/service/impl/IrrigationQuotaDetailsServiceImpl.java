package com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.bean.req.IrrigationQuotaContrastReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.bean.res.IrrigationQuotaContrastRes;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.bean.req.StatisticsReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.mapper.IrrigationQuotaDetailsMapper;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.entity.IrrigationQuotaDetails;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.service.IrrigationQuotaDetailsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 灌溉明细表(IrrigationQuotaDetails)表服务实现类
 *
 * @author makejava
 * @since 2024-02-02 10:59:15
 */
@Service("irrigationQuotaDetailsService")
public class IrrigationQuotaDetailsServiceImpl extends ServiceImpl<IrrigationQuotaDetailsMapper, IrrigationQuotaDetails> implements IrrigationQuotaDetailsService {
    @Override
    public RestResponse<Map<String, List<IrrigationQuotaDetails>>> statistics(StatisticsReq req) {
        List<IrrigationQuotaDetails> statistics = this.baseMapper.statistics(req);
        if (null != statistics && statistics.size() > 0) {
            if (StringUtils.isEmpty(req.getUnit())) {
                Map<String, List<IrrigationQuotaDetails>> collect = statistics.stream().collect(Collectors.groupingBy(IrrigationQuotaDetails::getWaterUser));
                return RestResponse.ok(collect);
            } else {
                if (StringUtils.isEmpty(req.getCropType())) {
                    Map<String, List<IrrigationQuotaDetails>> collect = statistics.stream().collect(Collectors.groupingBy(IrrigationQuotaDetails::getCropType));
                    return RestResponse.ok(collect);
                } else {
                    Map<String, List<IrrigationQuotaDetails>> collect = statistics.stream().collect(Collectors.groupingBy(IrrigationQuotaDetails::getIrrigationCrop));
                    return RestResponse.ok(collect);
                }
            }
        } else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public List<IrrigationQuotaContrastRes> contrast(IrrigationQuotaContrastReq req) {
        return this.baseMapper.contrast(req);
    }
}

