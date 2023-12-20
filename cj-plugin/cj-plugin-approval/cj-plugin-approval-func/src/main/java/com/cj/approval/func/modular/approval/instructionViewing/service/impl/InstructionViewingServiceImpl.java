package com.cj.approval.func.modular.approval.instructionViewing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.approval.func.modular.approval.instructionViewing.mapper.InstructionViewingMapper;
import com.cj.approval.func.modular.approval.instructionViewing.entity.InstructionViewing;
import com.cj.approval.func.modular.approval.instructionViewing.service.InstructionViewingService;
import org.springframework.stereotype.Service;

/**
 * 指令查看表(InstructionViewing)表服务实现类
 *
 * @author makejava
 * @since 2023-12-19 19:41:48
 */
@Service("instructionViewingService")
public class InstructionViewingServiceImpl extends ServiceImpl<InstructionViewingMapper, InstructionViewing> implements InstructionViewingService {

}

