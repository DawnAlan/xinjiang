package com.cj.project.modular.FiducialEnterA.service;


import com.cj.project.modular.FiducialEnterA.entity.ConfigProjectField;

import java.util.List;

public interface ConfigProjectFieldService {

    List<ConfigProjectField> GetList(String projectCode, String instrument);
}
