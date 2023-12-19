package com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 渠首管理站总计表(CanalHeadManagementStationTotal)表实体类
 *
 * @author makejava
 * @since 2023-12-15 18:08:09
 */
@Data
public class CanalHeadManagementStationTotal extends Model<CanalHeadManagementStationTotal> {
    //主键ID
    private String id;
    //年度
    private Integer year;
    //月份
    private Integer month;
    //旬
    private String tenDays;
    //合计
    private Double amountTo;
    //本旬水量
    private Double currentWaterVolume;
    //上旬水量
    private Double waterVolumeFirstTenDays;
    //累计水量
    private Double accumulatedWaterVolume;
    //应交水费
    private Double payableWaterFee;
    //预交水费
    private Double advancePaymentWaterFee;
    //水费余(欠)
    private Double unpaidWaterFees;
    //去年同期水量
    private Double waterVolumeDuringLastYear;
    //备注
    private String remark;
    //创建时间
    private Date createTime;
    //创建人
    private String createBy;
    //更新时间
    private Date updateTime;
    //更新人
    private String updateBy;
    //逻辑删除(0-否 1-是)
    private Integer del;
    //表头映射
    private String code;
}

