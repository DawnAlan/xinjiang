package com.cj.model.func.modular.FloodPredict.Calibration.entity;

import com.cj.model.func.modular.FloodPredict.entity.IrrigatedHydrologyParam;
import com.cj.model.func.modular.FloodPredict.entity.LzzHydrologyParam;
import lombok.Data;

import java.util.Date;


@Data
public class OneCalibrationParam {
    //模型类型(true-自动率定，false-人工率定)
    private Boolean isAutomatic;
    //断面位置
    private String location;
    //率定开始时间
    private Date startTime;
    //率定结束时间
    private Date endTime;
    //人工率定站点参数
    private ShanbeiParam manualParam;
    //选择的站点参数
    private ShanbeiParam historyParam;
    //楼庄子历史数据
    private LzzHydrologyParam lzzHydrologyParam;
    //灌区实时雨量站信息
    private IrrigatedHydrologyParam irrigatedHydrologyParam;
    //文件存储路径
    private String filePath;
}
