package dataExtraction.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dataExtraction.ghd.entity.WpdCurvedAss;
import dataExtraction.response.ResultObject;
import dataExtraction.response.ResultState;
import dataExtraction.service.WpdCurvedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/wpdCurved")
public class WpdCurvedController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    WpdCurvedService wpdCurvedService;

    @RequestMapping(value = "/addCurve", method = RequestMethod.POST)
    @Operation(summary = "新增历史曲线关系")
    public ResultObject addCurve(@RequestBody JSONObject jsonObject) {
        ResultObject resultObject = new ResultObject();
        try {
            WpdCurvedAss wpdCuAss = wpdCurvedService.addCurve(jsonObject);
            logger.info("新增曲线关系成功");
            resultObject.setMessage("成功");
            resultObject.setData(wpdCuAss);
            return resultObject;
        }catch (Exception e) {
            logger.error("新增曲线关系失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/modify", method = RequestMethod.GET)
    @Operation(summary = "应用当前曲线")
    public ResultObject modify(@Parameter(description = "水库Id")  String id,
                               @Parameter(description = "雨量站ID")  String ndcdId) {
        ResultObject resultObject = new ResultObject();
        try {
            WpdCurvedAss wpdCuAss = wpdCurvedService.modify(id,ndcdId);
            logger.info("应用曲线成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(wpdCuAss);
            return resultObject;
        }catch (Exception e) {
            logger.error("应用曲线成功失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/queryQuXT", method = RequestMethod.GET)
    @Operation(summary = "根据选择历史曲线ID查询曲线信息")
    public ResultObject queryQuXT(@Parameter(description = "水库Id")  String id) {
        ResultObject resultObject = new ResultObject();
        try {
            JSONObject jsonObject = wpdCurvedService.queryQuXT(id);
            logger.info("查询曲线信息成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(jsonObject);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询曲线信息失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/dropDown", method = RequestMethod.GET)
    @Operation(summary = "查询历史曲线下拉列表")
    public ResultObject dropDown(@Parameter(description = "设备ID")  String ndcdId) {
        ResultObject resultObject = new ResultObject();
        try {
            JSONArray jsonArray = wpdCurvedService.dropDown(ndcdId);
            logger.info("查询成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(jsonArray);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/regimenShowQS", method = RequestMethod.GET)
    @Operation(summary = "查询库容曲线-水位流量")
    public ResultObject regimenShowQS(@Parameter(description = "设备ID")  String ndcdId) {
        ResultObject resultObject = new ResultObject();
        try {
            JSONObject jsonObject = wpdCurvedService.regimenShowQS(ndcdId);
            logger.info("查询水位流量成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(jsonObject);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询水位流量失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/queryLevelFlow", method = RequestMethod.GET)
    @Operation(summary = "水位查询流量")
    public ResultObject queryLevelFlow(@Parameter(description = "记录ID")  String ndcdId,
                                       @Parameter(description = "水位")  double level) {
        ResultObject resultObject = new ResultObject();
        try {
            BigDecimal object = wpdCurvedService.queryLevelFlow(ndcdId,level);
            logger.info("查询成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(object);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

}
