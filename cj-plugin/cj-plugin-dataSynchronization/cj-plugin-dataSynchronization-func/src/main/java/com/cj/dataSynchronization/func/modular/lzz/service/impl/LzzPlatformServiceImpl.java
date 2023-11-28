package com.cj.dataSynchronization.func.modular.lzz.service.impl;

import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.dataSynchronization.func.modular.lzz.bean.ParamDto;
import com.cj.dataSynchronization.func.modular.lzz.service.LzzPlatformService;
import com.cj.dataSynchronization.func.modular.lzz.service.LzzTestService;
import com.cj.dataSynchronization.func.modular.lzz.service.PubUserService;
import com.cj.middleDatabase.func.modular.rainfallStation.entity.RainfallStation;
import com.cj.middleDatabase.func.modular.rainfallStation.service.RainfallStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LzzPlatformServiceImpl implements LzzPlatformService {

    @Autowired
    private LzzTestService lzzTestService;

    @Autowired
    private PubUserService pubUserService;

    @Autowired
    private RainfallStationService rainfallStationService;


    @Override
    public RestResponse add() {
        String senId = pubUserService.selectRainfallStation();
        ParamDto paramDto = lzzTestService.selectInfo(senId);
        RainfallStation station = new RainfallStation();
        station.setId(UUIDUtils.getUUID());
        station.setStationName("萨尔达万雨量");
        station.setRainfall(paramDto.getV());
        station.setTime(paramDto.getTime());
        Integer year = paramDto.getTime().getYear();
        station.setYear(year.toString());
        boolean save = rainfallStationService.save(station);
        if(save){
            return RestResponse.ok("success");
        }else{
            return RestResponse.no("fail");
        }
    }
}
