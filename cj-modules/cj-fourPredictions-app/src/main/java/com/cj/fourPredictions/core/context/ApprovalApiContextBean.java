package com.cj.fourPredictions.core.context;

import com.cj.approval.api.ApprovalApi;
import com.cj.approval.feign.ApprovalFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ApprovalApiContextBean implements ApprovalApi {

    private final ApprovalFeign approvalFeign;

    @Override
    public String getTotalCount(String time) {
        return approvalFeign.getTotalCount(time);
    }
}
