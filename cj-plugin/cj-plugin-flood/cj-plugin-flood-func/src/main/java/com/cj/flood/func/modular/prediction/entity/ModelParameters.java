package com.cj.flood.func.modular.prediction.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.models.auth.In;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 陕北模型参数(ModelParameters)表实体类
 *
 * @author makejava
 * @since 2024-03-13 12:27:10
 */
@Data
@Builder
public class ModelParameters extends Model<ModelParameters> {
    
    private String id;
    //站点名称
    private String siteName;
    //面积
    private Double area;
    //透水系数
    private Double fb;
    //张力水蓄水容量
    private Double wm;
    //蒸散发折减系数
    private Double kc;
    //流域土壤稳定下渗率
    private Double fc;
    //流域土壤最大下渗率
    private Double fm;
    //霍尔顿下渗曲线方程
    private Double k;
    //下渗能力分布系数
    private Double b;
    //地面径流消退系数
    private Double cs;
    //汇流滞时
    private Integer l;
    //前期径流
    private Integer puelwpactdax;
    //降水系数
    private Double deriodiengthe;
    //时间
    private Date date;
    //合格率
    private Double rate;
    //关联id
    private Integer state;
    //备注
    private String remarks;
    }

