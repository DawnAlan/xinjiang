package com.cj.model.func.modular.FloodPredict.Calibration.entity;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.model.func.modular.FloodPredict.entity.RainFallDto;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class CalibrationParam {
    //模型类型(true-自动率定，false-人工率定)
    private Boolean isAutomatic;
    //率定开始时间
    private Date startTime;
    //率定结束时间
    private Date endTime;
    //人工率定站点参数
    private Map<String,ShanbeiParam> manualParam;
    //选择的站点参数
    private Map<String,ShanbeiParam> historyParam;
    //雨量站信息
    private Map<String, List<RainFallDto>>rainfall;
    //水位站信息
    private Map<String,List<LzzGaugingStation>> waterLevel;
    //文件存储路径
    private String filePath;
}
