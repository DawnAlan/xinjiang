package com.cj.approval.func.modular.provider;


import com.alibaba.fastjson.JSONObject;
import com.cj.approval.api.ApprovalApi;
import com.cj.approval.func.modular.approval.approvalManagement.entity.ApprovalManagement;
import com.cj.approval.func.modular.approval.approvalManagement.service.ApprovalManagementService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ApprovalApiProvider implements ApprovalApi {

    @Autowired
    private ApprovalManagementService approvalManagementService;

    @Override
    public String getTotalCount(String time) {
        Map<String,Object> result = new HashMap<>();
        List<ApprovalManagement> list = approvalManagementService.lambdaQuery().between(ApprovalManagement::getCreateTime,time+" 00:00:00",time+" 23:25:59").list();
        long finish = list.stream().filter(t -> t.getInstructionStatus() == 3).count();
        result.put("incomplete",list.size()-finish);
        result.put("finish",finish);
        result.put("count",list.size());
        long lzz = list.stream().filter(t -> t.getDispatchingUnit().contains("楼庄子水库")).count();
        long tth = list.stream().filter(t -> t.getDispatchingUnit().contains("头屯河水库")).count();
        long qs = list.stream().filter(t -> t.getDispatchingUnit().contains("渠首管理站")).count();
        long hx = list.stream().filter(t -> t.getDispatchingUnit().contains("河西管理站")).count();
        long hd = list.stream().filter(t -> t.getDispatchingUnit().contains("河东管理站")).count();
        result.put("lzz",lzz);
        result.put("tth",tth);
        result.put("qs",qs);
        result.put("hx",hx);
        result.put("hd",hd);
        return JSONObject.toJSONString(result);
    }
}
