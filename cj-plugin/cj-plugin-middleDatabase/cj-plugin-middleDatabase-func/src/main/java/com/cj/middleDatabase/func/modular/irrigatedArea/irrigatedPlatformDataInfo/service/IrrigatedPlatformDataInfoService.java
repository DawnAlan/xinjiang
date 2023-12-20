package com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.bean.res.SelectInfoByIrrigationNameListRes;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;

import java.util.List;

/**
 * 灌区平台时刻信息表(IrrigatedPlatformDataInfo)表服务接口
 *
 * @author makejava
 * @since 2023-12-06 19:26:07
 */
public interface IrrigatedPlatformDataInfoService extends IService<IrrigatedPlatformDataInfo> {

    List<SelectInfoByIrrigationNameListRes> selectInfoByIrrigationNameList(String[] name);

    List<IrrigatedPlatformDataInfo> selectInfoByCondition(String id,String time,String startTime,String endTime);
    IrrigatedPlatformDataInfo selectOneByCondition(String name,String time);
}

