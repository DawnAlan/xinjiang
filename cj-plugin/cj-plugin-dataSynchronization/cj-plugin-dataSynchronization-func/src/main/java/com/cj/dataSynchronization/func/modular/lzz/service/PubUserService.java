package com.cj.dataSynchronization.func.modular.lzz.service;

import com.cj.dataSynchronization.func.modular.lzz.bean.UserIdParam;

import java.util.List;

public interface PubUserService {

    String selectListTest();

    String selectRainfallStation();

    List<UserIdParam> selectPidList(String name);
    List<UserIdParam> selectPidList();

    List<UserIdParam> selectGaugingStationIdList(String pId);
    List<UserIdParam> selectReservoirLevelIdList(String pId);

    List<UserIdParam> selectRainfallStationIdList(String pId);
}
