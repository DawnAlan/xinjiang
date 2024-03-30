package com.cj.fourPredictions.func.modular.flood.video.controller;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.flood.video.bean.dto.GetRegionsDto;
import com.cj.fourPredictions.func.modular.flood.video.bean.dto.RegionIndexCodeDto;
import com.cj.fourPredictions.func.modular.flood.video.service.ArtemisPost;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags="防洪兴利-视频监控")
@Slf4j
@RestController
@RequestMapping("/video")
public class VideoController {

    @Resource
    private ArtemisPost artemisPost;

    @ApiOperation(value="防洪兴利-视频监控分页获取区域列表", notes="分页获取区域列表")
    @CommonLog(value = "防洪兴利-视频监控分页获取区域列表")
    @GetMapping(value = "/getRegions")
    public RestResponse getRegions(@RequestParam("pageNo")String pageNo, @RequestParam("pageSize")String pageSize){
        try {
            GetRegionsDto regions = artemisPost.get_regions(pageNo, pageSize);
            return RestResponse.ok(regions);
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }

    @ApiOperation(value="防洪兴利-视频监控根据区域编号获取下级监控点列表", notes="根据区域编号获取下级监控点列表")
    @CommonLog(value = "防洪兴利-视频监控根据区域编号获取下级监控点列表")
    @GetMapping(value = "/regionIndexCode")
    public RestResponse regionIndexCode(@RequestParam("regionIndexCode") String regionIndexCode,
                                        @RequestParam("pageNo")String pageNo,
                                        @RequestParam("pageSize")String pageSize){
        try {
            RegionIndexCodeDto regionIndexCodeDto = artemisPost.region_index_code(regionIndexCode,pageNo,pageSize);
            return RestResponse.ok(regionIndexCodeDto);
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }

    @ApiOperation(value="防洪兴利-视频监控获取监控点预览取流URL", notes="获取监控点预览取流URL")
    @CommonLog(value = "防洪兴利-视频监控获取监控点预览取流URL")
    @GetMapping(value = "/getPreviewUrl")
    public RestResponse getPreviewUrl(@RequestParam("cameraIndexCode") String cameraIndexCode){
        try {
            String regions = artemisPost.get_preview_url(cameraIndexCode);
            JSONObject jsonObject = JSONObject.parseObject(regions);
            JSONObject data = jsonObject.getJSONObject("data");
            String url = data.getString("url");
            return RestResponse.ok(url);
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }

    @ApiOperation(value="防洪兴利-视频监控分页禁获取监控点资源", notes="分页禁获取监控点资源")
    @CommonLog(value = "防洪兴利-视频监控分页禁获取监控点资源")
    @GetMapping(value = "/getCameras")
    public RestResponse getCameras(@RequestParam("pageNo")String pageNo,@RequestParam("pageSize")String pageSize){
        try {
            String regions = artemisPost.get_cameras(pageNo, pageSize);
            return RestResponse.ok(regions);
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }
}
