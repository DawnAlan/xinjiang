package com.cj.fourPredictions.func.modular.flood.video.common;

public interface VideoURL {

    /**
     * 分页获取区域列表
     */
    public static String GET_REGIONS = "/artemis/api/resource/v1/regions";

    /**
     * 根据区域编号获取下级监控点列表
     */
    public static String REGION_INDEX_CODE = "/artemis/api/resource/v1/regions/regionIndexCode/cameras";

    /**
     * 获取监控点预览取流URL
     */
    public static String GET_PREVIEW_URL = "/artemis/api/video/v1/cameras/previewURLs";

    public static String GET_CAMERAS = "/artemis/api/resource/v1/cameras";

    //云台
    public static String PTZ = "/artemis/api/video/v1/ptzs/controlling";

    //根据监控点编号进行3D放大
    public static String SEL_ZOOM = "/artemis/api/video/v1/ptzs/selZoom";
}
