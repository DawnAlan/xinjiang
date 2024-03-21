package com.cj.model.func.modular.FloodPredict.entity;

import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import lombok.Data;

import java.util.List;

@Data
public class IrrigatedHydrologyParam {

    //小渠子雨量站
    private List<IrrigatedPlatformDataInfo> xqzGaugingStation;

    //团结一队雨量站
    private List<IrrigatedPlatformDataInfo> tjydGaugingStation;

    //头屯河水库雨量站
    private List<IrrigatedPlatformDataInfo> tthGaugingStation;

    //入库流量
    private List<IrrigatedPlatformDataInfo> tthInput;

}
