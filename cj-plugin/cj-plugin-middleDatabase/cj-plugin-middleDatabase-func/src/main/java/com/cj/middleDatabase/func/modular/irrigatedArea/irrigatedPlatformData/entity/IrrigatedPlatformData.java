package com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformData.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 灌区平台所有数据(IrrigatedPlatformData)表实体类
 *
 * @author makejava
 * @since 2023-12-06 12:34:00
 */
@Data
public class IrrigatedPlatformData extends Model<IrrigatedPlatformData> {
    
    private String regionId;
    
    private Double voltage;
    
    private Double pTotalFlow;
    
    private String userId;
    
    private String downWater;
    
    private String igCo;
    
    private Double monitorFlow;
    
    private String monitorId;
    
    private String operateTime;
    
    private String monitorTime;

    private String monitorName;

    private String gateOpenHoles;
    
    private String id;
    
    private String remark;
    
    private Double water;
    
    private Double capacity;
    
    private Double nTotalFlow;
    
    private String isSurpass;
    
    private String userName;
    
    private Double inputFlow;
    
    private Double gateHeight;
    
    private Double waterLevel;
    
    private Double totalFlow;
    
    private Double monitorFlowRate;
    
    private String igSp;
    
    private String downLevel;
    
    private String gateHeightShow;

    private String isNullPipe;

    private String pipePressure;

}

