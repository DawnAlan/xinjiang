package com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;

import java.util.List;

/**
 * 水位站数据表(LzzGaugingStation)表服务接口
 *
 * @author makejava
 * @since 2023-12-05 17:55:46
 */
@DS("master")
public interface LzzGaugingStationService extends IService<LzzGaugingStation> {

    List<LzzGaugingStation> selectInfoByCondition(String id, String time, String startTime, String endTime);

}

