package com.cj.model.func.modular.curve.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.model.func.modular.FloodPrevent.entity.CurveParam;
import com.cj.model.func.modular.curve.entity.Curve;
import com.cj.model.func.modular.curve.mapper.CurveMapper;
import com.cj.model.func.modular.curve.service.CurveService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author July Lion
* @description 针对表【CURVE】的数据库操作Service实现
* @createDate 2023-11-10 15:00:19
*/
@Service
public class CurveServiceImpl extends ServiceImpl<CurveMapper, Curve>
    implements CurveService {

    @Override
    public List<CurveParam> selectList() {
        return this.baseMapper.selectList();
    }
}




