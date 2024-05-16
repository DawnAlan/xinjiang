package com.cj.waterresources.core.context;

import com.cj.DataSynchronization.api.DataSynchronizationApi;
import com.cj.dataSynchronization.feign.DataSynchronizationFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataSynchronizationApiContextBean implements DataSynchronizationApi {

    private final DataSynchronizationFeign dataSynchronizationFeign;
    @Override
    public String updateMonitor(Integer treeType) {
        return dataSynchronizationFeign.updateMonitor(treeType);
    }
}
