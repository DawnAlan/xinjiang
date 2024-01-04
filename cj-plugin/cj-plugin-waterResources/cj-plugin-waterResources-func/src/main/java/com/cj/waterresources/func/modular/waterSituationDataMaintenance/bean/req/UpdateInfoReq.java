package com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req;

import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateInfoReq {

    @ApiModelProperty(value = "修改头屯河灌区数据实体类")
    private IrrigatedPlatformDataInfo irrigatedPlatformDataInfo;

    @ApiModelProperty(value = "修改楼庄子水库数据实体类")
    private LzzGaugingStation lzzGaugingStation;

    @ApiModelProperty(value = "修改楼庄子雨量站数据实体类")
    private LzzRainfallStation lzzRainfallStation;


}
