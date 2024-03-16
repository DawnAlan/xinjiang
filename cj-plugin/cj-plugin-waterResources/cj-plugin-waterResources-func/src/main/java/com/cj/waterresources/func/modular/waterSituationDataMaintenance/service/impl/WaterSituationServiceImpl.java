package com.cj.waterresources.func.modular.waterSituationDataMaintenance.service.impl;

import com.cj.common.model.RestResponse;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.entity.IrrigatedPlatformTree;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.service.IrrigatedPlatformTreeService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service.LzzGaugingStationService;
import com.cj.middleDatabase.func.modular.lzz.lzzPlatformTree.entity.LzzPlatformTree;
import com.cj.middleDatabase.func.modular.lzz.lzzPlatformTree.service.LzzPlatformTreeService;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.service.LzzRainfallStationService;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.service.IndustrialWaterFeeService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res.UseWaterTypeStatisticsRes;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.bean.res.WaterPriceSelectListRes;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.UpdateInfoReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.res.HydrographRes;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.res.IrrigatedPlatformTreeRes;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.res.LzzPlatformTreeRes;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.service.WaterSituationService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.AllService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WaterSituationServiceImpl implements WaterSituationService {

    @Autowired
    private IrrigatedPlatformTreeService irrigatedPlatformTreeService;

    @Autowired
    private LzzPlatformTreeService lzzPlatformTreeService;

    @Autowired
    private IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;

    @Autowired
    private LzzRainfallStationService lzzRainfallStationService;

    @Autowired
    private LzzGaugingStationService lzzGaugingStationService;

    @Autowired
    private AllService allService;


    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public RestResponse<Map<String, Object>> selectTree() {
        try {
            Map<String, Object> result = new HashMap<>();
            List<IrrigatedPlatformTreeRes> irrigatedPlatformTreeResList = new ArrayList<>();
            List<IrrigatedPlatformTree> irrigatedPlatformTreeList = irrigatedPlatformTreeService.list();
            irrigatedPlatformTreeList.forEach(t->{
                IrrigatedPlatformTreeRes res = new IrrigatedPlatformTreeRes();
                BeanUtils.copyProperties(t,res);
                irrigatedPlatformTreeResList.add(res);
            });
            List<IrrigatedPlatformTreeRes> irrigatedPlatformParent= irrigatedPlatformTreeResList.stream().filter(t -> t.getParentId().equals("0")).collect(Collectors.toList());
            getIrrigatedTree(irrigatedPlatformParent,irrigatedPlatformTreeResList);
            List<LzzPlatformTreeRes> lzzPlatformTreeResList = new ArrayList<>();
            List<LzzPlatformTree> lzzPlatformTrees = lzzPlatformTreeService.list();
            lzzPlatformTrees.forEach(t->{
                LzzPlatformTreeRes res = new LzzPlatformTreeRes();
                BeanUtils.copyProperties(t,res);
                lzzPlatformTreeResList.add(res);
            });
            List<LzzPlatformTreeRes> lzzPlatformParent= lzzPlatformTreeResList.stream().filter(t -> t.getPId().equals("0")).collect(Collectors.toList());
            getLzzTree(lzzPlatformParent,lzzPlatformTreeResList);
            result.put("irrigated", irrigatedPlatformParent);
            result.put("lzz", lzzPlatformParent);
            return RestResponse.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("查询失败");
        }
    }

    @Override
    public RestResponse selectInfoList(SelectInfoListReq req) {
        if(req.getTreeType().equals("irrigated")){
            List<IrrigatedPlatformDataInfo> list = irrigatedPlatformDataInfoService.selectInfoByCondition(req.getTreeId(),req.getTime(),req.getStartTime(),req.getEndTime());
            if(null != list && list.size()>0) {
                return RestResponse.ok(list);
            }else {
                return RestResponse.no("暂无数据");
            }
        }
        if(req.getTreeType().equals("lzz")){
            List<LzzRainfallStation> list = lzzRainfallStationService.selectInfoByCondition(req.getTreeId(),req.getTime(),req.getStartTime(),req.getEndTime());
            if(null != list && list.size()>0){
                return RestResponse.ok(list);
            }
            List<LzzGaugingStation> list1 = lzzGaugingStationService.selectInfoByCondition(req.getTreeId(),req.getTime(),req.getStartTime(),req.getEndTime());
            if(null != list1 && list1.size()>0){
                return RestResponse.ok(list1);
            }
            return RestResponse.no("暂无数据");
        }
        return RestResponse.no("暂无数据");
    }

    @Override
    public RestResponse<List<HydrographRes>> selectInfoList1(SelectInfoListReq req) {
        List<HydrographRes> hydrographResList = new ArrayList<>();
        if(req.getTreeType().equals("irrigated")){
            List<IrrigatedPlatformDataInfo> list = irrigatedPlatformDataInfoService.selectInfoByCondition(req.getTreeId(),req.getTime(),req.getStartTime(),req.getEndTime());
            if(null != list && list.size()>0) {
                list.forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName(t.getMonitorName());
                    res.setTime(t.getMonitorTime());
                    res.setFlow(t.getSqMonitorFlow());
                    res.setWaterLevel(t.getSqWaterLevel());
                    hydrographResList.add(res);
                });
            }
        }
        if(req.getTreeType().equals("lzz")){
            List<LzzRainfallStation> list = lzzRainfallStationService.selectInfoByCondition(req.getTreeId(),req.getTime(),req.getStartTime(),req.getEndTime());
            if(null != list && list.size()>0){
                list.forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName(t.getStationName());
                    res.setTime(sdf.format(t.getTime()));
                    res.setRainfall(t.getRainfall());
                    res.setTemperature(t.getTemperature());
                    hydrographResList.add(res);
                });
            }
            List<LzzGaugingStation> list1 = lzzGaugingStationService.selectInfoByCondition(req.getTreeId(),req.getTime(),req.getStartTime(),req.getEndTime());
            if(null != list1 && list1.size()>0){
                list1.forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName(t.getStationName());
                    res.setTime(sdf.format(t.getGatherTime()));
                    res.setFlow(t.getFlow());
                    res.setWaterLevel(t.getRelativeWaterLevel());
                    hydrographResList.add(res);
                });
            }
        }
        if(hydrographResList.isEmpty()){
            return RestResponse.no("暂无数据");
        }
        return RestResponse.ok(hydrographResList);
    }

    @Override
    public RestResponse update(UpdateInfoReq req) {
        if(null != req.getIrrigatedPlatformDataInfo()){
            boolean b = irrigatedPlatformDataInfoService.updateById(req.getIrrigatedPlatformDataInfo());
            if(b){
                return RestResponse.ok("修改成功");
            }else {
                return RestResponse.no("修改失败");
            }
        }
        if(null != req.getLzzRainfallStation()){
            boolean b = lzzRainfallStationService.updateById(req.getLzzRainfallStation());
            if(b){
                return RestResponse.ok("修改成功");
            }else {
                return RestResponse.no("修改失败");
            }
        }
        if(null != req.getLzzGaugingStation()){
            boolean b = lzzGaugingStationService.updateById(req.getLzzGaugingStation());
            if(b){
                return RestResponse.ok("修改成功");
            }else {
                return RestResponse.no("修改失败");
            }
        }
        return RestResponse.no("请上传修改数据");
    }

    @Override
    public RestResponse selectInfoListAll(SelectInfoListReq req) {
        RestResponse restResponse1 = this.selectInfoList1(req);
        if(restResponse1.getCode()==200){
            return restResponse1;
        }
        RestResponse restResponse = allService.selectInfoList(req);
        if(restResponse.getCode()==200){
            return restResponse;
        }
        return RestResponse.no("查无数据");
    }

    public void getIrrigatedTree(List<IrrigatedPlatformTreeRes> resultList, List<IrrigatedPlatformTreeRes> list){
        if(resultList.size()>0){
            for(IrrigatedPlatformTreeRes res : resultList){
                List<IrrigatedPlatformTreeRes> collect = list.stream().filter(t -> t.getParentId().equals(res.getId())).collect(Collectors.toList());
                if(collect.size()>0){
                    List<IrrigatedPlatformTreeRes> tempList = new ArrayList<>();
                    for (IrrigatedPlatformTreeRes param:collect){
                        IrrigatedPlatformTreeRes tempRes = new IrrigatedPlatformTreeRes();
                        BeanUtils.copyProperties(param,tempRes);
                        tempList.add(tempRes);
                    }
                    res.setChildren(tempList);
                    getIrrigatedTree(tempList,list);
                }
            }
        }
    }

    public void getLzzTree(List<LzzPlatformTreeRes> resultList, List<LzzPlatformTreeRes> list){
        if(resultList.size()>0){
            for(LzzPlatformTreeRes res : resultList){
                List<LzzPlatformTreeRes> collect = list.stream().filter(t -> t.getPId().equals(res.getId())).collect(Collectors.toList());
                if(collect.size()>0){
                    List<LzzPlatformTreeRes> tempList = new ArrayList<>();
                    for (LzzPlatformTreeRes param:collect){
                        LzzPlatformTreeRes tempRes = new LzzPlatformTreeRes();
                        BeanUtils.copyProperties(param,tempRes);
                        tempList.add(tempRes);
                    }
                    res.setChildren(tempList);
                    getLzzTree(tempList,list);
                }
            }
        }
    }
}
