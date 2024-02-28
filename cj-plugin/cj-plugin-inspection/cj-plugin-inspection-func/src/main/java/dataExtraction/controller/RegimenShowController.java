package dataExtraction.controller;

import com.alibaba.fastjson.JSONObject;
import dataExtraction.response.ResultObject;
import dataExtraction.response.ResultState;
import dataExtraction.service.RegimenShowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/regimenShow")
@Tag(name = "水情信息展示")
public class RegimenShowController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RegimenShowService regimenShowService;

    @RequestMapping(value = "/queryDeviceList", method = RequestMethod.GET)
    @Operation(summary = "根据设备类型查询对应的水位流量信息")
    public ResultObject queryDeviceList(@Parameter(description = "类型")  String typeName,
                                        @Parameter(description = "监测时间") String time) {
        ResultObject resultObject = new ResultObject();
        try {
            List<Map<String,Object>> list = regimenShowService.queryDeviceList(typeName,time);
            logger.info("查询水位流量信息成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(list);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询水位流量信息失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/queryLevel", method = RequestMethod.GET)
    @Operation(summary = "查询水位流量曲线图")
    public ResultObject queryLevel(@Parameter(description = "设备ID")  String deviceId,
                                       @Parameter(description = "开始时间") String beginTime,
                                       @Parameter(description = "结束时间") String endTime) {
        ResultObject resultObject = new ResultObject();
        try {
            Map<String,Object> map = regimenShowService.queryLevel(deviceId,beginTime,endTime);
            logger.info("查询监测站水情信息成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(map);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询监测站水情信息失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/historyQueryRain", method = RequestMethod.GET)
    @Operation(summary = "查询监测站水情信息历史数据对比")
    public ResultObject historyQueryRain(@Parameter(description = "雨量站ID")  String ndcdId,
                                         @Parameter(description = "开始时间") String beginTime,
                                         @Parameter(description = "结束时间") String endTime,
                                         @Parameter(description = "类型") String type,
                                         @Parameter(description = "时间类型") String timeType) {
        ResultObject resultObject = new ResultObject();
        try {
            JSONObject jsonObject = regimenShowService.historyQueryRain(ndcdId,beginTime,endTime,type,timeType);
            logger.info("查询历史数据对比成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(jsonObject);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询历史数据对比失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/historyQueryRainLn", method = RequestMethod.GET)
    @Operation(summary = "查询监测站水情信息历史数据对比历年")
    public ResultObject historyQueryRainLn(@Parameter(description = "雨量站ID")  String ndcdId,
                                         @Parameter(description = "开始时间") String beginTime,
                                         @Parameter(description = "结束时间") String endTime,
                                         @Parameter(description = "对比时间") String time,
                                         @Parameter(description = "类型") String type,
                                         @Parameter(description = "时间类型") String timeType) {
        ResultObject resultObject = new ResultObject();
        try {
            JSONObject jsonObject = regimenShowService.historyQueryRainLn(ndcdId,beginTime,endTime,time,type,timeType);
            logger.info("查询历史数据对比成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(jsonObject);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询历史数据对比失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }



}
