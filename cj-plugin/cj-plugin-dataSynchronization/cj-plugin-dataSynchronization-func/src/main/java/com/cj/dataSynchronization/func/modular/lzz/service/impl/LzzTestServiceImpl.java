package com.cj.dataSynchronization.func.modular.lzz.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.cj.dataSynchronization.func.modular.lzz.bean.ParamDto;
import com.cj.dataSynchronization.func.modular.lzz.mapper.LzzTestMapper;
import com.cj.dataSynchronization.func.modular.lzz.service.LzzTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@DS("multi-datasource1")
public class LzzTestServiceImpl implements LzzTestService {

    @Autowired
    private LzzTestMapper mapper;
    @Override
    public String selectListTest() {
        return mapper.selectListTest();
    }

    @Override
    public ParamDto selectInfo(String senId) {
        return mapper.selectInfo(senId);
    }
}
