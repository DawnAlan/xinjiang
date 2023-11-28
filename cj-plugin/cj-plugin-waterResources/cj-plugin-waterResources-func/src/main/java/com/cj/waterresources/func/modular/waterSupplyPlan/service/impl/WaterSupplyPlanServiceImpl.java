package com.cj.waterresources.func.modular.waterSupplyPlan.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.waterSupplyPlan.bean.req.WaterSupplyPlanSelectListReq;
import com.cj.waterresources.func.modular.waterSupplyPlan.bean.req.WaterSupplyPlanUpdateReq;
import com.cj.waterresources.func.modular.waterSupplyPlan.bean.res.WaterSupplyPlanSelectListRes;
import com.cj.waterresources.func.modular.waterSupplyPlan.mapper.WaterSupplyPlanMapper;
import com.cj.waterresources.func.modular.waterSupplyPlan.entity.WaterSupplyPlan;
import com.cj.waterresources.func.modular.waterSupplyPlan.service.WaterSupplyPlanService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 供水计划管理(WaterSupplyPlan)表服务实现类
 *
 * @author makejava
 * @since 2023-11-21 09:51:24
 */
@Service("waterSupplyPlanService")
public class WaterSupplyPlanServiceImpl extends ServiceImpl<WaterSupplyPlanMapper, WaterSupplyPlan> implements WaterSupplyPlanService {

    @Override
    public RestResponse<IPage<WaterSupplyPlanSelectListRes>> getWaterSupplyPlanList(WaterSupplyPlanSelectListReq req) {
        try {
            IPage<WaterSupplyPlanSelectListRes> page = new Page<>(req.getPageNum(),req.getPageSize());
            IPage<WaterSupplyPlanSelectListRes> waterSupplyPlanList = this.baseMapper.getWaterSupplyPlanList(req, page);
            if(waterSupplyPlanList.getTotal()>0){
                return RestResponse.ok(waterSupplyPlanList);
            }else {
                return RestResponse.no("暂无数据");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("查询错误");
        }

    }

    @Override
    public RestResponse addWaterSupplyPlan(WaterSupplyPlan waterSupplyPlan) {
        try {
            waterSupplyPlan.setId(UUIDUtils.getUUID());
            waterSupplyPlan.setDel(0);
            waterSupplyPlan.setCreateTime(new Date());
            boolean save = this.save(waterSupplyPlan);
            if(save){
                return RestResponse.ok("保存成功");
            }else {
                return RestResponse.no("保存失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("保存错误");
        }
    }

    @Override
    public RestResponse deleteWaterSupplyPlan(String id) {
        try {
            boolean update = this.lambdaUpdate().set(WaterSupplyPlan::getDel, 1).eq(WaterSupplyPlan::getId, id).update();
            if(update){
                return RestResponse.ok("删除成功");
            }else {
                return RestResponse.no("删除失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("删除错误");
        }
    }

    @Override
    public RestResponse updateWaterSupplyPlan(WaterSupplyPlanUpdateReq req) {
        try {
            boolean update = this.lambdaUpdate().set(WaterSupplyPlan::getTableValue, req.getTableValue()).set(WaterSupplyPlan::getTableHead, req.getTableHead()).eq(WaterSupplyPlan::getId, req.getId()).update();
            if(update){
                return RestResponse.ok("修改成功");
            }else {
                return RestResponse.no("修改失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("修改错误");
        }
    }
}

