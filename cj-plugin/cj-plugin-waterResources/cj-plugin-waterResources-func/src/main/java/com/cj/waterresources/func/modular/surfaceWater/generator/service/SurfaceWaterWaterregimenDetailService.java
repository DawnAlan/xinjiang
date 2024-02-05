package com.cj.waterresources.func.modular.surfaceWater.generator.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.waterresources.func.modular.surfaceWater.generator.domain.SurfaceWaterWaterregimenDetail;
import com.cj.waterresources.func.modular.surfaceWater.generator.mapper.SurfaceWaterWaterregimenDetailMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【surface_water_waterregimen_detail】的数据库操作Service实现
 * @createDate 2023-12-25 10:17:44
 */
@Service
@RequiredArgsConstructor
public class SurfaceWaterWaterregimenDetailService extends ServiceImpl<SurfaceWaterWaterregimenDetailMapper, SurfaceWaterWaterregimenDetail>
        implements IService<SurfaceWaterWaterregimenDetail> {
    private final SurfaceWaterWaterregimenDetailMapper surfaceWaterWaterregimenDetailMapper;

    public List<SurfaceWaterWaterregimenDetail> getFileList(MultipartFile file, String parentId, String siteCode, String siteName, Integer year) {
        List<SurfaceWaterWaterregimenDetail> list = new ArrayList<>();
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
                    // 将字符串转换为日期对象
                    Date sampleTime = DateUtil.getJavaDate(number);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("MM");
                    SimpleDateFormat sdf3 = new SimpleDateFormat("dd");
                    Integer year1 = Integer.parseInt(sdf1.format(sampleTime));
                    Integer month1 = Integer.parseInt(sdf2.format(sampleTime));
                    Integer day1 = Integer.parseInt(sdf3.format(sampleTime));
                    SurfaceWaterWaterregimenDetail detail = SurfaceWaterWaterregimenDetail.builder()
                            .id(UUID.randomUUID().toString())
                            .sampleTime(sampleTime)
                            .inReservoirAm(currentRow.getCell(1).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(1).getStringCellValue().trim()))
                            .inReservoirMean(currentRow.getCell(2).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(2).getStringCellValue().trim()))
                            .outReservoirAm(currentRow.getCell(3).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(3).getStringCellValue().trim()))
                            .outReservoirMean(currentRow.getCell(4).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(4).getStringCellValue().trim()))
                            .outRiverAm(currentRow.getCell(5).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(5).getStringCellValue().trim()))
                            .outRiverMean(currentRow.getCell(6).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(6).getStringCellValue().trim()))
                            .outConcealedAm(currentRow.getCell(7).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(7).getStringCellValue().trim()))
                            .outConcealedMean(currentRow.getCell(8).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(8).getStringCellValue().trim()))
                            .waterLevelAm(currentRow.getCell(9).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(9).getStringCellValue().trim()))
                            .waterLevelPm(currentRow.getCell(10).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(10).getStringCellValue().trim()))
                            .capacityAm(currentRow.getCell(11).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(11).getStringCellValue().trim()))
                            .capacityPm(currentRow.getCell(12).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(12).getStringCellValue().trim()))
                            .seepageFlow(currentRow.getCell(13).getStringCellValue().trim().isEmpty() ? null : currentRow.getCell(13).getStringCellValue().trim())
                            .bagangTurbidity(currentRow.getCell(14).getStringCellValue().trim().isEmpty() ? 0 : Integer.parseInt(currentRow.getCell(14).getStringCellValue().trim()))
                            .outTurbidityAm(currentRow.getCell(15).getStringCellValue().trim().isEmpty() ? 0 : Integer.parseInt(currentRow.getCell(15).getStringCellValue().trim()))
                            .outTurbidityPm(currentRow.getCell(16).getStringCellValue().trim().isEmpty() ? 0 : Integer.parseInt(currentRow.getCell(16).getStringCellValue().trim()))
                            .longkouTurbidityAm(currentRow.getCell(17).getStringCellValue().trim().isEmpty() ? 0 : Integer.parseInt(currentRow.getCell(17).getStringCellValue().trim()))
                            .longkouTurbidityPm(currentRow.getCell(18).getStringCellValue().trim().isEmpty() ? 0 : Integer.parseInt(currentRow.getCell(18).getStringCellValue().trim()))
                            .year(year1)
                            .month(month1)
                            .day(day1)
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

    public Boolean ins(List<SurfaceWaterWaterregimenDetail> input) {
        input.forEach(surfaceWaterWaterregimenDetailMapper::insert);
        return true;
    }

    public Boolean del(String id) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("parent_id", id);
        surfaceWaterWaterregimenDetailMapper.deleteByMap(map);
        return true;
    }

    public List<SurfaceWaterWaterregimenDetail> QueryList(String id) {
        /*序列化查询结构
         * */
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("parent_id", id);
        return surfaceWaterWaterregimenDetailMapper.selectByMap(map).stream().sorted(Comparator.comparing(SurfaceWaterWaterregimenDetail::getSampleTime)).collect(Collectors.toList());
    }

    private LambdaQueryWrapper<SurfaceWaterWaterregimenDetail> wrapper(String id) {
        LambdaQueryWrapper<SurfaceWaterWaterregimenDetail> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SurfaceWaterWaterregimenDetail::getParentId, id)
                .orderBy(true, false, SurfaceWaterWaterregimenDetail::getSampleTime);
        return wrapper;
    }
}




