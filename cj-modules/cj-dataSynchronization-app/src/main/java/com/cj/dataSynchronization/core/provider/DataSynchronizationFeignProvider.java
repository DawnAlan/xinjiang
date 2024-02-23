package com.cj.dataSynchronization.core.provider;

import com.cj.dataSynchronization.feign.DataSynchronizationFeign;
import com.cj.dataSynchronization.func.modular.provider.DataSynchronizationApiProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class DataSynchronizationFeignProvider implements DataSynchronizationFeign {

    private final DataSynchronizationApiProvider dataSynchronizationApiProvider;

    @Override
    public String updateMonitor() {
        return dataSynchronizationApiProvider.updateMonitor();
    }
}
