package com.cj.flood.func.modular.rollUpdate.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.flood.func.modular.prediction.bean.dto.PredictionProcessDto;
import com.cj.flood.func.modular.prediction.bean.res.IncomingWaterForecastDetailsRes;
import com.cj.flood.func.modular.rollUpdate.bean.req.ModelRollUpdateSelectListReq;
import com.cj.flood.func.modular.rollUpdate.entity.RollUpdateFloodControl;
import com.cj.flood.func.modular.rollUpdate.entity.RollUpdateIncomingWater;
import com.cj.flood.func.modular.rollUpdate.mapper.ModelRollUpdateMapper;
import com.cj.flood.func.modular.rollUpdate.entity.ModelRollUpdate;
import com.cj.flood.func.modular.rollUpdate.service.ModelRollUpdateService;
import com.cj.flood.func.modular.rollUpdate.service.RollUpdateFloodControlService;
import com.cj.flood.func.modular.rollUpdate.service.RollUpdateIncomingWaterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 模型滚动更新表(ModelRollUpdate)表服务实现类
 *
 * @author makejava
 * @since 2024-07-19 14:59:18
 */
@Service("modelRollUpdateService")
@Slf4j
public class ModelRollUpdateServiceImpl extends ServiceImpl<ModelRollUpdateMapper, ModelRollUpdate> implements ModelRollUpdateService {

    @Autowired
    private RollUpdateFloodControlService rollUpdateFloodControlService;

    @Autowired
    private RollUpdateIncomingWaterService rollUpdateIncomingWaterService;

    @Override
    public RestResponse add(ModelRollUpdate modelRollUpdate) {
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        modelRollUpdate.setId(UUIDUtils.getUUID());
        modelRollUpdate.setCreateBy(saBaseLoginUser.getName());
        modelRollUpdate.setCreateTime(new Date());
        modelRollUpdate.setRunStatus(0);
        modelRollUpdate.setCurrentRunCount(0);
        boolean save = this.save(modelRollUpdate);
        if (save) {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            pool.submit(new Runnable() {
                private ModelRollUpdateService modelRollUpdateService = SpringUtil.getBean(ModelRollUpdateService.class);
                @Override
                public void run() {
                    modelRollUpdate.setCurrentRunCount(modelRollUpdate.getCurrentRunCount()+1);
                    String incomingWaterId ="";
                    //滚动新增来水预报
                    try {
                        incomingWaterId = rollUpdateIncomingWaterService.add(new Date(), modelRollUpdate,saBaseLoginUser.getName());
                    }catch (Exception e) {
                        log.error("来水预报模执行失败，原因：{}", e.getMessage());
                        String remark = modelRollUpdate.getRemark();
                        modelRollUpdate.setRemark(StringUtils.isEmpty(remark)?""+"来水预报模型第"+modelRollUpdate.getCurrentRunCount()+"次执行失败":remark+" || " +"来水预报模型第"+modelRollUpdate.getCurrentRunCount()+"次执行失败");
                    }
                    //滚动新增防洪调度
                    if(StringUtils.isNotEmpty(incomingWaterId)){
                        try {
                            rollUpdateFloodControlService.add(incomingWaterId, modelRollUpdate);
                        }catch (Exception e) {
                            log.error("防洪调度执行失败，原因：{}", e.getMessage());
                            String remark = modelRollUpdate.getRemark();
                            modelRollUpdate.setRemark(StringUtils.isEmpty(remark)?""+"防洪调度模型第"+modelRollUpdate.getCurrentRunCount()+"次执行失败":remark+" || " +"防洪调度模型第"+modelRollUpdate.getCurrentRunCount()+"次执行失败");
                        }
                    }
                    modelRollUpdateService.updateById(modelRollUpdate);
                }
            });

            return RestResponse.ok();
        }else {
            return RestResponse.no("新增失败");
        }
    }

