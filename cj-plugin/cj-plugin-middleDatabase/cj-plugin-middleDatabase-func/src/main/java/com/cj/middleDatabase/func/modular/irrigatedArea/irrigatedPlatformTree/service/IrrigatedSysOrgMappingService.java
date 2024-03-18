package com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.entity.IrrigatedSysOrgMapping;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.mapper.IrrigatedSysOrgMappingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qianyf
* @description 针对表【IRRIGATED_SYS_ORG_MAPPING】的数据库操作Service实现
* @createDate 2024-03-18 09:47:16
*/
@Service
@RequiredArgsConstructor
public class IrrigatedSysOrgMappingService extends ServiceImpl<IrrigatedSysOrgMappingMapper, IrrigatedSysOrgMapping>
    implements IService<IrrigatedSysOrgMapping>{

}




