package com.cj.waterresources.func.modular.waterPrice.waterDistributionRatio.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 配水比例(WaterDistributionRatio)表实体类
 *
 * @author makejava
 * @since 2023-12-15 18:08:31
 */
@Data
public class WaterDistributionRatio extends Model<WaterDistributionRatio> {
    //主键ID
    private String id;
    //管理站
    private String station;
    //年
    private Integer year;
    //月
    private Integer month;
    //旬
    private String tenDays;
    //表头ID
    private String tableBeadId;
    //配水值
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double v;

    private String frontTableList;
}

