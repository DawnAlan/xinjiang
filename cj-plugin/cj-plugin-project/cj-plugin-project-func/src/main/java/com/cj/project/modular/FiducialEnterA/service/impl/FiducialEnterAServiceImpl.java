package com.cj.project.modular.FiducialEnterA.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.cj.common.exception.CommonException;
import com.cj.project.api.fiducial.entity.FiducialBase;
import com.cj.project.api.instruments.entity.ProjectInstruments;
import com.cj.project.modular.FiducialEnterA.entity.ConfigProjectPoint;
import com.cj.project.modular.FiducialEnterA.service.ConfigProjectPointService;
import com.cj.project.modular.FiducialEnterA.service.FiducialEnterAService;
import com.cj.project.modular.fiducial.service.FiducialBaseService;
import com.cj.project.modular.fiducial.service.FiducialService;
import com.cj.project.modular.instruments.service.ProjectInstrumentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FiducialEnterAServiceImpl implements FiducialEnterAService {

    @Resource
    private ProjectInstrumentsService projectInstrumentsService;
    @Resource
    private FiducialBaseService fiducialBaseService;
    @Resource
    private FiducialService fiducialService;

    @Resource
    private ConfigProjectPointService configProjectPointService;

    @Override
    public List<Map<String, Object>> EnterPointFiducial(String projectCode, String instrument, String isCover) {
        List<Map<String, Object>> result = new ArrayList<>();
        //project instrument
        List<ProjectInstruments> instrumentsJson = projectInstrumentsService.getList(projectCode,null,instrument,null);
        if(ObjectUtil.isEmpty(instrumentsJson))
            throw new CommonException("找不到该项目仪器");
        for (ProjectInstruments item : instrumentsJson
        ) {
            // List<Map<String, Object>> instruResult = new ArrayList<>();
            String instru = item.getInstrumentType();
            //configProjectPoint
            List<ConfigProjectPoint> pointList = configProjectPointService.getList("002", instru);
            if(ObjectUtil.isEmpty(pointList))
                continue;
            //instrument fiducial;
            List<FiducialBase> fiducialsTs = fiducialBaseService.getBatch(projectCode,instru);
            //repeat fiducial
            Set<String> pointSet = pointList.stream().map(ConfigProjectPoint::getRepointname).collect(Collectors.toSet());
            List<FiducialBase> fiducialsRepeat = fiducialsTs.stream()
                    .filter(fiducial -> pointSet.contains(fiducial.getPointName()))
                    .collect(Collectors.toList());
            if(isCover.equals("1"))
            {
                List<String> fiducialIdList = fiducialsRepeat.stream().map(FiducialBase::getId).collect(Collectors.toList());
                fiducialService.delete(fiducialIdList);
            }else
            {
                Set<String> fiducialSet = fiducialsRepeat.stream().map(FiducialBase::getPointName).collect(Collectors.toSet());
                pointList.removeIf(point ->fiducialSet.contains(point.getRepointname()));
            }
            if(fiducialsRepeat != null)
                log.info(instru + "|已有重复考证：" + fiducialsRepeat.stream().count() + "个");
            if(ObjectUtil.isEmpty(pointList))
                continue;
            List<Map<String, Object>>  fiducialAddList = new ArrayList<>();
            for (ConfigProjectPoint point : pointList
            ){
                // boolean isHave = fiducialsTs.stream().anyMatch(s->s.getPointName().equals(point.getPointname())
                //         && s.getInstrumentType().equals(item.getInstrumentType()));
                // if(isHave)
                //     continue;
                Map<String, Object> fiducialMap = new HashMap<>();
                fiducialMap.put("projectCode",projectCode);//ProjectCode
                fiducialMap.put("pointName",point.getRepointname());//测点编号
                fiducialMap.put("subProject","楼庄子水库");//分项类型
                fiducialMap.put("itemProject",point.getPointitemproject());//分部类型
                fiducialMap.put("monitorName",item.getMonitorName());//监测类型
                fiducialMap.put("sensorType",point.getInstrumentType());//传感器类型
                fiducialMap.put("instrumentType",item.getInstrumentType());//仪器类型
                fiducialMap.put("factoryName",point.getSccj());//生产厂家
                fiducialMap.put("sensorName",point.getPointtype());//传感器编号
                fiducialMap.put("sensorCode",point.getPointid());//传感器编号
                fiducialMap.put("installTime",point.getStarttime());//安装时间
                fiducialMap.put("elevation",point.getAzgc());//高程
                fiducialMap.put("mileage",point.getZh());//桩号
                fiducialMap.put("deviceCode",point.getPointid());//测点Code，唯一标识

                if (point.getGzzt().equals("0"))
                {
                    fiducialMap.put("remark","失效");//停用
                }
                else if (point.getGzzt().equals("1"))
                {
                    fiducialMap.put("remark","正常");//正常
                }
                fiducialAddList.add(fiducialMap);
            }

            if(ObjectUtil.isNotEmpty(fiducialAddList))
            {
                fiducialService.addsByMap(fiducialAddList);
                log.info(instru + "|录入考证：" + fiducialAddList.stream().count() + "个");
                result.addAll(fiducialAddList);
            }
        }

        return result;
    }
}
