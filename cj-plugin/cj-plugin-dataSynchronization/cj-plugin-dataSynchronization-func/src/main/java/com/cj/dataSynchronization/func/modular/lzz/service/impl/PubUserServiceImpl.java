package com.cj.dataSynchronization.func.modular.lzz.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.cj.dataSynchronization.func.modular.lzz.mapper.PubUserMapper;
import com.cj.dataSynchronization.func.modular.lzz.service.PubUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@DS("multi-datasource2")
public class PubUserServiceImpl implements PubUserService {

    @Autowired
    private PubUserMapper pubUserMapper;

    @Override
    public String selectListTest() {
        return pubUserMapper.selectListTest();
    }

    @Override
    public String selectRainfallStation() {
        return pubUserMapper.selectRainfallStation();
    }
}
