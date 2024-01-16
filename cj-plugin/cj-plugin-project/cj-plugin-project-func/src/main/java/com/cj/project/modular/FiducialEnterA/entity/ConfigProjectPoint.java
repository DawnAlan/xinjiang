package com.cj.project.modular.FiducialEnterA.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * FiducialEnterA实体
 *
 * @author Lb
 * @date  2023/11/23 10:20
 **/
@Getter
@Setter
@TableName("config_projectpoint")
public class ConfigProjectPoint {

    /** ID */
    @TableId
    @ApiModelProperty(value = "ID", position = 1)
    private Integer id;

    /** PROJECTCODE */
    @ApiModelProperty(value = "PROJECTCODE", position = 2)
    private String projectcode;

    /** POINTTYPE */
    @ApiModelProperty(value = "POINTTYPE", position = 3)
    private String pointtype;

    /** POINTID */
    @ApiModelProperty(value = "POINTID", position = 4)
    private String pointid;

    /** POINTNAME */
    @ApiModelProperty(value = "POINTNAME", position = 5)
    private String pointname;

    /** GZFS */
    @ApiModelProperty(value = "GZFS", position = 6)
    private String gzfs;

    /** GZZT */
    @ApiModelProperty(value = "GZZT", position = 7)
    private String gzzt;

    /** ZDJK */
    @ApiModelProperty(value = "ZDJK", position = 8)
    private String zdjk;

    /** STARTTIME */
    @ApiModelProperty(value = "STARTTIME", position = 9)
    private Date starttime;

    /** ZH */
    @ApiModelProperty(value = "ZH", position = 10)
    private String zh;

    /** AZGC */
    @ApiModelProperty(value = "AZGC", position = 11)
    private String azgc;

    /** SCCJ */
    @ApiModelProperty(value = "SCCJ", position = 12)
    private String sccj;

    /** ISRENAME */
    @ApiModelProperty(value = "ISRENAME", position = 13)
    private String isrename;

    /** RENAME */
    @ApiModelProperty(value = "REPOINTNAME", position = 14)
    private String repointname;

    /** INSTRUMENT_TYPE */
    @ApiModelProperty(value = "INSTRUMENT_TYPE", position = 15)
    private String instrumentType;

    /** INSTRUMENT_NAME */
    @ApiModelProperty(value = "INSTRUMENT_NAME", position = 16)
    private String instrumentName;

    /** INSTRUMENT_METATYPE */
    @ApiModelProperty(value = "INSTRUMENT_METATYPE", position = 17)
    private String instrumentMetatype;

    /** POINTITEMPROJECT */
    @ApiModelProperty(value = "POINTITEMPROJECT", position = 18)
    private String pointitemproject;
}
