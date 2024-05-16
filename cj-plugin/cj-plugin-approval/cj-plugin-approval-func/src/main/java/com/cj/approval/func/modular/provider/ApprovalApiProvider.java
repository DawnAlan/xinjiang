package com.cj.approval.func.modular.provider;


import com.alibaba.fastjson.JSONObject;
import com.cj.approval.api.ApprovalApi;
import com.cj.approval.func.modular.approval.approvalManagement.entity.ApprovalManagement;
import com.cj.approval.func.modular.approval.approvalManagement.service.ApprovalManagementService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ApprovalApiProvider implements ApprovalApi {

    @Autowired
    private ApprovalManagementService approvalManagementService;

    @Override
    public String getTotalCount(String time) {
        Map<String,Object> result = new HashMap<>();
        List<ApprovalManagement> list = approvalManagementService.lambdaQuery().between(ApprovalManagement::getCreateTime,time+" 00:00:00",time+" 23:25:59").list();
        result.put("总数",list.size());
        Map<String, List<ApprovalManagement>> instructionType = list.stream().collect(Collectors.groupingBy(ApprovalManagement::getInstructionType));
        Set<String> instructionTypeSet = instructionType.keySet();
        for(String string : instructionTypeSet){
            result.put(string,instructionType.get(string).size());
            Map<Integer, List<ApprovalManagement>> approvalStatus = list.stream().filter(t->t.getInstructionType().equals(string)).collect(Collectors.groupingBy(ApprovalManagement::getApprovalStatus));
            Set<Integer> approvalStatusSet = approvalStatus.keySet();
            Map<String,Object> result1 = new HashMap<>();
            for(Integer i : approvalStatusSet){
                //审批状态(1-待审批 2-已审批 3-已拒绝)
                result1.put(i==1?"待审批":i==2?"已审批":"已拒绝",approvalStatus.get(i).size());
                Map<String,Object> result2 = new HashMap<>();
                if(i==2){
                    Map<Integer, List<ApprovalManagement>> instructionStatus = list.stream().filter(t->t.getInstructionType().equals(string)&&t.getApprovalStatus()==2).collect(Collectors.groupingBy(ApprovalManagement::getInstructionStatus));
                    Set<Integer> instructionStatusSet = instructionStatus.keySet();
                    for(Integer ii : instructionStatusSet){
                        //指令状态(1-未开始 2-进行中 3-已完成)
                        result2.put(ii==1?"未开始":ii==2?"进行中":"已完成",instructionStatus.get(ii).size());
                    }
                }
                if(string.equals("指令下达")&&result2.size()>0){
                    result.put(string+"_"+(i==1?"待审批":i==2?"已审批":"已拒绝")+"_detailCount",result2);
                }
            }
            result.put(string+"_detailCount",result1);
        }

        return JSONObject.toJSONString(result);
    }
}
