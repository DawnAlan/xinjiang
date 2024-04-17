package com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.WaterFeeStatisticsDetailsSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res.SelectTotalForIndexRes;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.mapper.WaterFeeStatisticsTotalMapper;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsTotal;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.WaterFeeStatisticsTotalService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 水费统计总计(WaterFeeStatisticsTotal)表服务实现类
 *
 * @author makejava
 * @since 2023-11-29 17:16:56
 */
@Service("waterFeeStatisticsTotalService")
public class WaterFeeStatisticsTotalServiceImpl extends ServiceImpl<WaterFeeStatisticsTotalMapper, WaterFeeStatisticsTotal> implements WaterFeeStatisticsTotalService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    private DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    public RestResponse<List<WaterFeeStatisticsTotal>> selectInfoList(WaterFeeStatisticsDetailsSelectListReq req) {
        try {
            List<WaterFeeStatisticsTotal> list = this.lambdaQuery().eq(WaterFeeStatisticsTotal::getStation, req.getStation()).
                    eq(WaterFeeStatisticsTotal::getYear, req.getYear()).
                    eq(WaterFeeStatisticsTotal::getMonth, req.getMonth()).
                    eq(WaterFeeStatisticsTotal::getTenDays, req.getTenDays()).list();
            if(null != list && list.size()>0){
                return RestResponse.ok(list);
            }else {
                return RestResponse.no("暂无数据");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("查询失败");
        }
    }

    @Override
    public RestResponse selectTotalForIndex(String time) {
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 2 && !t.getUseStation().equals("城市工业")&& t.getPId().equals("0") && t.getParamName().equals("合计")).collect(Collectors.toList());
        List<SelectTotalForIndexRes> resList = new ArrayList<>();
        String[] split = time.split("-");
        Integer year = Integer.valueOf(split[0]);
        Integer month =Integer.valueOf(split[1]);
        Integer day = Integer.valueOf(split[2]);
        String s = determineTenDays(day);
        List<String> totalIds = trendsTableParamList.stream().map(TrendsTableParam::getId).collect(Collectors.toList());
        List<WaterFeeStatisticsTotal> waterFeeStatisticsTotalList = this.lambdaQuery().in(WaterFeeStatisticsTotal::getTableHeadId, totalIds).eq(WaterFeeStatisticsTotal::getYear, year).eq(WaterFeeStatisticsTotal::getMonth, month).
                eq(WaterFeeStatisticsTotal::getTenDays, s).list();
        for(TrendsTableParam param:trendsTableParamList){
            SelectTotalForIndexRes res = new SelectTotalForIndexRes();
            res.setName(param.getUseStation().replace("管理站",""));
            Double aDouble = waterFeeStatisticsTotalList.stream().filter(t -> t.getTableHeadId().equals(param.getId())).map(WaterFeeStatisticsTotal::getAccumulatedWaterVolume).reduce(Double::sum).orElse(0.00);
            res.setV(formatDoubleForThreeDecimal(aDouble));
            resList.add(res);
        }
        return RestResponse.ok(resList);
    }

    public String determineTenDays(Integer day){
        if(day<=10){
            return "上旬";
        }
        if(day<=20){
            return "中旬";
        }
        if(day>20){
            return "下旬";
        }
        return "";
    }

    private Double formatDoubleForThreeDecimal(Double value){
        String format = df.format(value);
        double v = Double.parseDouble(format);
        return v;
    }
}

