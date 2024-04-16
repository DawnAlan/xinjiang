package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.mapper.ApprovalTrafficOverviewMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.entity.ApprovalTrafficOverview;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.ApprovalTrafficOverviewService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 审批管理流量概览表(ApprovalTrafficOverview)表服务实现类
 *
 * @author makejava
 * @since 2024-04-09 16:35:43
 */
@Service("approvalTrafficOverviewService")
public class ApprovalTrafficOverviewServiceImpl extends ServiceImpl<ApprovalTrafficOverviewMapper, ApprovalTrafficOverview> implements ApprovalTrafficOverviewService {

    @Override
    public RestResponse selectListById(String id) {
        List<ApprovalTrafficOverview> list = this.lambdaQuery().eq(ApprovalTrafficOverview::getOverviewId, id).list();
        if(list.isEmpty()){
            return RestResponse.no("未查询到相关数据");
        }else {
            return RestResponse.ok(list);
        }
    }

    @Override
    public RestResponse update(ApprovalTrafficOverview approvalTrafficOverview) {
        boolean b = this.updateById(approvalTrafficOverview);
        if(b){
          return RestResponse.ok("更新成功");
        }else {
            return RestResponse.no("更新失败");
        }
    }
}

