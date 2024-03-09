package com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.mapper.IndustrialWaterFeeMapper;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.entity.IndustrialWaterFee;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.service.IndustrialWaterFeeService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.UseWaterTypeStatisticsReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res.UseWaterTypeStatisticsRes;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.res.HydrographRes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 工业水费(IndustrialWaterFee)表服务实现类
 *
 * @author makejava
 * @since 2024-01-31 20:11:19
 */
@Service("industrialWaterFeeService")
public class IndustrialWaterFeeServiceImpl extends ServiceImpl<IndustrialWaterFeeMapper, IndustrialWaterFee> implements IndustrialWaterFeeService {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<UseWaterTypeStatisticsRes> statistics(UseWaterTypeStatisticsReq req) {
        return this.baseMapper.statistics(req);
    }

    @Override
    public RestResponse<List<HydrographRes>> selectInfoList(SelectInfoListReq req) {
        List<HydrographRes> hydrographResList = new ArrayList<>();
        if(StringUtils.isNotEmpty(req.getTreeName())){
            List<UseWaterTypeStatisticsRes> useWaterTypeStatisticsRes = this.baseMapper.selectInfoList(req);
            if(useWaterTypeStatisticsRes.isEmpty()){
                return RestResponse.no("暂无数据");
            }else {
                useWaterTypeStatisticsRes.forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setFlow(t.getV());
                    res.setName(t.getParamName());
                    res.setTime(sdf.format(t.getRecordTime()));
                    hydrographResList.add(res);
                });
                return RestResponse.ok(hydrographResList);
            }
        }else {
            return RestResponse.no("暂无数据");
        }

    }
}

