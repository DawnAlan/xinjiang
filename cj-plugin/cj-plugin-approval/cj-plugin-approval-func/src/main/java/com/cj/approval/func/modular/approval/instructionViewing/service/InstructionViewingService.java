package com.cj.approval.func.modular.approval.instructionViewing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.approval.func.modular.approval.instructionViewing.entity.InstructionViewing;
import com.cj.common.model.RestResponse;

import java.util.List;

/**
 * 指令查看表(InstructionViewing)表服务接口
 *
 * @author makejava
 * @since 2023-12-19 19:41:48
 */
public interface InstructionViewingService extends IService<InstructionViewing> {

    RestResponse<List<InstructionViewing>> selectListByInstructionId(String instructionId);

    RestResponse updateInstructionStatus(String instructionId);

}

