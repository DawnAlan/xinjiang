package com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.bean.req.WaterPriceSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.bean.res.WaterPriceSelectListRes;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.entity.WaterPriceManagement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 水价管理(WaterPriceManagement)表数据库访问层
 *
 * @author makejava
 * @since 2023-11-29 10:44:39
 */
public interface WaterPriceManagementMapper extends BaseMapper<WaterPriceManagement> {

    List<WaterPriceSelectListRes> waterPriceSelectList(@Param("req") WaterPriceSelectListReq req);
}

