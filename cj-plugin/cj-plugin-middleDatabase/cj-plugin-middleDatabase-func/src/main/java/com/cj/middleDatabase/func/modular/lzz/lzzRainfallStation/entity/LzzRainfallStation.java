package com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * (LzzRainfallStation)表实体类
 *
 * @author makejava
 * @since 2023-12-05 17:56:37
 */
@Data
public class LzzRainfallStation extends Model<LzzRainfallStation> {
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
    //温度
    private BigDecimal temperature;

    //树结构ID
    private String treeId;


}

