package com.cj.approval.func.modular.approval.instructionViewing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.approval.func.modular.approval.approvalManagement.entity.ApprovalManagement;
import com.cj.approval.func.modular.approval.approvalManagement.service.ApprovalManagementService;
import com.cj.approval.func.modular.approval.instructionViewing.mapper.InstructionViewingMapper;
import com.cj.approval.func.modular.approval.instructionViewing.entity.InstructionViewing;
import com.cj.approval.func.modular.approval.instructionViewing.service.InstructionViewingService;
import com.cj.common.model.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 指令查看表(InstructionViewing)表服务实现类
 *
 * @author makejava
 * @since 2023-12-19 19:41:48
 */
@Service("instructionViewingService")
public class InstructionViewingServiceImpl extends ServiceImpl<InstructionViewingMapper, InstructionViewing> implements InstructionViewingService {

    @Autowired
    private ApprovalManagementService approvalManagementService;
    @Override
    public RestResponse<List<InstructionViewing>> selectListByInstructionId(String instructionId) {
        List<InstructionViewing> list = this.lambdaQuery().eq(InstructionViewing::getInstructionId, instructionId).list();
        if(null!=list && list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse updateInstructionStatus(String instructionId) {
        List<InstructionViewing> list = this.lambdaQuery().eq(InstructionViewing::getInstructionId, instructionId).list();
        Boolean flag = false;
        for(InstructionViewing viewing:list){
            if(viewing.getInstructionStatus()==4){
                flag = true;
            }else {
                flag = false;
            }
        }
        if(flag){
            boolean update = approvalManagementService.lambdaUpdate().set(ApprovalManagement::getInstructionStatus, 3).eq(ApprovalManagement::getId, instructionId).update();
            if(update){
                return RestResponse.ok();
            }else {
                return RestResponse.no("error");
            }
        }else {
            boolean update = approvalManagementService.lambdaUpdate().set(ApprovalManagement::getInstructionStatus, 2).eq(ApprovalManagement::getId, instructionId).update();
            if(update){
                return RestResponse.ok();
            }else {
                return RestResponse.no("error");
            }
        }
    }
}

