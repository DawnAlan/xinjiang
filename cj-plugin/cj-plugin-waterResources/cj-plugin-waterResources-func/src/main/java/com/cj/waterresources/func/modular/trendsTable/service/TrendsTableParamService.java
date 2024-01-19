package com.cj.waterresources.func.modular.trendsTable.service;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.trendsTable.bean.req.QueryTrendsTableParamReq;
import com.cj.waterresources.func.modular.trendsTable.bean.req.TrendsTableParamAddReq;
import com.cj.waterresources.func.modular.trendsTable.bean.req.TrendsTableParamUpdateReq;
import com.cj.waterresources.func.modular.trendsTable.bean.res.WaterDailyParamSelectRes;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author July Lion
* @description 针对表【WATER_DAILY_PARAM(水情日报参数表)】的数据库操作Service
* @createDate 2023-10-27 10:41:38
*/
public interface TrendsTableParamService extends IService<TrendsTableParam> {


    @Transactional(rollbackFor=Exception.class)
    RestResponse add(TrendsTableParamAddReq req);

    RestResponse delete(String id);

    @Transactional(rollbackFor=Exception.class)
    RestResponse update(TrendsTableParamUpdateReq req);

    RestResponse<List<WaterDailyParamSelectRes>> select(QueryTrendsTableParamReq req);

    RestResponse<List<TrendsTableParam>> selectNoParent();

    void updateCache();

}
