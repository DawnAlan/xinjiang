package com.cj.dataSynchronization.func.modular.lzz.controller;

import com.cj.common.model.RestResponse;
import com.cj.dataSynchronization.func.modular.lzz.service.LzzPlatformService;
import com.cj.dataSynchronization.func.modular.lzz.bean.ReqParam;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import java.text.SimpleDateFormat;
import java.util.Date;

@Api(tags = "楼庄子平台")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@RestController
@Validated
@RequestMapping("lzzPlatform")
public class LzzPlatformController {

    @Autowired
    private LzzPlatformService lzzPlatformService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @ApiOperationSupport(order = 1)
    @ApiOperation("数据录入测试")
    @GetMapping("/add")
    public RestResponse add(@RequestParam(value = "time")String time) {
        try {
            Date parse = sdf.parse(time);
            RestResponse restResponse = lzzPlatformService.insertRainfallStationInfo(parse);
            RestResponse restResponse1 = lzzPlatformService.insertReservoirLevel(parse);
            RestResponse restResponse2 = lzzPlatformService.insertGaugingStation(parse);
            if(restResponse.getCode()==200 && restResponse1.getCode()==200 && restResponse2.getCode() == 200){
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
    @ApiOperation("结构树录入测试")
    @GetMapping("/insertTree")
    public RestResponse insertTree() {
        try {
          return lzzPlatformService.insertTree();
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("error");
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("雨量站信息录入")
    @PostMapping("/insertRainfallStationInfo")
    public RestResponse insertRainfallStationInfo(@RequestBody ReqParam reqParam) {
        try {
            RestResponse restResponse = lzzPlatformService.insertRainfallStationInfoBetweenTime(sdf.parse(reqParam.getStartDate()),sdf.parse(reqParam.getEndDate()),reqParam.getName());
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
    @ApiOperation("库水位站信息录入")
    @PostMapping("/insertReservoirLevelBetweenTime")
    public RestResponse insertReservoirLevelBetweenTime(@RequestBody ReqParam reqParam) {
        try {
            RestResponse restResponse = lzzPlatformService.insertReservoirLevelBetweenTime(sdf.parse(reqParam.getStartDate()),sdf.parse(reqParam.getEndDate()));
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
    @ApiOperation("水位站信息录入")
    @PostMapping("/insertGaugingStationBetweenTime")
    public RestResponse insertGaugingStationBetweenTime(@RequestBody ReqParam reqParam) {
        try {
            RestResponse restResponse = lzzPlatformService.insertGaugingStationBetweenTime(sdf.parse(reqParam.getStartDate()),sdf.parse(reqParam.getEndDate()));
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
    @ApiOperation("楼庄子水厂信息录入")
    @PostMapping("/insertLzzBetweenTime")
    public RestResponse insertLzzBetweenTime(@RequestBody ReqParam reqParam) {
        try {
            RestResponse restResponse = lzzPlatformService.insertLzzBetweenTime(sdf.parse(reqParam.getStartDate()),sdf.parse(reqParam.getEndDate()));
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
    @ApiOperation("库区雨量站信息录入")
    @PostMapping("/insertLzzKqRailBetweenTime")
    public RestResponse insertLzzKqRailBetweenTime(@RequestBody ReqParam reqParam) {
        try {
            RestResponse restResponse = lzzPlatformService.insertLzzKqRailBetweenTimeByMyself(sdf.parse(reqParam.getStartDate()),sdf.parse(reqParam.getEndDate()));
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

    @ApiOperationSupport(order = 4)
    @ApiOperation("楼庄子进库流量数据同步")
    @PostMapping("/insertLzzInputFlowBetweenTime")
    public RestResponse insertLzzInputFlowBetweenTime(@RequestBody ReqParam reqParam) {
        try {
            RestResponse restResponse = lzzPlatformService.insertLzzInputFlowBetweenTime(reqParam.getStartDate(),reqParam.getEndDate());
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
}
