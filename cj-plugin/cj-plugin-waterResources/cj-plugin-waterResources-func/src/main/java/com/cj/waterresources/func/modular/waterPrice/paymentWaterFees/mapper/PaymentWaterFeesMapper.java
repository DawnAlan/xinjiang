package com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.bean.req.PaymentWaterFeesSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.bean.res.PaymentWaterFeesSelectListRes;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.entity.PaymentWaterFees;
import org.apache.ibatis.annotations.Param;

/**
 * 水费缴纳(PaymentWaterFees)表数据库访问层
 *
 * @author makejava
 * @since 2023-11-29 11:28:30
 */
public interface PaymentWaterFeesMapper extends BaseMapper<PaymentWaterFees> {

    IPage<PaymentWaterFeesSelectListRes> paymentWaterFeesSelectList(IPage<PaymentWaterFeesSelectListRes> page, @Param("req") PaymentWaterFeesSelectListReq req);
}

