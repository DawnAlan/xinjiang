package com.cj.approval.func.modular.approval.dutyRecords.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.approval.func.modular.approval.dutyRecords.entity.DutyRecords;
import com.cj.approval.func.modular.approval.dutyRecords.mapper.DutyRecordsMapper;
import com.cj.approval.func.modular.approval.dutyRecords.service.DutyRecordsService;
import org.springframework.stereotype.Service;

/**
 * 值班记录(DutyRecords)表服务实现类
 *
 * @author makejava
 * @since 2023-12-25 16:59:50
 */
@Service("dutyRecordsService")
public class DutyRecordsServiceImpl extends ServiceImpl<DutyRecordsMapper, DutyRecords> implements DutyRecordsService {

}

