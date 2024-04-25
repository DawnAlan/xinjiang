package com.cj.fourPredictions.func.modular.flood.video.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SelZoomVo implements Serializable {
    /**
     *      "cameraIndexCode": "90ad77d8057c43dab140b77361606927",
     *     "startX": 22,
     *     "startY": 27,
     *     "endX": 55,
     *     "endY": 58
     */

    //监控点编号（通用唯一识别码UUID），通过#API@分页获取监控点资源@#获取。
    @ApiModelProperty(value = "监控点编号")
    private String cameraIndexCode;

    /**
     * 开始放大的X坐标，范围：0-255。由于设备比例限制，
     * 以及实际场景屏幕比例大小不同，请按照如下坐标位计算方式计算入参：
     * 屏幕X坐标/屏幕宽 * 255，即该坐标位X坐标占总屏幕宽的比例*255。
     * 监控点会对startX、startY、endX 、endY四点围成的区域进行放大。
     */
    @ApiModelProperty(value = "开始放大的X坐标")
    private Integer startX;

    /**
     * 开始放大的Y坐标，范围：0-255，由于设备比例限制，
     * 以及实际场景屏幕比例大小不同，请按照如下坐标位计算方式计算入参：
     * 屏幕Y坐标/屏幕高 * 255，即该坐标位Y坐标占总屏幕高的比例*255。
     * 监控点会对startX、startY、endX 、endY四点围成的区域进行放大
     */
    @ApiModelProperty(value = "开始放大的Y坐标")
    private Integer startY;

    //结束放大的X坐标，范围以及计算方式同startX
    @ApiModelProperty(value = "结束放大的X坐标，范围以及计算方式同startX")
    private Integer endX;

    //结束放大的Y坐标，范围以及计算方式同startY
    @ApiModelProperty(value = "结束放大的Y坐标，范围以及计算方式同startY")
    private Integer endY;
}
