package com.cj.flood.func.modular.prediction.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * (ModelParametersDetail)表实体类
 *
 * @author makejava
 * @since 2024-04-19 14:26:51
 */
@Data
@Builder
public class ModelParametersDetail extends Model<ModelParametersDetail> {
    
    private String id;
    
    private Date time;
    
    private Double historyFlow;
    
    private Double preParamFlow;
    
    private Double newParamFlow;
    
    private String parentId;

    private Double preParamRainfall;
}

