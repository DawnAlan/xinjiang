package com.cj.inspection.func.modular.inspectionEquipmentManagement.bean.req;

import com.cj.inspection.func.core.utils.PageToolUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceSelectReq extends PageToolUtil implements Serializable {

    //设备类型
    @ApiModelProperty(value = "设备类型")
    private String deviceType;

    //所属单位
    @ApiModelProperty(value = "所属单位")
    private String affiliatedUnit;
}
