package com.cj.flood.func.modular.prediction.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.flood.func.modular.prediction.mapper.ModelParametersMapper;
import com.cj.flood.func.modular.prediction.entity.ModelParameters;
import com.cj.flood.func.modular.prediction.service.ModelParametersService;
import org.springframework.stereotype.Service;

/**
 * 陕北模型参数(ModelParameters)表服务实现类
 *
 * @author makejava
 * @since 2024-03-13 12:27:10
 */
@Service("modelParametersService")
public class ModelParametersServiceImpl extends ServiceImpl<ModelParametersMapper, ModelParameters> implements ModelParametersService {

}

