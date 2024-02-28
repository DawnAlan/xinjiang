package dataExtraction.controller;

import com.alibaba.fastjson.JSONArray;
import dataExtraction.response.ResultObject;
import dataExtraction.response.ResultState;
import dataExtraction.service.RainfallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/rainfall")
@Tag(name = "雨量信息展示")
public class RainfallController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RainfallService rainfallService;

    @RequestMapping(value = "/queryRainfall", method = RequestMethod.GET)
    @Operation(summary = "查询水库下的雨量站")
    public ResultObject queryRainfall(@Parameter(description = "类型")  String typeName,
                                      @Parameter(description = "时间")  String time) {
        ResultObject resultObject = new ResultObject();
        try {
            JSONArray jsonArray = rainfallService.queryRainfall(typeName,time);
            logger.info("查询雨量站信息成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(jsonArray);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询雨量站信息失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/queryRainfallCurve", method = RequestMethod.GET)
    @Operation(summary = "查询水库下的雨量站曲线图")
    public ResultObject queryRainfallCurve(@Parameter(description = "节点ID")  String ndcdId,
                                           @Parameter(description = "开始时间") String beginTime,
                                           @Parameter(description = "结束时间") String endTime) {
        ResultObject resultObject = new ResultObject();
        try {
            JSONArray jsonArray = rainfallService.queryRainfallCurve(ndcdId,beginTime,endTime);
            logger.info("查询雨量信息成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(jsonArray);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询雨量信息失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    /*********************************************雨量统计查询**************************************************/

    @RequestMapping(value = "/queryHourRainfall", method = RequestMethod.GET)
    @Operation(summary = "查询日雨量信息")
    public ResultObject queryHourRainfall(@Parameter(description = "节点标识")  String typeName,
                                          @Parameter(description = "时间") String time,
                                          @Parameter(description = "小时") Integer hour) {
        ResultObject resultObject = new ResultObject();
        try {
            JSONArray jsonArray = rainfallService.queryHourRainfall(typeName,time,hour);
            logger.info("查询日雨量信息成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(jsonArray);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询日雨量信息失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/queryTenDayRainfall", method = RequestMethod.GET)
    @Operation(summary = "查询旬雨量信息")
    public ResultObject queryTenDayRainfall(@Parameter(description = "节点标识")  String typeName,
                                            @Parameter(description = "时间") String time,
                                            @Parameter(description = "上中下旬") Integer ten) {
        ResultObject resultObject = new ResultObject();
        try {
            JSONArray jsonArray = rainfallService.queryTenDayRainfall(typeName,time,ten);
            logger.info("查询旬雨量信息成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(jsonArray);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询旬雨量信息失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/queryYearDayRainfall", method = RequestMethod.GET)
    @Operation(summary = "查询年逐日雨量信息")
    public ResultObject queryYearDayRainfall(@Parameter(description = "节点Id")  String ndcdId,
                                             @Parameter(description = "时间") String time) {
        ResultObject resultObject = new ResultObject();
        try {
            Map<Object,Object> jsonArray = rainfallService.queryYearDayRainfall(ndcdId,time);
            logger.info("查询旬雨量信息成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(jsonArray);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询旬雨量信息失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/queryYearMonthRainfall", method = RequestMethod.GET)
    @Operation(summary = "查询年逐月雨量信息")
    public ResultObject queryYearMonthRainfall(@Parameter(description = "节点标识")  String typeName,
                                               @Parameter(description = "时间") String time,
                                               @Parameter(description = "月份") String month) {
        ResultObject resultObject = new ResultObject();
        try {
            JSONArray jsonArray = rainfallService.queryYearMonthRainfall(typeName,time,month);
            logger.info("查询旬雨量信息成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(jsonArray);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询旬雨量信息失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

}
