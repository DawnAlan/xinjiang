package com.cj.project.modular.fiducialImport.controller;

import com.cj.common.pojo.CommonResult;
import com.cj.project.api.configfield.dto.ConfigFieldFiducialQueryDto;
import com.cj.project.api.configfield.entity.ConfigFieldFiducial;
import com.cj.project.modular.configfield.service.ConfigFieldFiducialService;
import com.cj.project.modular.fiducialImport.enums.FieldFiducialInEnum;
import com.cj.project.api.configfield.dto.ConfigFieldFiducialQueryDto;
import com.cj.project.modular.configfield.result.ConfigFieldFiducialResult;
import com.cj.project.modular.fiducialImport.service.FiducialImportService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "考证导入控制器")
@ApiSupport(author = "CJ_TEAM", order = 1)
@RestController
public class FiducialImportController {

    @Resource
    private FiducialImportService fiducialImportService;

    @Resource
    private ConfigFieldFiducialService configFieldFiducialService;
    /**
     * 导入考证数据
     *
     * @author : lb
     * @date : 2023/10/27 14:56
    */
    @ApiOperationSupport(order = 1)
    @ApiOperation("导入考证数据")
    @GetMapping("/project/fiducialImport/import")
    public CommonResult<List<Map<String, String>>> Import(String fileName) {
        // fileName = "";
        List<Map<String, String>> maps = fiducialImportService.ImportFiducial(fileName);
        return CommonResult.data(maps);
    }

    /**
     * 考证字段模板
     *
     * @author : lb
     * @date : 2023/11/02 14:57
    */
    @ApiOperationSupport(order = 2)
    @ApiOperation("考证字段模板")
    @GetMapping("/project/fiducialImport/fieldExcel")
    public CommonResult<String> FiducialFieldExcel(String projectCode, String instrumentType) {
        Map<String,String> fieldMap = new LinkedHashMap<>();
        ConfigFieldFiducialQueryDto configFieldQueryDto = new ConfigFieldFiducialQueryDto();
        configFieldQueryDto.setProjectCode(projectCode);
        configFieldQueryDto.setInstrumentType(instrumentType);
        List<ConfigFieldFiducialResult> fieldFiducialResults = configFieldFiducialService.getList(configFieldQueryDto);
        for (FieldFiducialInEnum item : FieldFiducialInEnum.values()
             ) {
            fieldMap.put(item.name(),item.getValue());
        }
        for (ConfigFieldFiducialResult item : fieldFiducialResults
        ) {
            List<ConfigFieldFiducial> fieldConfigs = item.getFieldConfigs();
            for (ConfigFieldFiducial field : fieldConfigs
                 ) {
                if("1".equals(field.getIsDisplay()))
                    fieldMap.put(field.getFieldKey(),field.getFieldText());
            }
        }
        String fileName = "D:\\2项目\\23\\2项目实施202307\\1测点考证" + "\\" + instrumentType + "模板.xlsx";
        String result = fiducialImportService.GetFiducialFieldExcel(fieldMap,fileName);
        return CommonResult.data(result);
        // return CommonResult.data(fieldMap);
    }
}
