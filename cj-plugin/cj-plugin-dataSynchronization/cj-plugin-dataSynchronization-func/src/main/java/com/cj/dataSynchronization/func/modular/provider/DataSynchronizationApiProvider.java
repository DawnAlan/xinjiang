package com.cj.dataSynchronization.func.modular.provider;

import com.cj.DataSynchronization.api.DataSynchronizationApi;
import com.cj.common.model.RestResponse;
import com.cj.dataSynchronization.func.modular.lzz.service.LzzPlatformService;
import com.cj.dataSynchronization.func.modular.tth.service.IrrigatedAreaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DataSynchronizationApiProvider implements DataSynchronizationApi {


    private final LzzPlatformService lzzPlatformService;

    private final IrrigatedAreaService irrigatedAreaService;

    @Override
    public String updateMonitor() {
        RestResponse allTree = irrigatedAreaService.getAllTree();
        RestResponse restResponse = lzzPlatformService.updateTree();
        if(allTree.getCode()==200 && restResponse.getCode()==200){
            return "200";
        }else {
            return "500";
        }
    }
}
