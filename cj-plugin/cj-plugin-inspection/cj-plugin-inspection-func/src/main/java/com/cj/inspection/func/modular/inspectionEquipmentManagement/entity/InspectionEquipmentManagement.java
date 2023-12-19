package com.cj.inspection.func.modular.inspectionEquipmentManagement.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 巡查设备管理表(InspectionEquipmentManagement)表实体类
 *
 * @author makejava
 * @since 2023-12-07 19:52:41
 */
@Data
public class InspectionEquipmentManagement extends Model<InspectionEquipmentManagement> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //设备编号
    @ApiModelProperty(value = "设备编号")
    private String deviceCode;

    //设备名称
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    //设备类型
    @ApiModelProperty(value = "设备类型")
    private String deviceType;

    //所属单位
    @ApiModelProperty(value = "所属单位")
    private String affiliatedUnit;

    //最近维护时间
    @ApiModelProperty(value = "最近维护时间")
    private Date recentMaintenanceTime;

    //最迟维护时间
    @ApiModelProperty(value = "区域")
    private Date latestMaintenanceTime;

    //二维码地址
    @ApiModelProperty(value = "二维码地址")
    private String codePath;
}

