package com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.bean.req.WaterPriceSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.bean.req.WaterPriceUpdateReq;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.bean.res.WaterPriceSelectListRes;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.mapper.WaterPriceManagementMapper;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.entity.WaterPriceManagement;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.service.WaterPriceManagementService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 水价管理(WaterPriceManagement)表服务实现类
 *
 * @author makejava
 * @since 2023-11-29 10:44:40
 */
@Service("waterPriceManagementService")
public class WaterPriceManagementServiceImpl extends ServiceImpl<WaterPriceManagementMapper, WaterPriceManagement> implements WaterPriceManagementService {

    @Override
    public RestResponse<List<WaterPriceSelectListRes>> waterPriceSelectList(WaterPriceSelectListReq req) {
        try {
            List<WaterPriceSelectListRes> waterPriceSelectListResList = this.baseMapper.waterPriceSelectList(req);
            List<WaterPriceSelectListRes> collect = waterPriceSelectListResList.stream().filter(t -> t.getPId().equals("0")).collect(Collectors.toList());
            getParamTree(collect,waterPriceSelectListResList);
            if(waterPriceSelectListResList.size()>0){
                return RestResponse.ok(waterPriceSelectListResList);
            }else {
                return RestResponse.no("暂无数据");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("查询错误");
        }
    }

    @Override
    public RestResponse updateWaterPrice(WaterPriceUpdateReq req) {
        try {
            boolean update = this.lambdaUpdate().
                    set(StringUtils.isNotEmpty(req.getUseWaterType()),WaterPriceManagement::getUseWaterType, req.getUseWaterType()).
                    set(req.getWaterPrice()!=null,WaterPriceManagement::getWaterPrice, req.getWaterPrice()).
                    set(req.getQuotaWaterQuantity()!=null,WaterPriceManagement::getQuotaWaterQuantity, req.getQuotaWaterQuantity()).
                    set(req.getFixedWaterPrice()!=null,WaterPriceManagement::getFixedWaterPrice, req.getFixedWaterPrice()).
                    set(req.getFirstLevelStandard()!=null,WaterPriceManagement::getFirstLevelStandard, req.getFirstLevelStandard()).
                    set(req.getFirstTierPrice()!=null,WaterPriceManagement::getFirstTierPrice, req.getFirstTierPrice()).
                    set(req.getSecondLevelStandard()!=null,WaterPriceManagement::getSecondLevelStandard, req.getSecondLevelStandard()).
                    set(req.getSecondTierPrice()!=null,WaterPriceManagement::getSecondTierPrice, req.getSecondTierPrice()).
                    set(req.getThirdLevelStandard()!=null,WaterPriceManagement::getThirdLevelStandard, req.getThirdLevelStandard()).
                    set(req.getThirdTierPrice()!=null,WaterPriceManagement::getThirdTierPrice, req.getThirdTierPrice()).
                    set(req.getWaterResourcePrice()!=null,WaterPriceManagement::getWaterResourcePrice, req.getWaterResourcePrice()).
                    in(WaterPriceManagement::getId, Arrays.stream(req.getIds().split(",")).collect(Collectors.toList())).update();
            if(update){
                return RestResponse.ok("修改成功");
            }else {
                return RestResponse.no("修改失败");
            }

        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("修改错误");
        }
    }

    @Override
    public RestResponse deleteWaterPrice(String id) {
        try {
            boolean update = this.lambdaUpdate().set(WaterPriceManagement::getDel,1).
                    eq(WaterPriceManagement::getId, id).update();
            if(update){
                return RestResponse.ok("删除成功");
            }else {
                return RestResponse.no("删除失败");
            }

        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("删除错误");
        }
    }

    public void getParamTree(List<WaterPriceSelectListRes> resultList, List<WaterPriceSelectListRes> list){
        if(resultList.size()>0){
            for(WaterPriceSelectListRes res : resultList){
                List<WaterPriceSelectListRes> collect = list.stream().filter(t -> t.getPId().equals(res.getId())).collect(Collectors.toList());
                if(collect.size()>0){
                    List<WaterPriceSelectListRes> tempList = new ArrayList<>();
                    for (WaterPriceSelectListRes param:collect){
                        WaterPriceSelectListRes tempRes = new WaterPriceSelectListRes();
                        BeanUtils.copyProperties(param,tempRes);
                        tempList.add(tempRes);
                    }
                    res.setChildren(tempList);
                    getParamTree(tempList,list);
                }
            }
        }
    }
}

