package com.cj.flood.func.modular.rollUpdate.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 滚动更新来水预报模型结果表(RollUpdateIncomingWater)表实体类
 *
 * @author makejava
 * @since 2024-07-19 14:59:57
 */
@Data
public class RollUpdateIncomingWater extends Model<RollUpdateIncomingWater> {
    
    private String id;
    
    private String programmeName;
    
    private Integer modelType;
    
    private Date predictionTime;
    
    private Integer periodTimeType;
    
    private Integer periodTimeStep;
    
    private Integer periodTimeNum;
    
    private String remark;
    
    private Date createTime;
    
    private String createBy;
    
    private String modelResultAddress;
    
    private Date endTime;
    
    private Integer status;
    //滚动更新id
    private String rollId;
}

