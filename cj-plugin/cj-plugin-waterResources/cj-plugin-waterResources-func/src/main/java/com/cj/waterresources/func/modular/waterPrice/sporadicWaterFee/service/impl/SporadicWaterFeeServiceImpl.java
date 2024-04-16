package com.cj.waterresources.func.modular.waterPrice.sporadicWaterFee.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.entity.PaymentWaterFees;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.service.PaymentWaterFeesService;
import com.cj.waterresources.func.modular.waterPrice.sporadicWaterFee.bean.req.SporadicWaterFeeSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.sporadicWaterFee.mapper.SporadicWaterFeeMapper;
import com.cj.waterresources.func.modular.waterPrice.sporadicWaterFee.entity.SporadicWaterFee;
import com.cj.waterresources.func.modular.waterPrice.sporadicWaterFee.service.SporadicWaterFeeService;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.entity.WaterPriceManagement;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.service.WaterPriceManagementService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 零星水费(SporadicWaterFee)表服务实现类
 *
 * @author makejava
 * @since 2024-02-01 08:58:08
 */
@Service("sporadicWaterFeeService")
public class SporadicWaterFeeServiceImpl extends ServiceImpl<SporadicWaterFeeMapper, SporadicWaterFee> implements SporadicWaterFeeService {

    @Autowired
    private WaterPriceManagementService waterPriceManagementService;

    @Autowired
    private PaymentWaterFeesService paymentWaterFeesService;

    @Value("${waterResourcePrice}")
    private String waterResourcePrice;

    @Override
    public RestResponse add(SporadicWaterFee sporadicWaterFee) {
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        sporadicWaterFee.setId(UUIDUtils.getUUID());
        sporadicWaterFee.setCreateTime(new Date());
        sporadicWaterFee.setCreateBy(saBaseLoginUser.getName());
        boolean save = this.save(sporadicWaterFee);
        if(save){
            return RestResponse.ok("新增成功");
        }else {
            return RestResponse.no("新增失败");
        }
    }

    @Override
    public RestResponse delete(String ids) {
        boolean b = this.removeBatchByIds(Arrays.stream(ids.split(",")).collect(Collectors.toList()));
        if(b){
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse update(SporadicWaterFee sporadicWaterFee) {
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        sporadicWaterFee.setUpdateBy(saBaseLoginUser.getName());
        sporadicWaterFee.setUpdateTime(new Date());
        boolean b = this.updateById(sporadicWaterFee);
        if(b){
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse<List<SporadicWaterFee>> selectList(SporadicWaterFeeSelectListReq req) {
        List<SporadicWaterFee> list = this.lambdaQuery().
                eq(StringUtils.isNotEmpty(req.getPId()), SporadicWaterFee::getPId, req.getPId()).
                eq(StringUtils.isNotEmpty(req.getUnitId()), SporadicWaterFee::getUnitId, req.getUnitId()).
                eq(req.getWaterFeeType() != null, SporadicWaterFee::getWaterFeeType, req.getWaterFeeType()).
                eq(req.getYear() != null, SporadicWaterFee::getYear, req.getYear()).
                eq(req.getMonth() != null, SporadicWaterFee::getMonth, req.getMonth()).orderByDesc(SporadicWaterFee::getCreateTime).list();
        if(null != list && list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("blank");
        }
    }
}

