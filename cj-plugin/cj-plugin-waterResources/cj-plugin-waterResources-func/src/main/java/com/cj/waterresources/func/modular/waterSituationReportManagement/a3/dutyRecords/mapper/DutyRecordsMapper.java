package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dutyRecords.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dutyRecords.bean.req.DutyRecordsSelectListReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dutyRecords.entity.DutyRecords;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 值班记录(DutyRecords)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-25 16:59:50
 */
public interface DutyRecordsMapper extends BaseMapper<DutyRecords> {

    List<DutyRecords> selectListByParam(@Param("req") DutyRecordsSelectListReq req);

}

