package com.cj.waterresources.func.modular.waterResourceAllcation.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.ExcelUtils;
import com.cj.common.util.UUIDUtils;
import com.cj.flood.api.PredictionApi;
import com.cj.model.func.core.util.MinioUtils;
import com.cj.model.func.core.util.MultipartFileUtil;
import com.cj.model.func.modular.curve.service.CurveService;
import com.cj.model.func.modular.entity.Flood;
import com.cj.model.func.modular.watertransfer.entity.DataInflowPrevent;
import com.cj.model.func.modular.watertransfer.function.OutResult;
import com.cj.model.func.modular.watertransfer.req.WaterTransferReq;
import com.cj.model.func.modular.watertransfer.res.ResOption;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.dto.IncomingWaterForecastDto;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.req.WaterResourceAllocationAddReq;
import com.cj.waterresources.func.modular.waterResourceAllcation.mapper.WaterResourceAllocationMapper;
import com.cj.waterresources.func.modular.waterResourceAllcation.entity.WaterResourceAllocation;
import com.cj.waterresources.func.modular.waterResourceAllcation.service.WaterResourceAllocationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 水资源调配模型表(WaterResourceAllocation)表服务实现类
 *
 * @author makejava
 * @since 2023-11-14 17:34:50
 */
@Service("waterResourceAllocationService")
public class WaterResourceAllocationServiceImpl extends ServiceImpl<WaterResourceAllocationMapper, WaterResourceAllocation> implements WaterResourceAllocationService {

    @Resource
    private PredictionApi predictionApi;

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private CurveService curveService;

    @Override
    public RestResponse<List<IncomingWaterForecastDto>> getIncomingWaterForecastListByTime(String startTime, String endTime) {
        try {
            String programmeListByTime = predictionApi.getProgrammeListByTime(startTime, endTime);
            List<IncomingWaterForecastDto> incomingWaterForecastDtos = JSONObject.parseArray(programmeListByTime, IncomingWaterForecastDto.class);
            if(null != incomingWaterForecastDtos && incomingWaterForecastDtos.size()>0){
                return RestResponse.ok(incomingWaterForecastDtos);
            }else {
                return RestResponse.no("暂无相关数据，请前往防洪业务系统的来水预报新建符合条件的模型结果");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("查询错误");
        }
    }

    @Override
    public RestResponse generativeModel(WaterResourceAllocationAddReq req) {
        try {
            WaterResourceAllocation waterResourceAllocation = new WaterResourceAllocation();
            BeanUtils.copyProperties(req,waterResourceAllocation);
            waterResourceAllocation.setId(UUIDUtils.getUUID());
            waterResourceAllocation.setDel(0);
            waterResourceAllocation.setCreateTime(new Date());
            ExecutorService pool = Executors.newSingleThreadExecutor();
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        WaterTransferReq waterTransferReq = new WaterTransferReq();
                        String inflowDataAddress = req.getInflowDataAddress();
                        InputStream getPredictionInputStream = minioUtils.getObject("tth", inflowDataAddress);
                        String[] split = inflowDataAddress.split("/");
                        String[] split1 = split[split.length - 1].split("\\.");
                        MultipartFile multipartFile = MultipartFileUtil.inputStreamToMultipartFile(getPredictionInputStream, split1[0]);
                        List<Flood> floods = ExcelUtils.importExcel(multipartFile, Flood.class);
                        String jsonString = JSONObject.toJSONString(floods);
                        getPredictionInputStream.close();
                        List<DataInflowPrevent> dataInflowPrevents = JSONObject.parseArray(jsonString, DataInflowPrevent.class);
                        List<DataInflowPrevent> lzzEntryStation = dataInflowPrevents.stream().filter(t -> t.getLocation().equals("楼庄子进库站")).collect(Collectors.toList());
                        List<DataInflowPrevent> interval = dataInflowPrevents.stream().filter(t -> t.getLocation().equals("楼头区间")).collect(Collectors.toList());
                        Map<String, List<DataInflowPrevent>> data = new HashMap<>();
                        data.put("lzz",lzzEntryStation);
                        data.put("tth",interval);
                        waterTransferReq.setStartTime(req.getWaterDistributionStartTime());
                        waterTransferReq.setEndTime(req.getWaterDistributionEndTime());
                        waterTransferReq.setName(req.getWaterDistributionType());
                        waterTransferReq.setFloodWaterLevelLzz(req.getFloodWaterLevelLzz());
                        waterTransferReq.setFloodWaterLevelTth(req.getFloodWaterLevelTth());
                        waterTransferReq.setLevelBeginLzz(req.getLevelBeginLzz());
                        waterTransferReq.setLevelBeginTth(req.getLevelBeginTth());
                        waterTransferReq.setLevelEndLzz(req.getLevelEndLzz());
                        waterTransferReq.setLevelEndTth(req.getLevelEndTth());
                        waterTransferReq.setTimeCalStep(req.getBucketType());
                        waterTransferReq.setData(data);
                        waterTransferReq.setCurve(curveService.selectList());
                        List<ResOption> calculator = OutResult.calculator(waterTransferReq);
                        System.out.println(calculator.size());
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            boolean save = this.save(waterResourceAllocation);
            if(save){
                return RestResponse.ok("水资源调配生成成功");
            }else {
                return RestResponse.no("水资源调配生成失败");
            }

        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("水资源调配生成错误");
        }
    }
}

