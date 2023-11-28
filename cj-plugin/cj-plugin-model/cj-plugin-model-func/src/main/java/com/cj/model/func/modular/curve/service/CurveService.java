package com.cj.model.func.modular.curve.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.model.func.modular.FloodPrevent.entity.CurveParam;
import com.cj.model.func.modular.curve.entity.Curve;

import java.util.List;

/**
* @author July Lion
* @description 针对表【CURVE】的数据库操作Service
* @createDate 2023-11-10 15:00:19
*/
public interface CurveService extends IService<Curve> {

    List<CurveParam> selectList();

}
