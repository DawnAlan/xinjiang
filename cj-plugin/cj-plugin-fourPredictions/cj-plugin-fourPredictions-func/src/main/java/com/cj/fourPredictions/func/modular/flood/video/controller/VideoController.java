package com.cj.fourPredictions.func.modular.flood.video.controller;

import com.alibaba.fastjson.JSONObject;
import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.flood.video.bean.dto.GetRegionsDto;
import com.cj.fourPredictions.func.modular.flood.video.bean.dto.RegionIndexCodeDto;
import com.cj.fourPredictions.func.modular.flood.video.bean.vo.PtzVo;
import com.cj.fourPredictions.func.modular.flood.video.bean.vo.SelZoomVo;
import com.cj.fourPredictions.func.modular.flood.video.service.ArtemisPostForHd;
import com.cj.fourPredictions.func.modular.flood.video.service.ArtemisPostForHx;
import com.cj.fourPredictions.func.modular.flood.video.service.ArtemisPostForQs;
import com.cj.fourPredictions.func.modular.flood.video.service.ArtemisPostForReservoir;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags="防洪兴利-视频监控")
@Slf4j
@RestController
@RequestMapping("/video")
public class VideoController {

    @Resource
    private ArtemisPostForReservoir artemisPostForReservoir;

    @Resource
    private ArtemisPostForHd artemisPostForHd;

    @Resource
    private ArtemisPostForHx artemisPostForHx;

    @Resource
    private ArtemisPostForQs artemisPostForQs;

    @ApiOperation(value="防洪兴利-视频监控分页获取区域列表", notes="分页获取区域列表")
    @CommonLog(value = "防洪兴利-视频监控分页获取区域列表")
    @GetMapping(value = "/getRegions")
    public RestResponse getRegions(@RequestParam("type")String type,
                                   @RequestParam("pageNo")String pageNo,
                                   @RequestParam("pageSize")String pageSize){
        try {
            switch (type) {
                case "1":
                    GetRegionsDto reservoir = artemisPostForReservoir.get_regions(pageNo, pageSize);
                    return RestResponse.ok(reservoir);
                case "2":
                    GetRegionsDto hd = artemisPostForHd.get_regions(pageNo, pageSize);
                    return RestResponse.ok(hd);
                case "3":
                    GetRegionsDto hx = artemisPostForHx.get_regions(pageNo, pageSize);
                    return RestResponse.ok(hx);
                case "4":
                    GetRegionsDto qs = artemisPostForQs.get_regions(pageNo, pageSize);
                    return RestResponse.ok(qs);
                default:
                    return RestResponse.no("请传入对应的监控类型");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }

    @ApiOperation(value="防洪兴利-视频监控根据区域编号获取下级监控点列表", notes="根据区域编号获取下级监控点列表")
    @CommonLog(value = "防洪兴利-视频监控根据区域编号获取下级监控点列表")
    @GetMapping(value = "/regionIndexCode")
    public RestResponse regionIndexCode(@RequestParam("type")String type,
                                        @RequestParam("regionIndexCode") String regionIndexCode,
                                        @RequestParam("pageNo")String pageNo,
                                        @RequestParam("pageSize")String pageSize){
        try {
            switch (type) {
                case "1":
                    RegionIndexCodeDto reservoir = artemisPostForReservoir.region_index_code(regionIndexCode,pageNo,pageSize);
                    return RestResponse.ok(reservoir);
                case "2":
                    RegionIndexCodeDto hd = artemisPostForHd.region_index_code(regionIndexCode,pageNo,pageSize);
                    return RestResponse.ok(hd);
                case "3":
                    RegionIndexCodeDto hx = artemisPostForHx.region_index_code(regionIndexCode,pageNo,pageSize);
                    return RestResponse.ok(hx);
                case "4":
                    RegionIndexCodeDto qs = artemisPostForQs.region_index_code(regionIndexCode,pageNo,pageSize);
                    return RestResponse.ok(qs);
                default:
                    return RestResponse.no("请传入对应的监控类型");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }

    @ApiOperation(value="防洪兴利-视频监控获取监控点预览取流URL", notes="获取监控点预览取流URL")
    @CommonLog(value = "防洪兴利-视频监控获取监控点预览取流URL")
    @GetMapping(value = "/getPreviewUrl")
    public RestResponse getPreviewUrl(@RequestParam("type")String type,
                                      @RequestParam("cameraIndexCode") String cameraIndexCode){
        try {
            String regions = "";
            switch (type) {
                case "1":
                    regions = artemisPostForReservoir.get_preview_url(cameraIndexCode);
                case "2":
                    regions = artemisPostForHd.get_preview_url(cameraIndexCode);
                case "3":
                    regions = artemisPostForHx.get_preview_url(cameraIndexCode);
                case "4":
                    regions = artemisPostForQs.get_preview_url(cameraIndexCode);
                default:
                    regions =  "";
            }
            if(StringUtils.isNotEmpty(regions)){
                JSONObject jsonObject = JSONObject.parseObject(regions);
                JSONObject data = jsonObject.getJSONObject("data");
                String url = data.getString("url");
                return RestResponse.ok(url);
            }else {
                return RestResponse.no("请传入对应的监控类型");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }

    @ApiOperation(value="防洪兴利-云台", notes="云台")
    @CommonLog(value = "防洪兴利-云台")
    @PostMapping(value = "/ptz")
    public RestResponse ptz(@RequestParam("type")String type,
                            @RequestBody PtzVo vo){
        try {
            String regions = "";
            switch (type) {
                case "1":
                    regions = artemisPostForReservoir.ptz(vo);
                case "2":
                    regions = artemisPostForHd.ptz(vo);
                case "3":
                    regions = artemisPostForHx.ptz(vo);
                case "4":
                    regions = artemisPostForQs.ptz(vo);
                default:
                    regions =  "";
            }
            if(StringUtils.isNotEmpty(regions)){
                return RestResponse.ok(regions);
            }else {
                return RestResponse.no("请传入对应的监控类型");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }

    @ApiOperation(value="防洪兴利-根据监控点编号进行3D放大", notes="根据监控点编号进行3D放大")
    @CommonLog(value = "防洪兴利-根据监控点编号进行3D放大")
    @PostMapping(value = "/selZoom")
    public RestResponse selZoom(@RequestParam("type")String type,
                                @RequestBody SelZoomVo vo ){
        try {
            String regions = "";
            switch (type) {
                case "1":
                    regions = artemisPostForReservoir.selZoom(vo);
                case "2":
                    regions = artemisPostForHd.selZoom(vo);
                case "3":
                    regions = artemisPostForHx.selZoom(vo);
                case "4":
                    regions = artemisPostForQs.selZoom(vo);
                default:
                    regions =  "";
            }
            if(StringUtils.isNotEmpty(regions)){
                return RestResponse.ok(regions);
            }else {
                return RestResponse.no("请传入对应的监控类型");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }
}
