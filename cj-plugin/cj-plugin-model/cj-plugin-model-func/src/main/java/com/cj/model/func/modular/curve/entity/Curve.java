package com.cj.model.func.modular.curve.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName CURVE
 */
@TableName(value ="CURVE")
@Data
public class Curve implements Serializable {
    /**
     * 
     */
    @TableField(value = "ID")
    private Integer id;

    /**
     * 
     */
    @TableField(value = "NAME")
    private String name;

    /**
     * 
     */
    @TableField(value = "CURVE_CODE")
    private Integer curveCode;

    /**
     * 
     */
    @TableField(value = "TYPE")
    private String type;

    /**
     * 
     */
    @TableField(value = "WATER_LEVEL")
    private Double waterLevel;

    /**
     * 
     */
    @TableField(value = "VALUE")
    private Double value;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}