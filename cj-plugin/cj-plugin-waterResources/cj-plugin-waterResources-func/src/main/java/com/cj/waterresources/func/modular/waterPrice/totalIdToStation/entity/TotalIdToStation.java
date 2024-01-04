package com.cj.waterresources.func.modular.waterPrice.totalIdToStation.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 管理站对应的合计表(TotalIdToStation)表实体类
 *
 * @author makejava
 * @since 2023-12-08 18:01:10
 */
@Data
public class TotalIdToStation extends Model<TotalIdToStation> {
    
    private String totalId;
    
    private String station;

    private Integer useType;

    private String name;

}

