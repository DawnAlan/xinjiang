package com.cj.fourPredictions.func.modular.flood.video.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PtzVo implements Serializable {
            /*"cameraIndexCode": "90ad77d8057c43dab140b77361606927",
            "action": 0,
            "command": "up",
            "speed": 40,
            "presetIndex": 20*/

    //监控点编号（通用唯一识别码UUID），通过#API@分页获取监控点资源@#获取。
    @ApiModelProperty(value = "监控点编号")
    private String cameraIndexCode;

    //开始或停止操作(0 开始 1 停止)
    @ApiModelProperty(value = "开始或停止操作(0 开始 1 停止)")
    private Integer action;

    /**
     * 控制命令(不区分大小写) 说明： LEFT 左转 RIGHT 右转 UP 上转 DOWN
     * 下转 ZOOM_IN 焦距变大 ZOOM_OUT 焦距变小 LEFT_UP 左上 LEFT_DOWN
     * 左下 RIGHT_UP 右上 RIGHT_DOWN 右下 FOCUS_NEAR 焦点前移 FOCUS_FAR
     * 焦点后移 IRIS_ENLARGE 光圈扩大 IRIS_REDUCE 光圈缩小 以下命令presetIndex
     * 不可为空： GOTO_PRESET到预置点
     */
    @ApiModelProperty(value = "控制命令")
    private String command;

    //云台速度(取值范围1-100,默认40)
    @ApiModelProperty(value = "云台速度(取值范围1-100,默认40)")
    private Integer speed;

    //预置点编号(取值范围为1-128)
    @ApiModelProperty(value = "预置点编号(取值范围为1-128)")
    private Integer presetIndex;
}
