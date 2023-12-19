package com.cj.inspection.func.modular.inspectionEquipmentManagement.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.inspection.func.modular.inspectionEquipmentManagement.bean.req.DeviceSelectReq;
import com.cj.inspection.func.modular.inspectionEquipmentManagement.entity.InspectionEquipmentManagement;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 巡查设备管理表(InspectionEquipmentManagement)表服务接口
 *
 * @author makejava
 * @since 2023-12-07 19:52:41
 */
public interface InspectionEquipmentManagementService extends IService<InspectionEquipmentManagement> {

    RestResponse<IPage<InspectionEquipmentManagement>> selectList(DeviceSelectReq req);

    RestResponse addImage(String id);

    void viewQRCode(String id, HttpServletResponse response);
}

