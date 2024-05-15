package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dutyRecords.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dutyRecords.bean.req.DutyRecordsSelectListReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dutyRecords.mapper.DutyRecordsMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dutyRecords.entity.DutyRecords;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dutyRecords.service.DutyRecordsService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 值班记录(DutyRecords)表服务实现类
 *
 * @author makejava
 * @since 2023-12-25 16:59:50
 */
@Service("dutyRecordsService")
public class DutyRecordsServiceImpl extends ServiceImpl<DutyRecordsMapper, DutyRecords> implements DutyRecordsService {

    @Override
    public RestResponse add(DutyRecords dutyRecords) {
        dutyRecords.setId(UUIDUtils.getUUID());
        dutyRecords.setDel(0);
        dutyRecords.setCreateTime(new Date());
        boolean save = this.save(dutyRecords);
        if(save){
            return RestResponse.ok();
        }else {
            return RestResponse.no("failed to save duty records");
        }
    }

    @Override
    public RestResponse insert(DutyRecords dutyRecords) {
        dutyRecords.setId(UUIDUtils.getUUID());
        dutyRecords.setDel(0);
        dutyRecords.setCreateTime(new Date());
        boolean save = this.save(dutyRecords);
        if(save){
            return RestResponse.ok();
        }else {
            return RestResponse.no("failed to save duty records");
        }
    }

    @Override
    public RestResponse delete(String id) {
        boolean update = this.lambdaUpdate().set(DutyRecords::getDel, 1).eq(DutyRecords::getId, id).update();
        if(update){
            return RestResponse.ok();
        }else {
            return RestResponse.no("failed to delete duty records");
        }
    }

    @Override
    public RestResponse update(DutyRecords dutyRecords) {
        boolean b = this.updateById(dutyRecords);
        if(b){
            return RestResponse.ok();
        }else {
            return RestResponse.no("failed to update duty records");
        }
    }

    @Override
    public RestResponse<List<DutyRecords>> selectList(DutyRecordsSelectListReq req) {
        log.error("-----------------------------------------------------------获取前端传的参数："+req.toString());
        List<DutyRecords> dutyRecords = this.baseMapper.selectListByParam(req);
        log.error("---------------------------------------------------查询后返回的结果集："+dutyRecords.toString());
        if(null != dutyRecords && dutyRecords.size()>0){
            return RestResponse.ok(dutyRecords);
        }else {
            return RestResponse.no("failed to selectList duty records");
        }
    }
}

