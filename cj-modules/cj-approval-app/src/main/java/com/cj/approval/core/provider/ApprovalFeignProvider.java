package com.cj.approval.core.provider;

import com.cj.approval.feign.ApprovalFeign;
import com.cj.approval.func.modular.provider.ApprovalApiProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ApprovalFeignProvider implements ApprovalFeign {

    private final ApprovalApiProvider approvalApiProvider;

    @Override
    public String getTotalCount(String time) {
        return approvalApiProvider.getTotalCount(time);
    }
}
