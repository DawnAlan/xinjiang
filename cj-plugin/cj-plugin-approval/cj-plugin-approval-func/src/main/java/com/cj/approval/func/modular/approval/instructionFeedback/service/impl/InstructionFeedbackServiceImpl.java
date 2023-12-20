package com.cj.approval.func.modular.approval.instructionFeedback.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.approval.func.modular.approval.instructionFeedback.mapper.InstructionFeedbackMapper;
import com.cj.approval.func.modular.approval.instructionFeedback.entity.InstructionFeedback;
import com.cj.approval.func.modular.approval.instructionFeedback.service.InstructionFeedbackService;
import org.springframework.stereotype.Service;

/**
 * 指令反馈表(InstructionFeedback)表服务实现类
 *
 * @author makejava
 * @since 2023-12-19 19:41:31
 */
@Service("instructionFeedbackService")
public class InstructionFeedbackServiceImpl extends ServiceImpl<InstructionFeedbackMapper, InstructionFeedback> implements InstructionFeedbackService {

}

