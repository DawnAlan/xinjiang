package com.cj.waterresources.func.modular.waterResourceAllcation.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 水资源调配模型表(WaterResourceAllocation)表实体类
 *
 * @author makejava
 * @since 2023-11-14 17:34:50
 */
@Data
public class WaterResourceAllocation extends Model<WaterResourceAllocation> {
    //主键ID
    private String id;
    //方案名称
    private String schemeName;
    //时段类型(1-年逐月 2-月逐旬 3-旬逐日)
    private Integer bucketType;
    //配水开始时间
    private Date waterDistributionStartTime;
    //配水结束时间
    private Date waterDistributionEndTime;
    //配水类型(1-供水比例最大 2-供水缺额最小 3-单库调度)
    private Integer waterDistributionType;
    //来水数据地址
    private String inflowDataAddress;
    //来水数据名称
    private String inflowDataName;
    //需水数据地址
    private String needWaterDataAddress;
    //需水数据名称
    private String needWaterName;
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
    //逻辑删除(0-正常 1-删除)
    private Integer del;

}

