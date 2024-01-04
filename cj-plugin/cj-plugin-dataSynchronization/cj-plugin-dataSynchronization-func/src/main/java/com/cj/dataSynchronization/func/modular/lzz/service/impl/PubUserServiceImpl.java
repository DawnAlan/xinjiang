package com.cj.dataSynchronization.func.modular.lzz.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.cj.dataSynchronization.func.modular.lzz.bean.UserIdParam;
import com.cj.dataSynchronization.func.modular.lzz.mapper.PubUserMapper;
import com.cj.dataSynchronization.func.modular.lzz.service.PubUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<UserIdParam> selectPidList(String name) {
        return pubUserMapper.selectPidList(name);
    }

    @Override
    public List<UserIdParam> selectPidList() {
        return pubUserMapper.selectPidList();
    }

    @Override
    public List<UserIdParam> selectGaugingStationIdList(String pId) {
        return pubUserMapper.selectGaugingStationIdList(pId);
    }

    @Override
    public List<UserIdParam> selectReservoirLevelIdList(String pId) {
        return pubUserMapper.selectReservoirLevelIdList(pId);
    }

    @Override
    public List<UserIdParam> selectRainfallStationIdList(String pId) {
        return pubUserMapper.selectRainfallStationIdList(pId);
    }
}
