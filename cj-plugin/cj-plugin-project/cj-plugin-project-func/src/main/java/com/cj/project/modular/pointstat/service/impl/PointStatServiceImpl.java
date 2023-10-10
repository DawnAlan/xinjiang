package com.cj.project.modular.pointstat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.cj.common.exception.CommonException;
import com.cj.common.pojo.bizPojo.TableColumn;
import com.cj.dev.api.DevDictApi;
import com.cj.project.modular.fiducial.entity.FiducialBase;
import com.cj.project.modular.fiducial.service.FiducialBaseService;
import com.cj.project.modular.pointstat.result.PointStatResult;
import com.cj.project.modular.pointstat.result.RemarkPoints;
import com.cj.project.modular.pointstat.service.PointStatService;
import com.cj.project.modular.treemodel.service.TreeModelService;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 测点统计Service接口实现类
 *
 * @author Lb
 * @date  2023/09/18 12:25
 **/
@Service
public class PointStatServiceImpl implements PointStatService {

    @Resource
    private DevDictApi devDictApi;
    @Resource
    private FiducialBaseService fiducialBaseService;

    @Resource
    private TreeModelService treeModelService;

    @Override
    public PointStatResult GetInstrumentStat(String projectCode, String instrumentStr) {
        PointStatResult result = new PointStatResult();
        //SUM
        Map<String, Object> resultSUM = new LinkedHashMap<>();
        Integer InstrumentTypeSum = 0;
        long PointNumberSum = 0;
        long AutoNumberSum = 0;
        resultSUM.put("仪器类型",InstrumentTypeSum);
        resultSUM.put("测点总数",PointNumberSum);
        resultSUM.put("自动化测点数",AutoNumberSum);
        //Get points
        List<FiducialBase> points = fiducialBaseService.getBatch(projectCode,instrumentStr);
        //tableColumns
        List<TableColumn>  tableColumns = new ArrayList<>();
        tableColumns.add(new TableColumn("InstrumentType", "仪器类型",null));
        tableColumns.add(new TableColumn("PointNumber", "测点总数",null));
        tableColumns.add(new TableColumn("AutoNumber", "自动化测点数",null));
        //remarkPointsList
        List<RemarkPoints> remarkPointsList = new ArrayList<>();
        //point remark dic
        List<JSONObject> devRemarkDict = devDictApi.getDictByValue("FIDUCIALREMARK");
        ////point remark stat dic
        List<JSONObject> devRemarkStatDict = devDictApi.getDictByValue("FIDUCIALREMARKSTAT");
        if(ObjectUtil.isEmpty(devRemarkDict))
            throw new CommonException("找不到测点状态字典");

        for (JSONObject remarkDict : devRemarkDict
        ) {
            String remarkKey = remarkDict.getStr("dictLabel");
            String remarkValue = remarkDict.getStr("dictValue");
            String exJson = remarkDict.getStr("extJson");
            tableColumns.add(new TableColumn(remarkKey,remarkValue,exJson));
            resultSUM.put(remarkKey, 0);
            RemarkPoints remarkPoints = new RemarkPoints();
            List<FiducialBase> details;
            if(remarkValue.equals("Normal"))
                details = points.stream().filter(p->p.getRemark().equals(remarkValue) || ObjectUtil.isEmpty(p.getRemark()))
                        .collect(Collectors.toList());
            else
                details = points.stream().filter(p->p.getRemark().equals(remarkValue))
                        .collect(Collectors.toList());
            resultSUM.put(remarkKey, details.stream().count());
            remarkPoints.setFiducialRemark(remarkValue);
            remarkPoints.setDetails(details);
            remarkPointsList.add(remarkPoints);
        }
        for (JSONObject remarkStatDict : devRemarkStatDict
        ) {
            //测点统计比率
            String remarkStatValue = remarkStatDict.getStr("dictValue");
            Double remarkStatRate = pointRemarkStat(resultSUM, remarkStatValue);
            resultSUM.put(remarkStatDict.getStr("dictLabel"), remarkStatRate);
            tableColumns.add(new TableColumn(remarkStatDict.getStr("dictLabel"),
                    remarkStatDict.getStr("dictValue"),remarkStatDict.getStr("extJson")));
        }
        //pointStat
        List<Map<String, Object>> pointStatTable = new ArrayList<>();
        Map<String, List<FiducialBase>> instrumentPointGroup = points.stream()
                .collect(Collectors.groupingBy(FiducialBase::getInstrumentType));
        for (String instrumentType : instrumentPointGroup.keySet()
             ) {
            List<FiducialBase> instrumentPoints = instrumentPointGroup.get(instrumentType);
            Map<String, Object> instrumentStat = new LinkedHashMap<>();
            instrumentStat.put("InstrumentType", instrumentType);
            InstrumentTypeSum ++;
            instrumentStat.put("PointNumber", instrumentPoints.stream().count());
            PointNumberSum += instrumentPoints.stream().count();
            instrumentStat.put("AutoNumber", instrumentPoints.stream().filter(p->p.getRecordMethod().equals("1")).count());
            AutoNumberSum += instrumentPoints.stream().filter(p->p.getRecordMethod().equals("1")).count();
            for (JSONObject remarkDict : devRemarkDict
            ){
                String remarkValue = remarkDict.getStr("dictValue");
                if(remarkValue.equals("Normal"))
                    instrumentStat.put(remarkValue, instrumentPoints.stream()
                            .filter(p->p.getRemark().equals(remarkValue) || ObjectUtil.isEmpty(p.getRemark())).count());
                else
                    instrumentStat.put(remarkValue, instrumentPoints.stream().filter(p->p.getRemark().equals(remarkValue)).count());
            }
            for (JSONObject remarkStatDict : devRemarkStatDict
            ) {
                //测点统计比率
                String remarkStatValue = remarkStatDict.getStr("dictValue");
                Double remarkStatRate = pointRemarkStat(instrumentStat, remarkStatValue);
                instrumentStat.put(remarkStatValue, remarkStatRate);
            }
            pointStatTable.add(instrumentStat);
        }

        resultSUM.put("仪器类型",InstrumentTypeSum);
        resultSUM.put("测点总数",PointNumberSum);
        resultSUM.put("自动化测点数",AutoNumberSum);
        result.setResultSUM(resultSUM);
        result.setRemarkPoints(remarkPointsList);
        result.setTableColumns(tableColumns);
        result.setPointStatTable(pointStatTable);

        return result;
    }

    @Override
    public PointStatResult GetTreeStat(String projectCode, String nodeID) {
        //Get Tree
        treeModelService.list();

        return null;
    }

    @NotNull
    @Contract(pure = true)
    private Double pointRemarkStat(Map<String, Object> instrumentStat, String remarkStatValue) {


        return 1.0;
    }
}
