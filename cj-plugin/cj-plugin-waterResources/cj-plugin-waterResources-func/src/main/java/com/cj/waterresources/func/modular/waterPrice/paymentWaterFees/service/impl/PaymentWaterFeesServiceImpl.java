package com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.bean.req.PaymentWaterFeesAddReq;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.bean.req.PaymentWaterFeesSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.bean.res.PaymentWaterFeesSelectListRes;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.mapper.PaymentWaterFeesMapper;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.entity.PaymentWaterFees;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.service.PaymentWaterFeesService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 水费缴纳(PaymentWaterFees)表服务实现类
 *
 * @author makejava
 * @since 2023-11-29 11:28:31
 */
@Service("paymentWaterFeesService")
public class PaymentWaterFeesServiceImpl extends ServiceImpl<PaymentWaterFeesMapper, PaymentWaterFees> implements PaymentWaterFeesService {



    @Override
    public RestResponse paymentWaterFeesAdd(PaymentWaterFeesAddReq req) {
        try {
            PaymentWaterFees paymentWaterFees = new PaymentWaterFees();
            paymentWaterFees.setId(UUIDUtils.getUUID());
            paymentWaterFees.setDel(0);
            paymentWaterFees.setCreateTime(new Date());
            paymentWaterFees.setStation(req.getStation());
            paymentWaterFees.setWaterUserName(req.getWaterUserName());
            paymentWaterFees.setWaterUserId(req.getWaterUserId());
            paymentWaterFees.setPaymentAmount(req.getPaymentAmount());
            paymentWaterFees.setPaymentTime(req.getPaymentTime());
            paymentWaterFees.setType(req.getType());
            boolean save = this.save(paymentWaterFees);
            if(save){
                return RestResponse.ok("新增成功");
            }else {
                return RestResponse.no("添加失败");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("添加错误");
        }
    }

    @Override
    public RestResponse<IPage<PaymentWaterFeesSelectListRes>> paymentWaterFeesSelectList(PaymentWaterFeesSelectListReq req) {
        try {
            IPage<PaymentWaterFeesSelectListRes> page = new Page<>(req.getPageNum(),req.getPageSize());
            IPage<PaymentWaterFeesSelectListRes> page1 = this.baseMapper.paymentWaterFeesSelectList(page, req);
            if(page1.getTotal()>0){
                return RestResponse.ok(page1);
            }else {
                return RestResponse.no("暂无数据");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("查询错误");
        }
    }

    @Override
    public RestResponse delete(String id) {
        try {
            boolean update = this.lambdaUpdate().set(PaymentWaterFees::getDel, 1).eq(PaymentWaterFees::getId, id).update();
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
}

