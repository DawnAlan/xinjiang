package com.cj.fourPredictions.func.modular.flood.simulationDeduction.conreoller;

import com.cj.common.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.flood.simulationDeduction.service.SimulationDeductionService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "防洪兴利-模拟推演")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("simulationDeduction")
public class SimulationDeductionController {

    @Autowired
    private SimulationDeductionService simulationDeductionService;


    @ApiOperationSupport(order = 1)
    @ApiOperation("防洪兴利-模拟推演根据洪水调度方案ids，比选方案结果")
    @CommonLog(value = "防洪兴利-模拟推演根据洪水调度方案ids，比选方案结果")
    @GetMapping("/getPlansComparison")
    public RestResponse getFloodControlOperationDetails(@RequestParam(value = "ids")String ids) {
        return simulationDeductionService.getPlansComparison(ids);
    }
}
