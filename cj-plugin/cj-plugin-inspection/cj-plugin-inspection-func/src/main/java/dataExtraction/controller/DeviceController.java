package dataExtraction.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dataExtraction.ghd.entity.WpdDevice;
import dataExtraction.response.ResultObject;
import dataExtraction.response.ResultState;
import dataExtraction.service.DeviceService;
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
@RequestMapping("/device")
@Tag(name = "设备管理")
public class DeviceController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    DeviceService deviceService;

    @RequestMapping(value = "/queryDevice", method = RequestMethod.GET)
    @Operation(summary = "查询设备列表")
    public ResultObject queryDevice(@Parameter(description = "名称或编码")  String nameORcode) {
        ResultObject resultObject = new ResultObject();
        try {
            JSONObject jsonObject = deviceService.queryDevice(nameORcode);
            logger.info("查询设备成功");
            resultObject.setMessage("成功");
            resultObject.setData(jsonObject);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询设备失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @Operation(summary = "保存数据")
    public ResultObject save(@Parameter(description = "数据JSON")  @RequestBody WpdDevice wpdDevice ) {
        ResultObject resultObject = new ResultObject();
        try {
            Object object = deviceService.save(wpdDevice);
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
    public ResultObject edit(@Parameter(description = "数据")  @RequestBody WpdDevice wpdDevice ){
        ResultObject resultObject = new ResultObject();
        try {
            Object object = deviceService.edit(wpdDevice);
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
            Object object = deviceService.deleteId(id);
            resultObject.setMessage("成功");
            resultObject.setData(object);
            return resultObject;
        }catch (Exception e){
            resultObject.setMessage("失败");
            resultObject.setState(ResultState.FAIL);
            return resultObject;
        }
    }

    @RequestMapping(value = "/deviceAbnorma", method = RequestMethod.GET)
    @Operation(summary = "查询异常设备信息")
    public ResultObject deviceAbnorma() {
        ResultObject resultObject = new ResultObject();
        try {
            JSONArray jsonArray = deviceService.deviceAbnorma();
            resultObject.setMessage("成功");
            resultObject.setData(jsonArray);
            return resultObject;
        }catch (Exception e){
            resultObject.setMessage("失败");
            resultObject.setState(ResultState.FAIL);
            return resultObject;
        }
    }

    @RequestMapping(value = "/deviceAbnormaNumber", method = RequestMethod.GET)
    @Operation(summary = "查询异常设备信息个数")
    public ResultObject deviceAbnormaNumber() {
        ResultObject resultObject = new ResultObject();
        try {
            JSONObject jsonObject = deviceService.deviceAbnormaNumber();
            resultObject.setMessage("成功");
            resultObject.setData(jsonObject);
            return resultObject;
        }catch (Exception e){
            resultObject.setMessage("失败");
            resultObject.setState(ResultState.FAIL);
            return resultObject;
        }
    }

}
