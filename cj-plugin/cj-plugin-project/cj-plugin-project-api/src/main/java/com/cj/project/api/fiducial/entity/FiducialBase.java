/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package com.cj.project.api.fiducial.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cj.common.pojo.CommonEntity;
import com.fhs.core.trans.vo.TransPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 测点考证表实体
 *
 * @author Lb
 * @date  2023/09/04 12:25
 **/
@Getter
@Setter
@TableName("fiducial_base")
public class FiducialBase extends CommonEntity implements TransPojo {

    /** ID */
    @TableId
    @ApiModelProperty(value = "ID", position = 1, notes = "base_field")
    private String id;
    /* base_field、status_field、position_field、param_field  */

    /** 项目编号 */
    @ApiModelProperty(value = "项目编号", position = 2, notes = "base_field")
    private String projectCode;

    /** 项目仪器类型ID */
    @ApiModelProperty(value = "项目仪器类型ID", position = 3, notes = "base_field")
    private String instrumentType;

    /** 测点编号 */
    @ApiModelProperty(value = "测点编号", position = 4, notes = "base_field")
    private String pointName;

    /** 测点别名 */
    @ApiModelProperty(value = "测点别名", position = 5, notes = "base_field")
    private String pointAlias;

    /** 传感器类型 */
    @ApiModelProperty(value = "传感器类型", position = 6, notes = "base_field")
    private String sensorType;

    /** 测点类型:人工|自动化 */
    @ApiModelProperty(value = "测点类型:人工|自动化", position = 7, notes = "base_field")
    private String recordMethod;

    /** 测点状态 */
    @ApiModelProperty(value = "测点状态", position = 8, notes = "status_field")
    private String remark;

    /** 重要等级 */
    @ApiModelProperty(value = "重要等级", position = 8, notes = "status_field")
    private String importance;

    /** 分项工程 */
    @ApiModelProperty(value = "分项工程", position = 9, notes = "position_field")
    private String subProject;

    /** 分部工程 */
    @ApiModelProperty(value = "分部工程", position = 10, notes = "position_field")
    private String itemProject;

    /** 坐标X */
    @ApiModelProperty(value = "坐标X", position = 11, notes = "position_field")
    private String coordinateX;

    /** 坐标Y */
    @ApiModelProperty(value = "坐标Y", position = 12, notes = "position_field")
    private String coordinateY;

    /** 高程 */
    @ApiModelProperty(value = "高程", position = 13, notes = "position_field")
    private String elevation;

    /** 桩号 */
    @ApiModelProperty(value = "桩号", position = 14, notes = "position_field")
    private String mileage;

    /** 断面 */
    @ApiModelProperty(value = "断面", position = 15, notes = "position_field")
    private String monitorSection;

    /** 观测频次 */
    @ApiModelProperty(value = "观测频次", position = 16, notes = "status_field")
    private String observeFrequency;

    /** 仪器编号 */
    @ApiModelProperty(value = "仪器编号", position = 17, notes = "param_field")
    private String deviceCode;

    /** 仪器厂家 */
    @ApiModelProperty(value = "仪器厂家", position = 18, notes = "param_field")
    private String factoryName;

    /** 仪器型号 */
    @ApiModelProperty(value = "仪器型号", position = 19, notes = "param_field")
    private String modelName;

    /** 传感器名称 */
    @ApiModelProperty(value = "传感器名称", position = 20, notes = "param_field")
    private String sensorName;

    /** 传感器编号 */
    @ApiModelProperty(value = "传感器编号", position = 21, notes = "param_field")
    private String sensorCode;

    /** 传感器厂家 */
    @ApiModelProperty(value = "传感器厂家", position = 22, notes = "param_field")
    private String sensorFactory;

    /** 是否在用 */
    @ApiModelProperty(value = "是否在用", position = 23, notes = "status_field")
    private String enableFlag;

    /** 安装时间 */
    @ApiModelProperty(value = "安装时间", position = 24, notes = "status_field")
    private String installTime;

    /** 失效时间 */
    @ApiModelProperty(value = "失效时间", position = 25, notes = "status_field")
    private String invalidTime;

    /** 首次数据时间 */
    @ApiModelProperty(value = "首次数据时间", position = 26, notes = "status_field")
    private String firstdataTime;

    /** 最后数据时间 */
    @ApiModelProperty(value = "最后数据时间", position = 27, notes = "status_field")
    private String lastdataTime;

    /** 备注 */
    @ApiModelProperty(value = "备注", position = 28, notes = "status_field")
    private String extJson;


}
