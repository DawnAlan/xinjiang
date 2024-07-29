package com.cj.dataSynchronization.func.modular.tth.service;

import com.cj.common.model.RestResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public interface IrrigatedAreaService {

    RestResponse getAllTree();

    RestResponse getDataByIdAndTime(Integer flag,String time);
    RestResponse getDataById();

    RestResponse importHistoryData(MultipartFile file);

    RestResponse selectHistoryData(String type,String id,String startTime,String endTime);
    RestResponse calculateHistoryDataAverageValue(String id,String time);

    RestResponse saveHistoryData(String id,String startTime,String endTime);
    RestResponse saveHistoryDataForRain(String id,String startTime,String endTime);

    RestResponse insertWarningInfo(String startTime, String endTime);
}
