package dataExtraction.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dataExtraction.response.ResultObject;
import dataExtraction.response.ResultState;
import dataExtraction.service.RealTimeWaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/realTime")
@Tag(name = "实时水情预警")
public class RealTimeWaterController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RealTimeWaterService realTimeWaterService;

    @RequestMapping(value = "/queryRegionLevel", method = RequestMethod.GET)
    @Operation(summary = "查询区域所有节点实时数据")
    public ResultObject queryRegionLevel(@Parameter(description = "属性")  String typeName) {
        ResultObject resultObject = new ResultObject();
        try {
            JSONArray jsonArray = realTimeWaterService.queryRegionLevel(typeName);
            logger.info("查询区域所有节点实时数据成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(jsonArray);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询区域所有节点实时数据失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @Operation(summary = "保存数据")
    public ResultObject save(@Parameter(description = "数据JSON")  @RequestBody JSONObject jsonObject ){
        ResultObject resultObject = new ResultObject();
        try {
            return realTimeWaterService.save(jsonObject);
        }catch (Exception e){
            resultObject.setMessage("保存失败");
            resultObject.setState(ResultState.FAIL);
            return resultObject;
        }
    }

    @RequestMapping(value = "/queryRREarlyl", method = RequestMethod.GET)
    @Operation(summary = "查询预警区域")
    public ResultObject queryRREarlyl(@Parameter(description = "标识")  String typeName) {
        ResultObject resultObject = new ResultObject();
        try {
            JSONArray jsonArray = realTimeWaterService.queryRREarlyl(typeName);
            logger.info("查询预警区域成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(jsonArray);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询预警区域失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/queryForeignEarlyl", method = RequestMethod.GET)
    @Operation(summary = "对外查询预警区域")
    public ResultObject queryForeignEarlyl() {
        ResultObject resultObject = new ResultObject();
        try {
            JSONObject jsonObject = realTimeWaterService.queryForeignEarlyl();
            logger.info("查询预警区域成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(jsonObject);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询预警区域失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }


}
