package com.cj.flood.func.modular.prediction.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.flood.func.modular.prediction.mapper.ModelParametersMapper;
import com.cj.flood.func.modular.prediction.entity.ModelParameters;
import com.cj.flood.func.modular.prediction.service.ModelParametersService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 陕北模型参数(ModelParameters)表服务实现类
 *
 * @author makejava
 * @since 2024-03-13 12:27:10
 */
@Service("modelParametersService")
public class ModelParametersServiceImpl extends ServiceImpl<ModelParametersMapper, ModelParameters> implements ModelParametersService {
    public Map<String, List<ModelParameters>> queryList(){
        List<ModelParameters> parametersList =  this.query().list();
        Map<String, List<ModelParameters>> map = parametersList.stream().collect(Collectors.groupingBy(ModelParameters::getSiteName));
        return map;
    }
}