    @Override
    public RestResponse update(ModelRollUpdate modelRollUpdate) {
        boolean b = this.updateById(modelRollUpdate);
        if ( b) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("修改失败");
        }
    }

    @Override
    public RestResponse stop(String ids) {
        boolean update = this.lambdaUpdate().set(ModelRollUpdate::getRunStatus, 1).in(ModelRollUpdate::getId, Arrays.stream(ids.split(",")).collect(Collectors.toList())).update();
        if (update) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("停止失败");
        }
    }

    @Override
    public RestResponse start(String ids) {
        boolean update = this.lambdaUpdate().set(ModelRollUpdate::getRunStatus, 0).in(ModelRollUpdate::getId, Arrays.stream(ids.split(",")).collect(Collectors.toList())).update();
        if (update) {
            List<ModelRollUpdate> list = this.lambdaQuery().in(ModelRollUpdate::getId, Arrays.stream(ids.split(",")).collect(Collectors.toList())).list();
            for (ModelRollUpdate modelRollUpdate : list) {
                if(modelRollUpdate.getCurrentRunCount()==modelRollUpdate.getRefreshCount()){
                    this.lambdaUpdate().set(ModelRollUpdate::getRunStatus, 1).eq(ModelRollUpdate::getId,modelRollUpdate.getId()).update();
                }else {
                    ExecutorService pool = Executors.newSingleThreadExecutor();
                    pool.submit(new Runnable() {
                        private ModelRollUpdateService modelRollUpdateService = SpringUtil.getBean(ModelRollUpdateService.class);
                        @Override
                        public void run() {
                            modelRollUpdate.setCurrentRunCount(modelRollUpdate.getCurrentRunCount()+1);
                            String incomingWaterId ="";
                            //滚动新增来水预报
                            try {
                                incomingWaterId = rollUpdateIncomingWaterService.add(new Date(), modelRollUpdate,modelRollUpdate.getCreateBy());
                            }catch (Exception e) {
                                log.error("来水预报模执行失败，原因：{}", e.getMessage());
                                String remark = modelRollUpdate.getRemark();
                                modelRollUpdate.setRemark(StringUtils.isEmpty(remark)?""+"来水预报模型第"+modelRollUpdate.getCurrentRunCount()+"次执行失败":remark+" || " +"来水预报模型第"+modelRollUpdate.getCurrentRunCount()+"次执行失败");
                            }
                            //滚动新增防洪调度
                            if(StringUtils.isNotEmpty(incomingWaterId)){
                                try {
                                    rollUpdateFloodControlService.add(incomingWaterId, modelRollUpdate);
                                }catch (Exception e) {
                                    log.error("防洪调度执行失败，原因：{}", e.getMessage());
                                    String remark = modelRollUpdate.getRemark();
                                    modelRollUpdate.setRemark(StringUtils.isEmpty(remark)?""+"防洪调度模型第"+modelRollUpdate.getCurrentRunCount()+"次执行失败":remark+" || " +"防洪调度模型第"+modelRollUpdate.getCurrentRunCount()+"次执行失败");
                                }
                            }
                            modelRollUpdateService.updateById(modelRollUpdate);
                        }
                    });
                }
            }
            return RestResponse.ok();
        }else {
            return RestResponse.no("启动失败");
        }
    }

    @Override
    public RestResponse delete(String ids) {
        boolean update = this.removeBatchByIds(Arrays.stream(ids.split(",")).collect(Collectors.toList()));
        if (update) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("删除失败");
        }
    }

    @Override
    public RestResponse selectList(ModelRollUpdateSelectListReq req) {
        IPage page = new Page(req.getPageNum(),req.getPageSize());
        IPage page1 = this.lambdaQuery().eq(req.getRunStatus() != null, ModelRollUpdate::getRunStatus, req.getRunStatus()).
                like(StringUtils.isNotEmpty(req.getCreateBy()), ModelRollUpdate::getCreateBy, req.getCreateBy()).
                like(StringUtils.isNotEmpty(req.getSchemeName()), ModelRollUpdate::getSchemeName, req.getSchemeName()).
                between(StringUtils.isNotEmpty(req.getStartTime()) && StringUtils.isNotEmpty(req.getEndTime()), ModelRollUpdate::getCreateTime, req.getStartTime(), req.getEndTime()).
                orderByDesc(ModelRollUpdate::getCreateTime).
                page(page);
        if(page1.getTotal()>0){
            return RestResponse.ok(page1);
        }else{
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    public RestResponse selectModelResultList(String id) {
        Map<String, Object> result = new HashMap<>();
        List<RollUpdateIncomingWater> incomingWater = rollUpdateIncomingWaterService.lambdaQuery().eq(RollUpdateIncomingWater::getRollId, id).list();
        List<RollUpdateFloodControl> floodControl = rollUpdateFloodControlService.lambdaQuery().eq(RollUpdateFloodControl::getRollId, id).list();
        result.put("incomingWater",incomingWater);
        result.put("floodControl",floodControl);
        return RestResponse.ok(result);
    }

    @Override
    public RestResponse selectDetailsById(String id) {
        Map<String, Object> result = new HashMap<>();
        RollUpdateIncomingWater incomingWater = rollUpdateIncomingWaterService.lambdaQuery().eq(RollUpdateIncomingWater::getRollId, id).
                orderByDesc(RollUpdateIncomingWater::getCreateTime).last("limit 1").one();
        IncomingWaterForecastDetailsRes incomingWaterForecastDetailsRes = rollUpdateIncomingWaterService.selectDetails(incomingWater.getId());
        RollUpdateFloodControl floodControl = rollUpdateFloodControlService.lambdaQuery().eq(RollUpdateFloodControl::getRollId, id).
                orderByDesc(RollUpdateFloodControl::getCreateTime).last("limit 1").one();
        Map<String, List<PredictionProcessDto>> stringListMap = rollUpdateFloodControlService.selectDetails(floodControl.getId());
        result.put("incomingWater",incomingWaterForecastDetailsRes);
        result.put("floodControl",stringListMap);
        return RestResponse.ok(result);
    }

    @Override
    public RestResponse selectDetailsByInComingWaterId(String inComingWaterId) {
        Map<String, Object> result = new HashMap<>();
        RollUpdateIncomingWater incomingWater = rollUpdateIncomingWaterService.lambdaQuery().eq(RollUpdateIncomingWater::getId, inComingWaterId).
                orderByDesc(RollUpdateIncomingWater::getCreateTime).last("limit 1").one();
        IncomingWaterForecastDetailsRes incomingWaterForecastDetailsRes = rollUpdateIncomingWaterService.selectDetails(incomingWater.getId());
        RollUpdateFloodControl floodControl = rollUpdateFloodControlService.lambdaQuery().eq(RollUpdateFloodControl::getForecastingSchemeId, inComingWaterId).
                orderByDesc(RollUpdateFloodControl::getCreateTime).last("limit 1").one();
        Map<String, List<PredictionProcessDto>> stringListMap = rollUpdateFloodControlService.selectDetails(floodControl.getId());
        result.put("incomingWater",incomingWaterForecastDetailsRes);
        result.put("floodControl",stringListMap);
        return RestResponse.ok(result);
    }
}

