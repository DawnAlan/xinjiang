package com.cj.dataSynchronization.func.modular.tth.service;

import com.cj.common.model.RestResponse;

import java.util.Date;

public interface IrrigatedAreaService {

    RestResponse getAllTree();

    RestResponse getDataByIdAndTime(Integer flag,String time);
    RestResponse getDataById();

}
