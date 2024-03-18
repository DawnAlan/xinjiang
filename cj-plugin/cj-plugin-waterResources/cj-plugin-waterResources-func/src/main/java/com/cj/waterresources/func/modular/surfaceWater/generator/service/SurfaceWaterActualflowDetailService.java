package com.cj.waterresources.func.modular.surfaceWater.generator.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.waterresources.func.modular.surfaceWater.generator.domain.SurfaceWaterActualflowDetail;
import com.cj.waterresources.func.modular.surfaceWater.generator.mapper.SurfaceWaterActualflowDetailMapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【surface_water_actualflow_detail】的数据库操作Service实现
 * @createDate 2023-12-25 10:17:44
 */
@Service
@RequiredArgsConstructor
public class SurfaceWaterActualflowDetailService extends ServiceImpl<SurfaceWaterActualflowDetailMapper, SurfaceWaterActualflowDetail>
        implements IService<SurfaceWaterActualflowDetail> {
    private final SurfaceWaterActualflowDetailMapper surfaceWaterActualflowDetailMapper;

    public List<SurfaceWaterActualflowDetail> getFileList(MultipartFile file, String parentId, String siteCode, String siteName, Integer year) {
        List<SurfaceWaterActualflowDetail> list = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    currentCell.setCellType(CellType.STRING);
                }
                try {
                    double number = new Double(currentRow.getCell(0).getStringCellValue());
                    double number1 = new Double(currentRow.getCell(1).getStringCellValue());
                    double number2 = new Double(currentRow.getCell(2).getStringCellValue());
                    double nu = number + number1;
                    double nu1 = number + number2;
                    // 将字符串转换为日期对象
                    Date startTime = DateUtil.getJavaDate(nu);
                    Date endTime = DateUtil.getJavaDate(nu1);
                    SurfaceWaterActualflowDetail detail = SurfaceWaterActualflowDetail.builder()
                            .id(UUID.randomUUID().toString())
                            .startTime(startTime)
                            .endTime(endTime)
                            .type(currentRow.getCell(3).getStringCellValue())
                            .testingMethod(currentRow.getCell(4).getStringCellValue())
                            .waterGauge(currentRow.getCell(5).getStringCellValue())
                            .flow(StringUtils.isEmpty(currentRow.getCell(6).getStringCellValue().trim())?null:new BigDecimal(currentRow.getCell(6).getStringCellValue().trim()))
                            .sectionalArea(StringUtils.isEmpty(currentRow.getCell(7).getStringCellValue().trim())?null:new BigDecimal(currentRow.getCell(7).getStringCellValue().trim()))
                            .meanVelocity(StringUtils.isEmpty(currentRow.getCell(8).getStringCellValue().trim())?null:new BigDecimal(currentRow.getCell(8).getStringCellValue().trim()))
                            .maxVelocity(StringUtils.isEmpty(currentRow.getCell(9).getStringCellValue().trim())?null:new BigDecimal(currentRow.getCell(9).getStringCellValue().trim()))
                            .tpwd(StringUtils.isEmpty(currentRow.getCell(10).getStringCellValue().trim())?null:new BigDecimal(currentRow.getCell(10).getStringCellValue().trim()))
                            .meanDepth(StringUtils.isEmpty(currentRow.getCell(11).getStringCellValue().trim())?null:new BigDecimal(currentRow.getCell(11).getStringCellValue().trim()))
                            .maxDepth(StringUtils.isEmpty(currentRow.getCell(12).getStringCellValue().trim())?null:new BigDecimal(currentRow.getCell(12).getStringCellValue().trim()))
                            .siteCode(siteCode)
                            .siteName(siteName)
                            .parentId(parentId)
                            .build();
                    list.add(detail);
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return list;
    }

    public Boolean ins(List<SurfaceWaterActualflowDetail> input) {
        input.forEach(surfaceWaterActualflowDetailMapper::insert);
        return true;
    }

    public Boolean del(String id) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("parent_id", id);
        surfaceWaterActualflowDetailMapper.deleteByMap(map);
        return true;
    }

    public List<SurfaceWaterActualflowDetail> QueryList(String id) {
        /*序列化查询结构
         * */
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("parent_id", id);
        return surfaceWaterActualflowDetailMapper.selectByMap(map).stream().sorted(Comparator.comparing(SurfaceWaterActualflowDetail::getStartTime)).collect(Collectors.toList());
    }

    private LambdaQueryWrapper<SurfaceWaterActualflowDetail> wrapper(String id) {
        LambdaQueryWrapper<SurfaceWaterActualflowDetail> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SurfaceWaterActualflowDetail::getParentId, id)
                .orderBy(true, false, SurfaceWaterActualflowDetail::getStartTime);
        return wrapper;
    }

    public static boolean isParsable(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            sdf.parse(str);
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}




