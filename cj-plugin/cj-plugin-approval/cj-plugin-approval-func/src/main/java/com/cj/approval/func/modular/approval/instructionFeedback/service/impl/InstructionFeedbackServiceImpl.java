package com.cj.approval.func.modular.approval.instructionFeedback.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.approval.func.modular.approval.approvalManagement.entity.ApprovalManagement;
import com.cj.approval.func.modular.approval.approvalManagement.service.ApprovalManagementService;
import com.cj.approval.func.modular.approval.instructionFeedback.mapper.InstructionFeedbackMapper;
import com.cj.approval.func.modular.approval.instructionFeedback.entity.InstructionFeedback;
import com.cj.approval.func.modular.approval.instructionFeedback.service.InstructionFeedbackService;
import com.cj.approval.func.modular.approval.instructionViewing.entity.InstructionViewing;
import com.cj.approval.func.modular.approval.instructionViewing.service.InstructionViewingService;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 指令反馈表(InstructionFeedback)表服务实现类
 *
 * @author makejava
 * @since 2023-12-19 19:41:31
 */
@Service("instructionFeedbackService")
public class InstructionFeedbackServiceImpl extends ServiceImpl<InstructionFeedbackMapper, InstructionFeedback> implements InstructionFeedbackService {

    @Autowired
    private InstructionViewingService instructionViewingService;

    @Autowired
    private ApprovalManagementService approvalManagementService;

    @Override
    public RestResponse<List<InstructionFeedback>> selectListByInstructionViewId(String id) {
        List<InstructionFeedback> list = this.lambdaQuery().eq(InstructionFeedback::getInstructionViewId, id).list();
        if(null!=list && list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(InstructionFeedback instructionFeedback) {
        instructionFeedback.setId(UUIDUtils.getUUID());
        //当前用户
        instructionFeedback.setFeedbackBy("");
        instructionFeedback.setFeedbackTime(new Date());
        boolean save = this.save(instructionFeedback);
        if(save){
            boolean update = instructionViewingService.lambdaUpdate().set(InstructionViewing::getViewStatus, instructionFeedback.getFeedbackStatus()).eq(InstructionViewing::getId, instructionFeedback.getInstructionViewId()).update();
            if(update){
                InstructionViewing byId = instructionViewingService.getById(instructionFeedback.getInstructionViewId());
                String instructionId = byId.getInstructionId();
                if(instructionFeedback.getFeedbackStatus()==4){
                    List<InstructionViewing> list = instructionViewingService.lambdaQuery().eq(InstructionViewing::getInstructionId, instructionId).list();
                    Boolean flag = false;
                    for(InstructionViewing viewing:list){
                        if(viewing.getViewStatus()==4){
                            flag=true;
                        }else {
                            flag = false;
                        }
                    }
                    if(flag){
                        boolean update1 = approvalManagementService.lambdaUpdate().set(ApprovalManagement::getInstructionStatus, 3).eq(ApprovalManagement::getId, instructionId).update();
                        if(update1){
                            return RestResponse.ok();
                        }else {
                            return RestResponse.no("error");
                        }
                    }else {
                        boolean update1 = approvalManagementService.lambdaUpdate().set(ApprovalManagement::getInstructionStatus, 2).eq(ApprovalManagement::getId, instructionId).update();
                        if(update1){
                            return RestResponse.ok();
                        }else {
                            return RestResponse.no("error");
                        }
                    }
                }else {
                    boolean update1 = approvalManagementService.lambdaUpdate().set(ApprovalManagement::getInstructionStatus, 2).eq(ApprovalManagement::getId, instructionId).update();
                    if(update1){
                        return RestResponse.ok();
                    }else {
                        return RestResponse.no("error");
                    }
                }
            }else {
                return RestResponse.no("error");
            }
        }else {
            return RestResponse.no("error");
        }
    }
}

