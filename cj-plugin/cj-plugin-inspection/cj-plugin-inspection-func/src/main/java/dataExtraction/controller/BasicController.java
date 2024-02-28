package dataExtraction.controller;

import com.alibaba.fastjson.JSONArray;
import dataExtraction.ghd.entity.WpdRrInfo;
import dataExtraction.response.ResultObject;
import dataExtraction.response.ResultState;
import dataExtraction.service.BasicService;
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

import java.util.List;

@RestController
@RequestMapping("/basic")
@Tag(name = "基本信息查询")
public class BasicController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BasicService basicService;

    @RequestMapping(value = "/queryNode", method = RequestMethod.GET)
    @Operation(summary = "查询区域节点")
    public ResultObject queryNode(@Parameter(description = "节点ID")  String ndcdId) {
        ResultObject resultObject = new ResultObject();
        try {
            WpdRrInfo wpdRrInfo = basicService.queryNode(ndcdId);
            logger.info("查询详情成功");
            resultObject.setMessage("成功");
            resultObject.setData(wpdRrInfo);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询详情失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/queryRRs", method = RequestMethod.GET)
    @Operation(summary = "查询水库")
    public ResultObject queryRRs(@Parameter(description = "项目编码")  String id) {
        ResultObject resultObject = new ResultObject();
        try {
            List<WpdRrInfo> wpdRrInfos = basicService.queryRRs(id);
            logger.info("查询水库成功");
            resultObject.setMessage("成功");
            resultObject.setData(wpdRrInfos);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询水库失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/queryRR", method = RequestMethod.GET)
    @Operation(summary = "查询水库树结构")
    public ResultObject queryRR() {
        ResultObject resultObject = new ResultObject();
        try {
            List<WpdRrInfo> wpdRrInfos = basicService.queryRR();
            logger.info("查询水库成功");
            resultObject.setMessage("成功");
            resultObject.setData(wpdRrInfos);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询水库失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    @Operation(summary = "修改水位站基础信息")
    public ResultObject modify(@RequestBody WpdRrInfo wpdRrInfo) {
        ResultObject resultObject = new ResultObject();
        try {
            WpdRrInfo winfo = basicService.modify(wpdRrInfo);
            logger.info("修改基本信息成功");
            resultObject.setMessage("成功");
            resultObject.setData(winfo);
            return resultObject;
        }catch (Exception e) {
            logger.error("修改基本信息失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/queryLast", method = RequestMethod.GET)
    @Operation(summary = "查询点位")
    public ResultObject queryLast(@Parameter(description = "类型")  String typeName) {
        ResultObject resultObject = new ResultObject();
        try {
            List<WpdRrInfo> wpdRrInfos = basicService.queryLast(typeName);
            logger.info("查询点位成功");
            resultObject.setMessage("成功");
            resultObject.setData(wpdRrInfos);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询点位失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/deleteRR", method = RequestMethod.GET)
    @Operation(summary = "删除数据")
    public ResultObject deleteRR(@Parameter(description = "id")  String id) {
        ResultObject resultObject = new ResultObject();
        try {
            basicService.deleteRR(id);
            logger.info("删除成功");
            resultObject.setMessage("成功");
            return resultObject;
        }catch (Exception e) {
            logger.error("删除失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/queryAllData", method = RequestMethod.GET)
    @Operation(summary = "查询所有点位信息")
    public ResultObject queryAllData() {
        ResultObject resultObject = new ResultObject();
        try {
            List<WpdRrInfo> wpdRrInfos = basicService.queryAllData();
            logger.info("查询所有点位成功");
            resultObject.setMessage("成功");
            resultObject.setData(wpdRrInfos);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询所有点位失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/queryRain", method = RequestMethod.GET)
    @Operation(summary = "根据水库ID查询雨量站")
    public ResultObject queryRain(@Parameter(description = "类型")  String typeName) {
        ResultObject resultObject = new ResultObject();
        try {
            List<WpdRrInfo> wpdRrInfos = basicService.queryRain(typeName);
            logger.info("查询雨量站成功");
            resultObject.setMessage("成功");
            resultObject.setData(wpdRrInfos);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询雨量站失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

    @RequestMapping(value = "/queryRRRain", method = RequestMethod.GET)
    @Operation(summary = "根据水库雨量站")
    public ResultObject queryRRRain() {
        ResultObject resultObject = new ResultObject();
        try {
            JSONArray jsonArray = basicService.queryRRRain();
            logger.info("查询水库雨量站成功");
            resultObject.setMessage("成功");
            resultObject.setData(jsonArray);
            return resultObject;
        }catch (Exception e) {
            logger.error("查询水库雨量站失败");
            resultObject.setState(ResultState.FAIL);
            resultObject.setMessage("查询失败");
            return resultObject;
        }
    }

}
