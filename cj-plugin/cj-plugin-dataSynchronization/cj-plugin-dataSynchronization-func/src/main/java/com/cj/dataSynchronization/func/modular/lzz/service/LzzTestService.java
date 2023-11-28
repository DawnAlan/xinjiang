package com.cj.dataSynchronization.func.modular.lzz.service;

import com.cj.dataSynchronization.func.modular.lzz.bean.ParamDto;

public interface LzzTestService {

    String selectListTest();

    ParamDto selectInfo(String senId);
}
