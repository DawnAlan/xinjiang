package com.cj.waterresources.func.modular.overallSituationUnitMgr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.entity.OverallSituationUnitMgr;

/**
 * 全局单位管理(OverallSituationUnitMgr)表服务接口
 *
 * @author makejava
 * @since 2024-02-21 11:11:46
 */
public interface OverallSituationUnitMgrService extends IService<OverallSituationUnitMgr> {

    RestResponse add(OverallSituationUnitMgr overallSituationUnitMgr);

    RestResponse delete(String id);

    RestResponse update(OverallSituationUnitMgr overallSituationUnitMgr);

    RestResponse selectTree();

    RestResponse updateMonitor(Integer treeType);

}

