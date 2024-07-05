package com.cj.approval.func.modular.approval.instructionFeedback.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.approval.func.core.utils.WebSocketServer;
import com.cj.approval.func.modular.approval.approvalManagement.bean.res.SendMsgRes;
import com.cj.approval.func.modular.approval.dutyRecords.entity.DutyRecords;
import com.cj.approval.func.modular.approval.dutyRecords.service.DutyRecordsService;
import com.cj.approval.func.modular.approval.instructionFeedback.mapper.InstructionFeedbackMapper;
import com.cj.approval.func.modular.approval.instructionFeedback.entity.InstructionFeedback;
import com.cj.approval.func.modular.approval.instructionFeedback.service.InstructionFeedbackService;
import com.cj.approval.func.modular.approval.instructionViewing.entity.InstructionViewing;
import com.cj.approval.func.modular.approval.instructionViewing.service.InstructionViewingService;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.msg.entity.OverallMsg;
import com.cj.msg.enums.MessageCategoryEnum;
import com.cj.msg.service.OverallMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private OverallMsgService overallMsgService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


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
                        String msgContext = instructionFeedback.getFeedbackBy()+"已反馈，反馈的信息："+instructionFeedback.getFeedbackContext();
                        saveMsg(saBaseLoginUser,msgContext,s,"");
                        WebSocketServer.sendInfo(msgContext,s);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return RestResponse.no("send msg fail");
                }
                ExecutorService pool = Executors.newSingleThreadExecutor();
                pool.submit(new Runnable() {
                    private DutyRecordsService dutyRecordsService = SpringUtil.getBean(DutyRecordsService.class);
                    @Override
                    public void run() {
                        DutyRecords dutyRecords = dutyRecordsService.lambdaQuery().eq(DutyRecords::getStation, saBaseLoginUser.getOrgName()).eq(DutyRecords::getType,1).
                                apply("RECORD_TIME = {0}", DateUtil.format(new Date(), "yyyy-MM-dd")).last("limit 1").one();
                        if(dutyRecords!=null){
                            String splicingMsg = dutyRecords.getContextInfo()+"\\n"+splicingMsg(instructionFeedback);
                            dutyRecordsService.lambdaUpdate().set(DutyRecords::getContextInfo,splicingMsg).eq(DutyRecords::getId,dutyRecords.getId()).update();
                        }else {
                            DutyRecords dutyRecordsTemp = new DutyRecords();
                            dutyRecordsTemp.setDel(0);
                            dutyRecordsTemp.setType(1);
                            dutyRecordsTemp.setId(UUIDUtils.getUUID());
                            dutyRecordsTemp.setCreateTime(new Date());
                            try {
                                dutyRecordsTemp.setRecordTime(sdf.parse(sdf.format(new Date())));
                            } catch (ParseException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                            dutyRecordsTemp.setCreateBy(saBaseLoginUser.getName());
                            dutyRecordsTemp.setStation(saBaseLoginUser.getOrgName());
                            dutyRecordsTemp.setContextInfo(splicingMsg(instructionFeedback));
                            dutyRecordsService.save(dutyRecordsTemp);
                        }
                    }
                });
                return instructionViewingService.updateInstructionStatus(instructionViewingService.getById(instructionFeedback.getInstructionViewId()).getInstructionId());
            }else {
                return RestResponse.no("error");
            }
        }else {
            return RestResponse.no("error");
        }
    }

    private void saveMsg(SaBaseLoginUser saBaseLoginUser,String msgContext,String receiveUser,String extJson){
        OverallMsg msg = new OverallMsg();
        msg.setId(UUIDUtils.getUUID());
        msg.setSubject(msgContext);
        msg.setCreateTime(new Date());
        msg.setIsRead(0);
        msg.setCreateUser(saBaseLoginUser.getId());
        msg.setReceiveUser(receiveUser);
        msg.setCategory(MessageCategoryEnum.APPROVAL.getValue());
        msg.setExtJson(extJson);
        SendMsgRes sendMsgRes = new SendMsgRes();
        sendMsgRes.setSendBy(saBaseLoginUser.getName());
        sendMsgRes.setSendUnit(saBaseLoginUser.getOrgName());
        sendMsgRes.setSendTime(new Date());
        sendMsgRes.setSendContent(msgContext);
        msg.setContent(com.alibaba.fastjson2.JSONObject.toJSONString(sendMsgRes));
        overallMsgService.save(msg);
    }

    private String splicingMsg(InstructionFeedback instructionFeedback){
        String msg = "";
        msg += sdf.format(instructionFeedback.getFeedbackTime());
        //反馈状态(1-未开始 2-开始 3-进行中 4-已完成)
        msg += " 指令 "+(instructionFeedback.getFeedbackStatus()==1?"未开始":instructionFeedback.getFeedbackStatus()==2?"开始":instructionFeedback.getFeedbackStatus()==3?"进行中":instructionFeedback.getFeedbackStatus()==4?"已完成":"未知状态");
        msg += "。"+instructionFeedback.getFeedbackBy()+"通知"+instructionFeedback.getRecipient()+instructionFeedback.getFeedbackContext()+"。";
        msg += "配水人员为："+instructionFeedback.getExecutive();
        return msg;
    }
}

