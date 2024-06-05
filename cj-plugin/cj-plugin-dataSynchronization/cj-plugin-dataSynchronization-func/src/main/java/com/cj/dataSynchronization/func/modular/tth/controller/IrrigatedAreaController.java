package com.cj.dataSynchronization.func.modular.tth.controller;

import com.cj.common.model.RestResponse;
import com.cj.dataSynchronization.func.modular.tth.dtos.ReqParam;
import com.cj.dataSynchronization.func.modular.tth.service.IrrigatedAreaService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Api(tags = "灌区E平台")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@RestController
@Validated
@RequestMapping("irrigated")
public class IrrigatedAreaController {

    @Autowired
    private IrrigatedAreaService irrigatedAreaService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("获取灌区平台结构树")
    @PostMapping("/getAllTree")
    public RestResponse getAllTree() {
        try {
            RestResponse restResponse = irrigatedAreaService.getAllTree();
            if(restResponse.getCode()==200){
                return RestResponse.ok("ok");
            }else {
                return RestResponse.no("error");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("error");
        }
    }
    @ApiOperationSupport(order = 2)
    @ApiOperation("获取灌区平台数据")
    @PostMapping("/getDataByIdAndTime")
    public RestResponse getDataByIdAndTime(@RequestBody ReqParam param) {
        try {
            RestResponse restResponse = irrigatedAreaService.getDataByIdAndTime(param.getFlag(),param.getTime());
            if(restResponse.getCode()==200){
                return RestResponse.ok("ok");
            }else {
                return RestResponse.no("error");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("error");
        }
    }

    @ApiOperationSupport(order = 1)
    @ApiOperation("获取灌区平台时刻数据")
    @PostMapping("/getDataById")
    public RestResponse getDataById() {
        try {
            RestResponse restResponse = irrigatedAreaService.getDataById();
            if(restResponse.getCode()==200){
                return RestResponse.ok("ok");
            }else {
                return RestResponse.no("error");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("error");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value="导入历史记录", notes="导入历史记录")
    @GetMapping(value = "/importHistoryData")
    public RestResponse importHistoryData(@RequestParam(value = "file",required = true) MultipartFile file) {
        return irrigatedAreaService.importHistoryData(file);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value="查询历史记录", notes="查询历史记录")
    @GetMapping(value = "/selectHistoryData")
    public RestResponse selectHistoryData(@RequestParam(value = "type") String type,
                                          @RequestParam(value = "id") String id,
                                          @RequestParam(value = "startTime") String startTime,
                                          @RequestParam(value = "endTime") String endTime) {
        return irrigatedAreaService.selectHistoryData(type,id,startTime,endTime);
    }
    @ApiOperationSupport(order = 5)
    @ApiOperation(value="保存历史记录", notes="保存历史记录")
    @GetMapping(value = "/saveHistoryData")
    public RestResponse saveHistoryData(@RequestParam(value = "id",required = false) String id,
                                          @RequestParam(value = "startTime") String startTime,
                                          @RequestParam(value = "endTime") String endTime) {
        return irrigatedAreaService.saveHistoryData(id,startTime,endTime);
    }
}
