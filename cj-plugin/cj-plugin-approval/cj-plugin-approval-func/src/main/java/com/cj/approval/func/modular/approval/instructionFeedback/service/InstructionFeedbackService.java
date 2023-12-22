package com.cj.approval.func.modular.approval.instructionFeedback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.approval.func.modular.approval.instructionFeedback.entity.InstructionFeedback;
import com.cj.common.model.RestResponse;

import java.util.List;

/**
 * 指令反馈表(InstructionFeedback)表服务接口
 *
 * @author makejava
 * @since 2023-12-19 19:41:31
 */
public interface InstructionFeedbackService extends IService<InstructionFeedback> {

    RestResponse<List<InstructionFeedback>> selectListByInstructionViewId(String id);

    RestResponse add(InstructionFeedback instructionFeedback);

}

