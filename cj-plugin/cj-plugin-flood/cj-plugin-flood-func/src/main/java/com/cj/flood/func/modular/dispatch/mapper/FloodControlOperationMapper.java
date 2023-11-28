package com.cj.flood.func.modular.dispatch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cj.flood.func.modular.dispatch.bean.req.FloodControlOperationListReq;
import com.cj.flood.func.modular.dispatch.bean.res.FloodControlOperationListRes;
import com.cj.flood.func.modular.dispatch.entity.FloodControlOperation;
import org.apache.ibatis.annotations.Param;

/**
 * 防洪调度表(FloodControlOperation)表数据库访问层
 *
 * @author makejava
 * @since 2023-11-09 15:49:43
 */
public interface FloodControlOperationMapper extends BaseMapper<FloodControlOperation> {

    IPage<FloodControlOperationListRes> selectFloodControlOperationList(@Param(value = "req") FloodControlOperationListReq req, IPage<FloodControlOperationListRes> page);

}

