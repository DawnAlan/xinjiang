package com.cj.fourPredictions.func.modular.flood.video.bean.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegionIndexCodeVo implements Serializable {

    /**
     * cameraIndexCode : 90ad77d8057c43dab140b77361606927
     * gbIndexCode : 12000000051210000000
     * name : iDS-2CD9371-AES_神捕Camera 01
     * deviceIndexCode : f5da3e320bcb483da6bef4b3f86de779
     * longitude : 120.216123284763
     * latitude : 30.21168569675452
     * altitude : 88
     * pixel : 1
     * cameraType : 1
     * cameraTypeName : 半球
     * installPlace : 街道
     * matrixCode : 2076c586b0a94a6ba639b44eda9e76e9
     * chanNum : 1
     * viewshed : {"horizontalValue":"13.80000","azimuth":"109.23000","visibleRadius":"48.00000"}
     * capabilitySet : @vss@
     * capabilitySetName : 视频能力
     * intelligentSet : @face@
     * intelligentSetName : 人脸结构化能力
     * recordLocation : 0
     * recordLocationName : 中心存储
     * ptzController : 1
     * ptzControllerName : DVR
     * deviceResourceType : ENCODE_DEVICE
     * deviceResourceTypeName : 编码设备
     * channelType : digital
     * channelTypeName : 数字通道
     * transType : 0
     * transTypeName : UDP
     * updateTime : 2021-06-15T00:00:00.000+08:00
     * unitIndexCode : 083b2031c1db4f368f015fe2562e0012
     * treatyType : 20001
     * treatyTypeName : 公司SDK
     * createTime : 2021-06-15T00:00:00.000+08:00
     * status : 0
     * statusName : 不在线
     */

    private String cameraIndexCode;
    private String gbIndexCode;
    private String name;
    private String deviceIndexCode;
    private String longitude;
    private String latitude;
    private String altitude;
    private int pixel;
    private int cameraType;
    private String cameraTypeName;
    private String installPlace;
    private String matrixCode;
    private int chanNum;
    private String viewshed;
    private String capabilitySet;
    private String capabilitySetName;
    private String intelligentSet;
    private String intelligentSetName;
    private String recordLocation;
    private String recordLocationName;
    private int ptzController;
    private String ptzControllerName;
    private String deviceResourceType;
    private String deviceResourceTypeName;
    private String channelType;
    private String channelTypeName;
    private int transType;
    private String transTypeName;
    private String updateTime;
    private String unitIndexCode;
    private String treatyType;
    private String treatyTypeName;
    private String createTime;
    private int status;
    private String statusName;
}
