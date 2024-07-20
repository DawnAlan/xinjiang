package com.cj.flood.func.modular.rollUpdate.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * (RollUpdateFloodControl)表实体类
 *
 * @author makejava
 * @since 2024-07-19 14:59:38
 */
@Data
public class RollUpdateFloodControl extends Model<RollUpdateFloodControl> {
    
    private String id;
    
    private String schemeName;
    
    private String forecastingSchemeId;
    
    private String createBy;
    
    private Date createTime;
    
    private String modelResultAddress;
    
    private Integer status;
    
    private String remark;
    
    private String forecastingSchemeName;
    
    private Date forecastingTime;
    //滚动更新id
    private String rollId;
}

