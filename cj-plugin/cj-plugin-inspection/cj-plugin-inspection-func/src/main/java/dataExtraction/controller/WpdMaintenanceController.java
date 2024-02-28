package dataExtraction.controller;

import dataExtraction.ghd.entity.WpdMaintenance;
import dataExtraction.response.ResultObject;
import dataExtraction.response.ResultState;
import dataExtraction.service.WpdMaintenanceService;
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
@RequestMapping("/wpdMaintenance")
@Tag(name = "设备维修记录")
public class WpdMaintenanceController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private WpdMaintenanceService wpdMaintenanceService;

    @RequestMapping(value = "/queryEmbellish", method = RequestMethod.GET)
    @Operation(summary = "查询维修记录列表")
    public ResultObject queryEmbellish(@Parameter(description = "记录ID")  String devId) {
        ResultObject resultObject = new ResultObject();
        try {
            Object object = wpdMaintenanceService.queryEmbellish(devId);
            logger.info("查询维修记录成功");
            resultObject.setMessage("查询成功");
            resultObject.setData(object);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询维修记录失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @Operation(summary = "保存数据")
    public ResultObject save(@Parameter(description = "数据JSON")  @RequestBody WpdMaintenance wpdMaintenance ) {
        ResultObject resultObject = new ResultObject();
        try {
            Object object = wpdMaintenanceService.save(wpdMaintenance);
            resultObject.setMessage("成功");
            resultObject.setData(object);
            return resultObject;
        }catch (Exception e){
            resultObject.setMessage("失败");
            resultObject.setState(ResultState.FAIL);
            return resultObject;
        }
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @Operation(summary = "修改数据")
    public ResultObject edit(@Parameter(description = "数据")  @RequestBody WpdMaintenance wpdMaintenance ) {
        ResultObject resultObject = new ResultObject();
        try {
            Object object = wpdMaintenanceService.edit(wpdMaintenance);
            resultObject.setMessage("成功");
            resultObject.setData(object);
            return resultObject;
        }catch (Exception e){
            resultObject.setMessage("失败");
            resultObject.setState(ResultState.FAIL);
            return resultObject;
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @Operation(summary = "删除数据")
    public ResultObject delete(String id) {
        ResultObject resultObject = new ResultObject();
        try {
            Object object = wpdMaintenanceService.deleteId(id);
            resultObject.setMessage("成功");
            resultObject.setData(object);
            return resultObject;
        }catch (Exception e){
            resultObject.setMessage("失败");
            resultObject.setState(ResultState.FAIL);
            return resultObject;
        }
    }

}
