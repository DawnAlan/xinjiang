package com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.mapper.IndustrialWaterFeeMapper;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.entity.IndustrialWaterFee;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.service.IndustrialWaterFeeService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.UseWaterTypeStatisticsReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res.UseWaterTypeStatisticsRes;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工业水费(IndustrialWaterFee)表服务实现类
 *
 * @author makejava
 * @since 2024-01-31 20:11:19
 */
@Service("industrialWaterFeeService")
public class IndustrialWaterFeeServiceImpl extends ServiceImpl<IndustrialWaterFeeMapper, IndustrialWaterFee> implements IndustrialWaterFeeService {

    @Override
    public List<UseWaterTypeStatisticsRes> statistics(UseWaterTypeStatisticsReq req) {
        return this.baseMapper.statistics(req);
    }

    @Override
    public RestResponse<List<UseWaterTypeStatisticsRes>> selectInfoList(SelectInfoListReq req) {
        if(StringUtils.isNotEmpty(req.getTreeName())){
            List<UseWaterTypeStatisticsRes> useWaterTypeStatisticsRes = this.baseMapper.selectInfoList(req);
            if(useWaterTypeStatisticsRes.isEmpty()){
                return RestResponse.no("暂无数据");
            }else {
                return RestResponse.ok(useWaterTypeStatisticsRes);
            }
        }else {
            return RestResponse.no("暂无数据");
        }

    }
}

