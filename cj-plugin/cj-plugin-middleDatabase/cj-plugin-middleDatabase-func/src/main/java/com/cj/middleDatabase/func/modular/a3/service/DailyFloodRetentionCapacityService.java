package com.cj.middleDatabase.func.modular.a3.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.middleDatabase.func.modular.a3.entity.DailyFloodRetentionCapacity;
import com.cj.middleDatabase.func.modular.a3.mapper.DailyFloodRetentionCapacityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qianyf
* @description 针对表【DAILY_FLOOD_RETENTION_CAPACITY】的数据库操作Service实现
* @createDate 2024-03-21 11:37:08
*/
@Service
@RequiredArgsConstructor
public class DailyFloodRetentionCapacityService extends ServiceImpl<DailyFloodRetentionCapacityMapper, DailyFloodRetentionCapacity>
    implements IService<DailyFloodRetentionCapacity>{

}




