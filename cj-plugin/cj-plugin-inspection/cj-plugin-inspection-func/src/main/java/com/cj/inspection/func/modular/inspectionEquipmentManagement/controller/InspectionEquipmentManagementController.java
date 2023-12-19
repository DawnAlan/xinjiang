package com.cj.inspection.func.modular.inspectionEquipmentManagement.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cj.common.model.RestResponse;
import com.cj.inspection.func.modular.inspectionEquipmentManagement.bean.req.DeviceSelectReq;
import com.cj.inspection.func.modular.inspectionEquipmentManagement.entity.InspectionEquipmentManagement;
import com.cj.inspection.func.modular.inspectionEquipmentManagement.service.InspectionEquipmentManagementService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


/**
 * 巡查设备管理表(InspectionEquipmentManagement)表控制层
 *
 * @author makejava
 * @since 2023-12-07 19:52:40
 */
@Api(tags = "巡查设备管理表模块")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("inspectionEquipmentManagement")
public class InspectionEquipmentManagementController {


    @Autowired
    private InspectionEquipmentManagementService inspectionEquipmentManagementService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("查询列表")
    @PostMapping("/select")
    public RestResponse<IPage<InspectionEquipmentManagement>> select(@RequestBody DeviceSelectReq req) {
        return inspectionEquipmentManagementService.selectList(req);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("生成二维码")
    @GetMapping("/addImage")
    public RestResponse addImage(@RequestParam String id) {
        return inspectionEquipmentManagementService.addImage(id);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("查看二维码")
    @GetMapping("/viewQRCode")
    public void viewQRCode(@RequestParam String id, HttpServletResponse response) {
         inspectionEquipmentManagementService.viewQRCode(id,response);
    }
}

