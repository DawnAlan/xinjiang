package com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * (IrrigatedPlatformTree)表实体类
 *
 * @author makejava
 * @since 2023-12-06 10:37:44
 */
@Data
public class IrrigatedPlatformTree extends Model<IrrigatedPlatformTree> {
    
    private String id;
    
    private String name;
    
    private String parentId;
    
    private String beginTime;
    
    private String beginTimeMark;
    
    private String elevation;
    
    private String isWaterLevel;
    
    private String locationType;
    
    private String locationTypeName;
    
    private String measureType;
    
    private String monitorType;
    
    private String nodetype;
    
    private String selfCode;
    
    private String waterlevelNotnormal;
}

