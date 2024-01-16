package com.cj.data.modular.dataResult.controller;

import com.cj.common.pojo.CommonResult;
import com.cj.data.modular.dataResult.entity.DataRecord;
import com.cj.data.modular.dataResult.param.DataRecordQueryParam;
import com.cj.data.modular.dataResult.service.DataManageService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * 
 * 
 * @author : lb
 * @date : 2023/10/27 09:18
*/
@Api(tags = "数据管理控制器")
@ApiSupport(author = "LB", order = 1)
@RestController
public class DataManageController {

    @Resource
    private DataManageService dataManageService;
    /**
     * 获取数据
     *
     * @author Lb
     * @date  2023/10/27 09:18
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取所有数据记录")
    @PostMapping("/data/dataManage/list")
    public CommonResult<List<Map<String, Object>>> getList(@RequestBody DataRecordQueryParam dataRecordQueryParam) {
        long start = System.currentTimeMillis();
        System.out.println("START:" + ": cost " + (System.currentTimeMillis() - start) + "ms");
        List<Map<String, Object>> result = dataManageService.getList(dataRecordQueryParam);
        System.out.println("SIZE:" + result.size());
        System.out.println("getList:" + ": cost " + (System.currentTimeMillis() - start) + "ms");
        return CommonResult.data(result);
    }


    

}
