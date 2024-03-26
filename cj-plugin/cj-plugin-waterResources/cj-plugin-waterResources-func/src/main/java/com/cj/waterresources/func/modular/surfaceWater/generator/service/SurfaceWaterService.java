package com.cj.waterresources.func.modular.surfaceWater.generator.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.model.func.core.util.MinioUtils;
import com.cj.waterresources.func.modular.surfaceWater.entity.QueryListReq;
import com.cj.waterresources.func.modular.surfaceWater.entity.SurfaceWaterReq;
import com.cj.waterresources.func.modular.surfaceWater.generator.domain.*;
import com.cj.waterresources.func.modular.surfaceWater.generator.mapper.SurfaceWaterMapper;
import com.cj.waterresources.func.modular.surfaceWater.vo.SurfaceWaterVo;
import io.minio.ObjectWriteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Administrator
 * @description 针对表【surface_water(地表水情数据)】的数据库操作Service实现
 * @createDate 2023-12-25 10:17:44
 */
@Service
@RequiredArgsConstructor
public class SurfaceWaterService extends ServiceImpl<SurfaceWaterMapper, SurfaceWater>
        implements IService<SurfaceWater> {
    private final SurfaceWaterMapper surfaceWaterMapper;
    private final SurfaceWaterFlowDetailService flowDetailService;
    private final SurfaceWaterActualflowDetailService sactualflowDetailService;
    private final SurfaceWaterHydrologyDetailService hydrologyDetailService;
    private final SurfaceWaterWaterregimenDetailService waterregimenDetailService;

    private final MinioUtils minioUtils;

    public SurfaceWater add(MultipartFile file, SurfaceWaterReq surfaceWaterReq) {
        String filePath = uploadFile(file);
        SurfaceWater surfaceWater = SurfaceWater.builder()
                .id(UUID.randomUUID().toString())
                .year(surfaceWaterReq.getYear())
                .type(surfaceWaterReq.getType())
                .tableName(surfaceWaterReq.getTableName())
                .managerCode(surfaceWaterReq.getManagerCode())
                .managerName(surfaceWaterReq.getManagerName())

                .siteCode(surfaceWaterReq.getSiteCode().isEmpty() ? surfaceWaterReq.getManagerCode() : surfaceWaterReq.getSiteCode())
                .siteName(surfaceWaterReq.getSiteName().isEmpty() ? surfaceWaterReq.getManagerName() : surfaceWaterReq.getSiteName())
                .unit(surfaceWaterReq.getUnit())
                .filePath(filePath)
                .build();
        if (surfaceWaterReq.getType().equals("日汇总表")) {
            List<SurfaceWaterFlowDetail> surfaceWaterFlowDetails = flowDetailService.getFileList(file, surfaceWater.getId(), surfaceWater.getSiteCode(), surfaceWater.getSiteName(), surfaceWater.getYear());
            flowDetailService.ins(surfaceWaterFlowDetails);
        }
        if (surfaceWaterReq.getType().equals("洪水摘录表")) {
            List<SurfaceWaterHydrologyDetail> surfaceWaterHydrologyDetails = hydrologyDetailService.getFileList(file, surfaceWater.getId(), surfaceWater.getSiteCode(), surfaceWater.getSiteName(), surfaceWater.getYear());
            hydrologyDetailService.ins(surfaceWaterHydrologyDetails);
        }
        if (surfaceWaterReq.getType().equals("实测流量成果表")) {
            List<SurfaceWaterActualflowDetail> surfaceWaterActualflowDetails = sactualflowDetailService.getFileList(file, surfaceWater.getId(), surfaceWater.getSiteCode(), surfaceWater.getSiteName(), surfaceWater.getYear());
            sactualflowDetailService.ins(surfaceWaterActualflowDetails);
        }
        if (surfaceWaterReq.getType().equals("水库水情统计表")) {
            List<SurfaceWaterWaterregimenDetail> surfaceWaterWaterregimenDetails = waterregimenDetailService.getFileList(file, surfaceWater.getId(), surfaceWater.getSiteCode(), surfaceWater.getSiteName(), surfaceWater.getYear());
            waterregimenDetailService.ins(surfaceWaterWaterregimenDetails);
        }
        surfaceWaterMapper.insert(surfaceWater);
        return surfaceWater;
    }

    public Boolean del(List<String> ids) {
        int isdel = surfaceWaterMapper.deleteBatchIds(ids);
        if (isdel > 0) {
            ids.forEach(id -> {
                flowDetailService.del(id);
                hydrologyDetailService.del(id);
                sactualflowDetailService.del(id);
                waterregimenDetailService.del(id);
            });
        }
        return true;
    }

    public IPage<SurfaceWater> queryList(QueryListReq input) {
        Page<SurfaceWater> page = new Page<>(input.getPageNo(), input.getPageSize());
        return surfaceWaterMapper.selectPage(page, wrapper(input));
    }

    public SurfaceWaterVo query(String id) {
        SurfaceWaterVo surfaceWaterVo = new SurfaceWaterVo();
        SurfaceWater surfaceWater = surfaceWaterMapper.selectById(id);
        surfaceWaterVo.setId(surfaceWater.getId().trim());
        surfaceWaterVo.setType(surfaceWater.getType());
        surfaceWaterVo.setYear(surfaceWater.getYear());
        surfaceWaterVo.setTableName(surfaceWater.getTableName());
        surfaceWaterVo.setSiteCode(surfaceWater.getSiteCode());
        surfaceWaterVo.setSiteName(surfaceWater.getSiteName());
        surfaceWaterVo.setManagerCode(surfaceWater.getManagerCode());
        surfaceWaterVo.setManagerName(surfaceWater.getManagerName());
        surfaceWaterVo.setUnit(surfaceWater.getUnit());
        surfaceWaterVo.setFilePath(surfaceWater.getFilePath().trim());
        /*根据主表ID获取子表信息
         * */
        surfaceWaterVo.setFlowDetailVos(flowDetailService.getsurf(id));
        surfaceWaterVo.setHydrologyDetailVos(hydrologyDetailService.QueryList(id));
        surfaceWaterVo.setActualflowDetailVos(sactualflowDetailService.QueryList(id));
        surfaceWaterVo.setWaterregimenDetailVos(waterregimenDetailService.QueryList(id));
        return surfaceWaterVo;
    }

    private LambdaQueryWrapper<SurfaceWater> wrapper(QueryListReq input) {
        LambdaQueryWrapper<SurfaceWater> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(input.getType() != null && !input.getType().isEmpty(), SurfaceWater::getType, input.getType())
                .eq(input.getYear() != null && input.getYear() > 0, SurfaceWater::getYear, input.getYear())
                .eq(input.getSiteName() != null && !input.getSiteName().isEmpty(), SurfaceWater::getSiteName, input.getSiteName())
                .eq(input.getManagerName() != null && !input.getManagerName().isEmpty(), SurfaceWater::getManagerName, input.getManagerName())
                .eq(StringUtils.isNotEmpty(input.getTableName()), SurfaceWater::getTableName, input.getTableName())
                .orderBy(true, false, SurfaceWater::getYear);
        return wrapper;
    }

   /* private String uploadFile(MultipartFile multipartFile) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDateTime = formatter.format(LocalDateTime.now());
        String filePath = "C:/save/file"; // 替换为你想要保存文件的路径
        String fileName = formattedDateTime + multipartFile.getOriginalFilename(); // 获取上传文件的文件名

        File dest = new File(filePath + fileName); // 指定保存文件的路径和文件名
        try (InputStream inputStream = multipartFile.getInputStream()) {
            dest.createNewFile(); // 创建新文件
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            OutputStream outputStream = new FileOutputStream(dest);
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            System.out.println("文件已成功保存到 " + filePath);
        } catch (IOException e) {
            System.out.println("保存文件时发生错误：" + e.getMessage());
        }
        return fileName;
    }*/

    private String uploadFile(MultipartFile multipartFile) {
        try {
            Date date = new Date();
            String yyyyMMdd = DateUtil.format(date, "yyyyMMdd");
            String hh = DateUtil.format(date, "HH");
            String mm = DateUtil.format(date, "mm");
            String ss = DateUtil.format(date, "ss");
            String namePath = yyyyMMdd + "/" + hh + "/" + mm + "/" + ss + "/" + cn.hutool.core.lang.UUID.fastUUID().toString(true) + multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
            ObjectWriteResponse objectWriteResponse = minioUtils.putObject("tth", namePath, multipartFile.getInputStream(), multipartFile.getContentType());
            String object = objectWriteResponse.object();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void download(String id, HttpServletResponse response) {
        //从minio下载
        SurfaceWater surfaceWater = surfaceWaterMapper.selectById(id);
        minioUtils.download("tth", surfaceWater.getFilePath(), response);
    }


    public List<Map<String, Object>> annualList() {
        List<Integer> yearlist = new ArrayList<>();
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        for (int i = 1989; i <= currentDate.getYear(); i++) {
            yearlist.add( i);
        }

        List<Map<String, Object>> result = surfaceWaterMapper.annualList(yearlist);
//        for (Map<String, Object> row : result) {
//            for (Map.Entry<String, Object> entry : row.entrySet()) {
//                String columnName = entry.getKey();
//                Object columnValue = entry.getValue();
//                // 处理列名和对应值
//            }
//
//
//        }

        return result;

    }
}




