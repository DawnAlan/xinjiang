package com.cj.approval.func.modular.approval.instructionFeedback.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.approval.func.core.utils.WebSocketServer;
import com.cj.approval.func.modular.approval.approvalManagement.entity.ApprovalManagement;
import com.cj.approval.func.modular.approval.approvalManagement.service.ApprovalManagementService;
import com.cj.approval.func.modular.approval.instructionFeedback.mapper.InstructionFeedbackMapper;
import com.cj.approval.func.modular.approval.instructionFeedback.entity.InstructionFeedback;
import com.cj.approval.func.modular.approval.instructionFeedback.service.InstructionFeedbackService;
import com.cj.approval.func.modular.approval.instructionViewing.entity.InstructionViewing;
import com.cj.approval.func.modular.approval.instructionViewing.service.InstructionViewingService;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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
        InstructionViewing byId = instructionViewingService.getById(instructionFeedback.getInstructionViewId());
        if(byId.getInstructionStatus()==4){
            return RestResponse.no("已反馈完成，请勿重复提交");
        }
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        instructionFeedback.setId(UUIDUtils.getUUID());
        //当前用户
        instructionFeedback.setFeedbackBy(saBaseLoginUser.getName());
        instructionFeedback.setFeedbackTime(new Date());
        boolean save = this.save(instructionFeedback);
        if(save){
            boolean update = instructionViewingService.lambdaUpdate().set(instructionFeedback.getFeedbackStatus()==4,InstructionViewing::getCompleteTime,new Date()).
                    set(InstructionViewing::getInstructionStatus, instructionFeedback.getFeedbackStatus()).eq(InstructionViewing::getId, instructionFeedback.getInstructionViewId()).update();
            if(update){
                try {
                    String[] split = instructionFeedback.getRecipientId().split(",");
                    for (String s : split){
                        WebSocketServer.sendInfo(instructionFeedback.getFeedbackBy()+"已反馈，反馈的信息："+instructionFeedback.getFeedbackContext(),s);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return RestResponse.no("send msg fail");
                }
                return instructionViewingService.updateInstructionStatus(instructionViewingService.getById(instructionFeedback.getInstructionViewId()).getInstructionId());
            }else {
                return RestResponse.no("error");
            }
        }else {
            return RestResponse.no("error");
        }
    }
}

