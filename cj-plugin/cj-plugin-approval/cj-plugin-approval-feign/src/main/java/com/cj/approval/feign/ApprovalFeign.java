package com.cj.approval.feign;

import com.cj.common.consts.FeignConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = FeignConstant.APPROVAL_APP,contextId="approvalFeign")
public interface ApprovalFeign {

    @RequestMapping("/feign/provider/approval/getTotalCount")
    public String getTotalCount();
}
