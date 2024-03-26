package com.cj.waterresources.func.modular.surfaceWater.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.surfaceWater.entity.DelReq;
import com.cj.waterresources.func.modular.surfaceWater.entity.QueryListReq;
import com.cj.waterresources.func.modular.surfaceWater.entity.QueryReq;
import com.cj.waterresources.func.modular.surfaceWater.entity.SurfaceWaterReq;
import com.cj.waterresources.func.modular.surfaceWater.generator.domain.SurfaceWater;
import com.cj.waterresources.func.modular.surfaceWater.generator.service.SurfaceWaterService;
import com.cj.waterresources.func.modular.surfaceWater.vo.SurfaceWaterVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "地表水情数据")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/surfacewater")
public class SurfaceWaterController {
    private final SurfaceWaterService surfaceWaterService;

    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping(value = "/add")
    public RestResponse<SurfaceWater> add(
            @RequestPart("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam("year") Integer year,
            @RequestParam("tableName") String tableName,
            @RequestParam("siteCode") String siteCode,
            @RequestParam("siteName") String siteName,
            @RequestParam("managerCode") String managerCode,
            @RequestParam("managerName") String managerName,
            @RequestParam("unit") String unit
    ) {
        try {
            SurfaceWaterReq surfaceWater = new SurfaceWaterReq();
            surfaceWater.setType(type);
            surfaceWater.setUnit(unit);
            surfaceWater.setYear(year);
            surfaceWater.setSiteCode(siteCode);
            surfaceWater.setSiteName(siteName);
            surfaceWater.setManagerCode(managerCode);
            surfaceWater.setManagerName(managerName);
            surfaceWater.setTableName(tableName);
            return RestResponse.ok(surfaceWaterService.add(file,surfaceWater));
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }


    @ApiOperation(value = "列表查询", notes = "列表查询")
    @PostMapping(value = "/queryList")
    public RestResponse<IPage<SurfaceWater>> queryList(@RequestBody QueryListReq input) {
        try {
            return RestResponse.ok(surfaceWaterService.queryList(input));
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }

    @ApiOperation(value = "查询详情", notes = "查询详情")
    @PostMapping(value = "/query")
    public RestResponse<SurfaceWaterVo> query(@RequestBody QueryReq input) {
        try {
            return RestResponse.ok(surfaceWaterService.query(input.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }

    @ApiOperation(value = "删除", notes = "删除")
    @PostMapping(value = "/del")
    public RestResponse<Boolean> del(@RequestBody DelReq input) {
        try {
            return RestResponse.ok(surfaceWaterService.del(input.getIds()));
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }

    @ApiOperation(value = "下载原始文件", notes = "下载原始文件")
    @GetMapping(value = "/downloadResourceFile")
    public void downloadResourceFile(@RequestParam("id") String id, HttpServletResponse response) {
        surfaceWaterService.download(id,response);
    }

    @ApiOperation(value = "昌吉州年景预报", notes = "昌吉州年景预报")
    @PostMapping(value = "/annualList")
    public RestResponse annualList() {
        try {
            return RestResponse.ok(surfaceWaterService.annualList());
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }

}
