package com.cj.middleDatabase.func.modular.rainfallStation.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * (RainfallStation)表实体类
 *
 * @author makejava
 * @since 2023-11-23 15:25:48
 */
@Data
public class RainfallStation extends Model<RainfallStation> {
    //主键ID
    private String id;
    //雨量站名称
    private String stationName;
    //降雨量
    private BigDecimal rainfall;
    //采集时间
    private Date time;
    //采集年度
    private String year;
}

