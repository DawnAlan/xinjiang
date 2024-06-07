package com.cj.flood.func.modular.dispatch.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 防洪调度表(FloodControlOperation)表实体类
 *
 * @author makejava
 * @since 2023-11-09 15:49:45
 */
@Data
public class FloodControlOperation extends Model<FloodControlOperation> {
    //主键ID
    private String id;
    //方案名称
    private String schemeName;
    //预报方案ID
    private String forecastingSchemeId;
    //创建人
    private String createBy;
    //创建时间
    private Date createTime;
    //方案结果地址
    private String modelResultAddress;

    //方案生成状态(1-生成中 2-已生成)
    private Integer status;

    private String remark;

    private Date forecastingTime;

    private String forecastingSchemeName;

}

