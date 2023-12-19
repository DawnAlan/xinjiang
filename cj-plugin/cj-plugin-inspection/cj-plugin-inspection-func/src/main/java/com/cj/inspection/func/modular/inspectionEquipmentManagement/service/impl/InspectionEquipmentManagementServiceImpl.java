package com.cj.inspection.func.modular.inspectionEquipmentManagement.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.inspection.func.core.utils.MinioUtils;
import com.cj.inspection.func.core.utils.QrCodeUtils;
import com.cj.inspection.func.modular.inspectionEquipmentManagement.bean.req.DeviceSelectReq;
import com.cj.inspection.func.modular.inspectionEquipmentManagement.mapper.InspectionEquipmentManagementMapper;
import com.cj.inspection.func.modular.inspectionEquipmentManagement.entity.InspectionEquipmentManagement;
import com.cj.inspection.func.modular.inspectionEquipmentManagement.service.InspectionEquipmentManagementService;
import io.minio.ObjectWriteResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;

/**
 * 巡查设备管理表(InspectionEquipmentManagement)表服务实现类
 *
 * @author makejava
 * @since 2023-12-07 19:52:41
 */
@Service("inspectionEquipmentManagementService")
public class InspectionEquipmentManagementServiceImpl extends ServiceImpl<InspectionEquipmentManagementMapper, InspectionEquipmentManagement> implements InspectionEquipmentManagementService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MinioUtils minioUtils;

    @Override
    public RestResponse<IPage<InspectionEquipmentManagement>> selectList(DeviceSelectReq req) {
        IPage<InspectionEquipmentManagement> p = new Page<>(req.getPageNum(), req.getPageSize());
        IPage<InspectionEquipmentManagement> page = this.lambdaQuery().
                eq(StringUtils.isNotEmpty(req.getDeviceType()), InspectionEquipmentManagement::getDeviceType, req.getDeviceType()).
                eq(StringUtils.isNotEmpty(req.getAffiliatedUnit()), InspectionEquipmentManagement::getAffiliatedUnit, req.getAffiliatedUnit()).page(p);
        if (page.getTotal() > 0) {
            return RestResponse.ok(page);
        } else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    public RestResponse addImage(String id) {
        try {
            String tempFilePath = getTempFilePath();
            Integer integer = QrCodeUtils.zxingCodeCreate(id, tempFilePath, 500, "");
            String path = tempFilePath+integer+".jpg";
            Date date = new Date();
            String yyyyMMdd = DateUtil.format(date, "yyyyMMdd");
            String hh = DateUtil.format(date, "HH");
            String mm = DateUtil.format(date, "mm");
            String ss = DateUtil.format(date, "ss");
            ObjectWriteResponse objectWriteResponse = minioUtils.putObject("tth", yyyyMMdd+"/"+hh+"/"+mm+"/"+ss+"/"+ UUID.fastUUID().toString(true)+"/"+integer+".jpg", path);
            String object = objectWriteResponse.object();
            boolean update = this.lambdaUpdate().set(InspectionEquipmentManagement::getCodePath, object).eq(InspectionEquipmentManagement::getId, id).update();
            if(update){
                return RestResponse.ok("生成成功");
            }else {
                return RestResponse.no("生成失败");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("生成失败");
        }
    }

    @Override
    public void viewQRCode(String id, HttpServletResponse response) {
        InspectionEquipmentManagement byId = this.getById(id);
        minioUtils.download("tth",byId.getCodePath(),response);
    }

    public String getTempFilePath() throws IOException {
        String path = (String)redisUtil.get("tempFilePath");
        if(StringUtils.isNotEmpty(path)){
            return path;
        }else {
            File tempFile = File.createTempFile("script",".sh");
            String tempFilePath = tempFile.getAbsolutePath();
            String[] split = tempFilePath.split("script");
            redisUtil.set("tempFilePath",split[0]);
            return split[0];
        }
    }
}

