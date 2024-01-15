package com.cj.project.modular.FiducialEnterA.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.project.modular.FiducialEnterA.entity.ConfigProjectField;
import com.cj.project.modular.FiducialEnterA.mapper.ConfigProjectFieldMapper;
import com.cj.project.modular.FiducialEnterA.service.ConfigProjectFieldService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigProjectFieldServiceImpl extends ServiceImpl<ConfigProjectFieldMapper, ConfigProjectField> implements ConfigProjectFieldService {


    @Override
    public List<ConfigProjectField> GetList(String projectCode, String instrument) {


        return null;
    }
}
