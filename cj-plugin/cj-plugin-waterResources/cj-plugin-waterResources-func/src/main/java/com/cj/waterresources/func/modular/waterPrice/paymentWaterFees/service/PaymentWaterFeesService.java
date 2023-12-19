package com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.bean.req.PaymentWaterFeesAddReq;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.bean.req.PaymentWaterFeesSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.bean.res.PaymentWaterFeesSelectListRes;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.entity.PaymentWaterFees;

/**
 * 水费缴纳(PaymentWaterFees)表服务接口
 *
 * @author makejava
 * @since 2023-11-29 11:28:30
 */
public interface PaymentWaterFeesService extends IService<PaymentWaterFees> {

    RestResponse paymentWaterFeesAdd(PaymentWaterFeesAddReq req);

    RestResponse<IPage<PaymentWaterFeesSelectListRes>> paymentWaterFeesSelectList(PaymentWaterFeesSelectListReq req);

    RestResponse delete(String id);

}

