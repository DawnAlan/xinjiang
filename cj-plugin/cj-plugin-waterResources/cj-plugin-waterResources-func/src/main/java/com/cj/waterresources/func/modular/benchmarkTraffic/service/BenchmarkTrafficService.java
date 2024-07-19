package com.cj.waterresources.func.modular.benchmarkTraffic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.benchmarkTraffic.bean.req.ApprovalReq;
import com.cj.waterresources.func.modular.benchmarkTraffic.bean.req.BenchmarkTrafficListReq;
import com.cj.waterresources.func.modular.benchmarkTraffic.entity.BenchmarkTraffic;

/**
 * 基准流量表(BenchmarkTraffic)表服务接口
 *
 * @author makejava
 * @since 2024-07-17 11:23:39
 */
public interface BenchmarkTrafficService extends IService<BenchmarkTraffic> {

    RestResponse add(BenchmarkTraffic benchmarkTraffic);


    RestResponse deleteById(String id);

    RestResponse selectList(BenchmarkTrafficListReq req);

    RestResponse approvalForSite(ApprovalReq req);
    RestResponse approvalForBureau(ApprovalReq req);


}

