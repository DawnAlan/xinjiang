package com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.req.UseWaterManagementQueryReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.bean.res.UseWaterManagementQueryRes;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.entity.UseWaterManagement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用水单位管理(UseWaterManagement)表数据库访问层
 *
 * @author makejava
 * @since 2023-11-28 17:14:41
 */
public interface UseWaterManagementMapper extends BaseMapper<UseWaterManagement> {

    List<UseWaterManagementQueryRes> selectListByReq(@Param("req") UseWaterManagementQueryReq req);

}

