package com.cj.waterresources.func.modular.surfaceWater.generator.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.waterresources.func.modular.surfaceWater.generator.domain.SurfaceWaterFlowDetail;
import com.cj.waterresources.func.modular.surfaceWater.generator.domain.SurfaceWaterWaterregimenDetail;
import com.cj.waterresources.func.modular.surfaceWater.generator.mapper.SurfaceWaterWaterregimenDetailMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
                    SurfaceWaterWaterregimenDetail detail;
                    if (siteName.contains("头屯河")) {
                        detail = SurfaceWaterWaterregimenDetail.builder()
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
                    } else {
                        detail = SurfaceWaterWaterregimenDetail.builder()
                                .id(UUID.randomUUID().toString())
                                .sampleTime(sampleTime)
                                .inReservoirAm(currentRow.getCell(1).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(1).getStringCellValue().trim()))
                                .inReservoirMean(currentRow.getCell(2).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(2).getStringCellValue().trim()))
                                .outReservoirAm(currentRow.getCell(3).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(3).getStringCellValue().trim()))
                                .outReservoirMean(currentRow.getCell(4).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(4).getStringCellValue().trim()))
                                .outRiverAm(currentRow.getCell(5).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(5).getStringCellValue().trim()))
                                .outRiverMean(currentRow.getCell(6).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(6).getStringCellValue().trim()))
                                .waterLevelAm(currentRow.getCell(7).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(7).getStringCellValue().trim()))
                                .waterLevelPm(currentRow.getCell(8).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(8).getStringCellValue().trim()))
                                .capacityAm(currentRow.getCell(9).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(9).getStringCellValue().trim()))
                                .capacityPm(currentRow.getCell(10).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(10).getStringCellValue().trim()))
                                .lzzSc1Am(currentRow.getCell(11).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(11).getStringCellValue().trim()))
                                .lzzSc2Am(currentRow.getCell(12).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(12).getStringCellValue().trim()))
                                .lzzScMean(currentRow.getCell(13).getStringCellValue().trim().isEmpty() ? null : new BigDecimal(currentRow.getCell(13).getStringCellValue().trim()))
                                .year(year1)
                                .month(month1)
                                .day(day1)
                                .parentId(parentId)
                                .build();
                    }

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
        List<SurfaceWaterWaterregimenDetail> list= surfaceWaterWaterregimenDetailMapper.selectByMap(map).stream().sorted(Comparator.comparing(SurfaceWaterWaterregimenDetail::getSampleTime)).collect(Collectors.toList());
        list.addAll(ten_day(id,list));
        return list;
    }

    private LambdaQueryWrapper<SurfaceWaterWaterregimenDetail> wrapper(String id) {
        LambdaQueryWrapper<SurfaceWaterWaterregimenDetail> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SurfaceWaterWaterregimenDetail::getParentId, id)
                .orderBy(true, false, SurfaceWaterWaterregimenDetail::getSampleTime);
        return wrapper;
    }

    public List<SurfaceWaterWaterregimenDetail> ten_day(String id,List<SurfaceWaterWaterregimenDetail> dayList) {
        // 将日期转换为旬并分组
        List<List<SurfaceWaterWaterregimenDetail>> deciles = dayList.stream()
                .collect(Collectors.groupingBy(it -> cn.hutool.core.date.DateUtil.dayOfMonth(it.getSampleTime()) == 31 ? 2 :
                        (cn.hutool.core.date.DateUtil.dayOfMonth(it.getSampleTime()) - 1) / 10)) // 按照旬分组
                .values() // 获取所有的分组
                .stream()
                .map(group -> group.stream().map(date -> date).collect(Collectors.toList())) // 将每个分组转换为一个列表
                .collect(Collectors.toList()); // 收集所有的列表到一个列表中

        SurfaceWaterWaterregimenDetail bu1 = SurfaceWaterWaterregimenDetail.builder()
                .id("上旬总数")
                .inReservoirAm(new BigDecimal(deciles.get(0).stream().filter(t->t.getInReservoirAm() != null).mapToDouble(t -> t.getInReservoirAm().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .inReservoirMean(new BigDecimal(deciles.get(0).stream().filter(t->t.getInReservoirMean() != null).mapToDouble(t -> t.getInReservoirMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outReservoirAm(new BigDecimal(deciles.get(0).stream().filter(t->t.getOutReservoirAm() != null).mapToDouble(t -> t.getOutReservoirAm().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outReservoirMean(new BigDecimal(deciles.get(0).stream().filter(t->t.getOutReservoirMean() != null).mapToDouble(t -> t.getOutReservoirMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outRiverAm(new BigDecimal(deciles.get(0).stream().filter(t->t.getOutRiverAm() != null).mapToDouble(t -> t.getOutRiverAm().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outRiverMean(new BigDecimal(deciles.get(0).stream().filter(t->t.getOutRiverMean() != null).mapToDouble(t -> t.getOutRiverMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outConcealedAm(new BigDecimal(deciles.get(0).stream().filter(t->t.getOutConcealedAm() != null).mapToDouble(t -> t.getOutConcealedAm().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outConcealedMean(new BigDecimal(deciles.get(0).stream().filter(t->t.getOutConcealedMean() != null).mapToDouble(t -> t.getOutConcealedMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .lzzSc1Am(new BigDecimal(deciles.get(0).stream().filter(t->t.getLzzSc1Am() != null).mapToDouble(t -> t.getLzzSc1Am().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .lzzSc2Am(new BigDecimal(deciles.get(0).stream().filter(t->t.getLzzSc2Am() != null).mapToDouble(t -> t.getLzzSc2Am().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .lzzScMean(new BigDecimal(deciles.get(0).stream().filter(t->t.getLzzScMean() != null).mapToDouble(t -> t.getLzzScMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .build();
        SurfaceWaterWaterregimenDetail bu2 = SurfaceWaterWaterregimenDetail.builder()
                .id("上旬平均")
                .inReservoirAm(new BigDecimal(deciles.get(0).stream().filter(t->t.getInReservoirAm() != null).mapToDouble(t -> t.getInReservoirAm().doubleValue()).sum() / deciles.get(0).size()).setScale(3, RoundingMode.DOWN))
                .inReservoirMean(new BigDecimal(deciles.get(0).stream().filter(t->t.getInReservoirMean() != null).mapToDouble(t -> t.getInReservoirMean().doubleValue()).sum() / deciles.get(0).size()).setScale(3, RoundingMode.DOWN))
                .outReservoirAm(new BigDecimal(deciles.get(0).stream().filter(t->t.getOutReservoirAm() != null).mapToDouble(t -> t.getOutReservoirAm().doubleValue()).sum() / deciles.get(0).size()).setScale(3, RoundingMode.DOWN))
                .outReservoirMean(new BigDecimal(deciles.get(0).stream().filter(t->t.getOutReservoirMean() != null).mapToDouble(t -> t.getOutReservoirMean().doubleValue()).sum() / deciles.get(0).size()).setScale(3, RoundingMode.DOWN))
                .outRiverAm(new BigDecimal(deciles.get(0).stream().filter(t->t.getOutRiverAm() != null).mapToDouble(t -> t.getOutRiverAm().doubleValue()).sum() / deciles.get(0).size()).setScale(3, RoundingMode.DOWN))
                .outRiverMean(new BigDecimal(deciles.get(0).stream().filter(t->t.getOutRiverMean() != null).mapToDouble(t -> t.getOutRiverMean().doubleValue()).sum() / deciles.get(0).size()).setScale(3, RoundingMode.DOWN))
                .outConcealedAm(new BigDecimal(deciles.get(0).stream().filter(t->t.getOutConcealedAm() != null).mapToDouble(t -> t.getOutConcealedAm().doubleValue()).sum() / deciles.get(0).size()).setScale(3, RoundingMode.DOWN))
                .outConcealedMean(new BigDecimal(deciles.get(0).stream().filter(t->t.getOutConcealedMean() != null).mapToDouble(t -> t.getOutConcealedMean().doubleValue()).sum() / deciles.get(0).size()).setScale(3, RoundingMode.DOWN))
                .lzzSc1Am(new BigDecimal(deciles.get(0).stream().filter(t->t.getLzzSc1Am() != null).mapToDouble(t -> t.getLzzSc1Am().doubleValue()).sum() / deciles.get(0).size()).setScale(3, RoundingMode.DOWN))
                .lzzSc2Am(new BigDecimal(deciles.get(0).stream().filter(t->t.getLzzSc2Am() != null).mapToDouble(t -> t.getLzzSc2Am().doubleValue()).sum() / deciles.get(0).size()).setScale(3, RoundingMode.DOWN))
                .lzzScMean(new BigDecimal(deciles.get(0).stream().filter(t->t.getLzzScMean() != null).mapToDouble(t -> t.getLzzScMean().doubleValue()).sum() / deciles.get(0).size()).setScale(3, RoundingMode.DOWN))
                .build();
        SurfaceWaterWaterregimenDetail bu3 = SurfaceWaterWaterregimenDetail.builder()
                .id("上旬水量")
                .inReservoirAm(new BigDecimal(deciles.get(0).stream().filter(t->t.getInReservoirAm() != null).mapToDouble(t -> t.getInReservoirAm().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .inReservoirMean(new BigDecimal(deciles.get(0).stream().filter(t->t.getInReservoirMean() != null).mapToDouble(t -> t.getInReservoirMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outReservoirAm(new BigDecimal(deciles.get(0).stream().filter(t->t.getOutReservoirAm() != null).mapToDouble(t -> t.getOutReservoirAm().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outReservoirMean(new BigDecimal(deciles.get(0).stream().filter(t->t.getOutReservoirMean() != null).mapToDouble(t -> t.getOutReservoirMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outRiverAm(new BigDecimal(deciles.get(0).stream().filter(t->t.getOutRiverAm() != null).mapToDouble(t -> t.getOutRiverAm().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outRiverMean(new BigDecimal(deciles.get(0).stream().filter(t->t.getOutRiverMean() != null).mapToDouble(t -> t.getOutRiverMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outConcealedAm(new BigDecimal(deciles.get(0).stream().filter(t->t.getOutConcealedAm() != null).mapToDouble(t -> t.getOutConcealedAm().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outConcealedMean(new BigDecimal(deciles.get(0).stream().filter(t->t.getOutConcealedMean() != null).mapToDouble(t -> t.getOutConcealedMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .lzzSc1Am(new BigDecimal(deciles.get(0).stream().filter(t->t.getLzzSc1Am() != null).mapToDouble(t -> t.getLzzSc1Am().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .lzzSc2Am(new BigDecimal(deciles.get(0).stream().filter(t->t.getLzzSc2Am() != null).mapToDouble(t -> t.getLzzSc2Am().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .lzzScMean(new BigDecimal(deciles.get(0).stream().filter(t->t.getLzzScMean() != null).mapToDouble(t -> t.getLzzScMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .build();

        SurfaceWaterWaterregimenDetail bu4 = SurfaceWaterWaterregimenDetail.builder()
                .id("中旬总数")
                .inReservoirAm(new BigDecimal(deciles.get(1).stream().filter(t->t.getInReservoirAm() != null).mapToDouble(t -> t.getInReservoirAm().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .inReservoirMean(new BigDecimal(deciles.get(1).stream().filter(t->t.getInReservoirMean() != null).mapToDouble(t -> t.getInReservoirMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outReservoirAm(new BigDecimal(deciles.get(1).stream().filter(t->t.getOutReservoirAm() != null).mapToDouble(t -> t.getOutReservoirAm().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outReservoirMean(new BigDecimal(deciles.get(1).stream().filter(t->t.getOutReservoirMean() != null).mapToDouble(t -> t.getOutReservoirMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outRiverAm(new BigDecimal(deciles.get(1).stream().filter(t->t.getOutRiverAm() != null).mapToDouble(t -> t.getOutRiverAm().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outRiverMean(new BigDecimal(deciles.get(1).stream().filter(t->t.getOutRiverMean() != null).mapToDouble(t -> t.getOutRiverMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outConcealedAm(new BigDecimal(deciles.get(1).stream().filter(t->t.getOutConcealedAm() != null).mapToDouble(t -> t.getOutConcealedAm().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outConcealedMean(new BigDecimal(deciles.get(1).stream().filter(t->t.getOutConcealedMean() != null).mapToDouble(t -> t.getOutConcealedMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .lzzSc1Am(new BigDecimal(deciles.get(1).stream().filter(t->t.getLzzSc1Am() != null).mapToDouble(t -> t.getLzzSc1Am().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .lzzSc2Am(new BigDecimal(deciles.get(1).stream().filter(t->t.getLzzSc2Am() != null).mapToDouble(t -> t.getLzzSc2Am().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .lzzScMean(new BigDecimal(deciles.get(1).stream().filter(t->t.getLzzScMean() != null).mapToDouble(t -> t.getLzzScMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .build();
        SurfaceWaterWaterregimenDetail bu5 = SurfaceWaterWaterregimenDetail.builder()
                .id("中旬平均")
                .inReservoirAm(new BigDecimal(deciles.get(1).stream().filter(t->t.getInReservoirAm() != null).mapToDouble(t -> t.getInReservoirAm().doubleValue()).sum() / deciles.get(1).size()).setScale(3, RoundingMode.DOWN))
                .inReservoirMean(new BigDecimal(deciles.get(1).stream().filter(t->t.getInReservoirMean() != null).mapToDouble(t -> t.getInReservoirMean().doubleValue()).sum() / deciles.get(1).size()).setScale(3, RoundingMode.DOWN))
                .outReservoirAm(new BigDecimal(deciles.get(1).stream().filter(t->t.getOutReservoirAm() != null).mapToDouble(t -> t.getOutReservoirAm().doubleValue()).sum() / deciles.get(1).size()).setScale(3, RoundingMode.DOWN))
                .outReservoirMean(new BigDecimal(deciles.get(1).stream().filter(t->t.getOutReservoirMean() != null).mapToDouble(t -> t.getOutReservoirMean().doubleValue()).sum() / deciles.get(1).size()).setScale(3, RoundingMode.DOWN))
                .outRiverAm(new BigDecimal(deciles.get(1).stream().filter(t->t.getOutRiverAm() != null).mapToDouble(t -> t.getOutRiverAm().doubleValue()).sum() / deciles.get(1).size()).setScale(3, RoundingMode.DOWN))
                .outRiverMean(new BigDecimal(deciles.get(1).stream().filter(t->t.getOutRiverMean() != null).mapToDouble(t -> t.getOutRiverMean().doubleValue()).sum() / deciles.get(1).size()).setScale(3, RoundingMode.DOWN))
                .outConcealedAm(new BigDecimal(deciles.get(1).stream().filter(t->t.getOutConcealedAm() != null).mapToDouble(t -> t.getOutConcealedAm().doubleValue()).sum() / deciles.get(1).size()).setScale(3, RoundingMode.DOWN))
                .outConcealedMean(new BigDecimal(deciles.get(1).stream().filter(t->t.getOutConcealedMean() != null).mapToDouble(t -> t.getOutConcealedMean().doubleValue()).sum() / deciles.get(1).size()).setScale(3, RoundingMode.DOWN))
                .lzzSc1Am(new BigDecimal(deciles.get(1).stream().filter(t->t.getLzzSc1Am() != null).mapToDouble(t -> t.getLzzSc1Am().doubleValue()).sum() / deciles.get(1).size()).setScale(3, RoundingMode.DOWN))
                .lzzSc2Am(new BigDecimal(deciles.get(1).stream().filter(t->t.getLzzSc2Am() != null).mapToDouble(t -> t.getLzzSc2Am().doubleValue()).sum() / deciles.get(1).size()).setScale(3, RoundingMode.DOWN))
                .lzzScMean(new BigDecimal(deciles.get(1).stream().filter(t->t.getLzzScMean() != null).mapToDouble(t -> t.getLzzScMean().doubleValue()).sum() / deciles.get(1).size()).setScale(3, RoundingMode.DOWN))
                .build();
        SurfaceWaterWaterregimenDetail bu6 = SurfaceWaterWaterregimenDetail.builder()
                .id("中旬水量")
                .inReservoirAm(new BigDecimal(deciles.get(1).stream().filter(t->t.getInReservoirAm() != null).mapToDouble(t -> t.getInReservoirAm().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .inReservoirMean(new BigDecimal(deciles.get(1).stream().filter(t->t.getInReservoirMean() != null).mapToDouble(t -> t.getInReservoirMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outReservoirAm(new BigDecimal(deciles.get(1).stream().filter(t->t.getOutReservoirAm() != null).mapToDouble(t -> t.getOutReservoirAm().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outReservoirMean(new BigDecimal(deciles.get(1).stream().filter(t->t.getOutReservoirMean() != null).mapToDouble(t -> t.getOutReservoirMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outRiverAm(new BigDecimal(deciles.get(1).stream().filter(t->t.getOutRiverAm() != null).mapToDouble(t -> t.getOutRiverAm().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outRiverMean(new BigDecimal(deciles.get(1).stream().filter(t->t.getOutRiverMean() != null).mapToDouble(t -> t.getOutRiverMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outConcealedAm(new BigDecimal(deciles.get(1).stream().filter(t->t.getOutConcealedAm() != null).mapToDouble(t -> t.getOutConcealedAm().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outConcealedMean(new BigDecimal(deciles.get(1).stream().filter(t->t.getOutConcealedMean() != null).mapToDouble(t -> t.getOutConcealedMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .lzzSc1Am(new BigDecimal(deciles.get(1).stream().filter(t->t.getLzzSc1Am() != null).mapToDouble(t -> t.getLzzSc1Am().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .lzzSc2Am(new BigDecimal(deciles.get(1).stream().filter(t->t.getLzzSc2Am() != null).mapToDouble(t -> t.getLzzSc2Am().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .lzzScMean(new BigDecimal(deciles.get(1).stream().filter(t->t.getLzzScMean() != null).mapToDouble(t -> t.getLzzScMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .build();

        SurfaceWaterWaterregimenDetail bu7 = SurfaceWaterWaterregimenDetail.builder()
                .id("下旬总数")
                .inReservoirAm(new BigDecimal(deciles.get(2).stream().filter(t->t.getInReservoirAm() != null).mapToDouble(t -> t.getInReservoirAm().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .inReservoirMean(new BigDecimal(deciles.get(2).stream().filter(t->t.getInReservoirMean() != null).mapToDouble(t -> t.getInReservoirMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outReservoirAm(new BigDecimal(deciles.get(2).stream().filter(t->t.getOutReservoirAm() != null).mapToDouble(t -> t.getOutReservoirAm().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outReservoirMean(new BigDecimal(deciles.get(2).stream().filter(t->t.getOutReservoirMean() != null).mapToDouble(t -> t.getOutReservoirMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outRiverAm(new BigDecimal(deciles.get(2).stream().filter(t->t.getOutRiverAm() != null).mapToDouble(t -> t.getOutRiverAm().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outRiverMean(new BigDecimal(deciles.get(2).stream().filter(t->t.getOutRiverMean() != null).mapToDouble(t -> t.getOutRiverMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outConcealedAm(new BigDecimal(deciles.get(2).stream().filter(t->t.getOutConcealedAm() != null).mapToDouble(t -> t.getOutConcealedAm().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outConcealedMean(new BigDecimal(deciles.get(2).stream().filter(t->t.getOutConcealedMean() != null).mapToDouble(t -> t.getOutConcealedMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .lzzSc1Am(new BigDecimal(deciles.get(2).stream().filter(t->t.getLzzSc1Am() != null).mapToDouble(t -> t.getLzzSc1Am().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .lzzSc2Am(new BigDecimal(deciles.get(2).stream().filter(t->t.getLzzSc2Am() != null).mapToDouble(t -> t.getLzzSc2Am().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .lzzScMean(new BigDecimal(deciles.get(2).stream().filter(t->t.getLzzScMean() != null).mapToDouble(t -> t.getLzzScMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .build();
        SurfaceWaterWaterregimenDetail bu8 = SurfaceWaterWaterregimenDetail.builder()
                .id("下旬平均")
                .inReservoirAm(new BigDecimal(deciles.get(2).stream().filter(t->t.getInReservoirAm() != null).mapToDouble(t -> t.getInReservoirAm().doubleValue()).sum() / deciles.get(2).size()).setScale(3, RoundingMode.DOWN))
                .inReservoirMean(new BigDecimal(deciles.get(2).stream().filter(t->t.getInReservoirMean() != null).mapToDouble(t -> t.getInReservoirMean().doubleValue()).sum() / deciles.get(2).size()).setScale(3, RoundingMode.DOWN))
                .outReservoirAm(new BigDecimal(deciles.get(2).stream().filter(t->t.getOutReservoirAm() != null).mapToDouble(t -> t.getOutReservoirAm().doubleValue()).sum() / deciles.get(2).size()).setScale(3, RoundingMode.DOWN))
                .outReservoirMean(new BigDecimal(deciles.get(2).stream().filter(t->t.getOutReservoirMean() != null).mapToDouble(t -> t.getOutReservoirMean().doubleValue()).sum() / deciles.get(2).size()).setScale(3, RoundingMode.DOWN))
                .outRiverAm(new BigDecimal(deciles.get(2).stream().filter(t->t.getOutRiverAm() != null).mapToDouble(t -> t.getOutRiverAm().doubleValue()).sum() / deciles.get(2).size()).setScale(3, RoundingMode.DOWN))
                .outRiverMean(new BigDecimal(deciles.get(2).stream().filter(t->t.getOutRiverMean() != null).mapToDouble(t -> t.getOutRiverMean().doubleValue()).sum() / deciles.get(2).size()).setScale(3, RoundingMode.DOWN))
                .outConcealedAm(new BigDecimal(deciles.get(2).stream().filter(t->t.getOutConcealedAm() != null).mapToDouble(t -> t.getOutConcealedAm().doubleValue()).sum() / deciles.get(2).size()).setScale(3, RoundingMode.DOWN))
                .outConcealedMean(new BigDecimal(deciles.get(2).stream().filter(t->t.getOutConcealedMean() != null).mapToDouble(t -> t.getOutConcealedMean().doubleValue()).sum() / deciles.get(2).size()).setScale(3, RoundingMode.DOWN))
                .lzzSc1Am(new BigDecimal(deciles.get(2).stream().filter(t->t.getLzzSc1Am() != null).mapToDouble(t -> t.getLzzSc1Am().doubleValue()).sum() / deciles.get(2).size()).setScale(3, RoundingMode.DOWN))
                .lzzSc2Am(new BigDecimal(deciles.get(2).stream().filter(t->t.getLzzSc2Am() != null).mapToDouble(t -> t.getLzzSc2Am().doubleValue()).sum() / deciles.get(2).size()).setScale(3, RoundingMode.DOWN))
                .lzzScMean(new BigDecimal(deciles.get(2).stream().filter(t->t.getLzzScMean() != null).mapToDouble(t -> t.getLzzScMean().doubleValue()).sum() / deciles.get(2).size()).setScale(3, RoundingMode.DOWN))
                .build();
        SurfaceWaterWaterregimenDetail bu9 = SurfaceWaterWaterregimenDetail.builder()
                .id("下旬水量")
                .inReservoirAm(new BigDecimal(deciles.get(2).stream().filter(t->t.getInReservoirAm() != null).mapToDouble(t -> t.getInReservoirAm().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .inReservoirMean(new BigDecimal(deciles.get(2).stream().filter(t->t.getInReservoirMean() != null).mapToDouble(t -> t.getInReservoirMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outReservoirAm(new BigDecimal(deciles.get(2).stream().filter(t->t.getOutReservoirAm() != null).mapToDouble(t -> t.getOutReservoirAm().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outReservoirMean(new BigDecimal(deciles.get(2).stream().filter(t->t.getOutReservoirMean() != null).mapToDouble(t -> t.getOutReservoirMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outRiverAm(new BigDecimal(deciles.get(2).stream().filter(t->t.getOutRiverAm() != null).mapToDouble(t -> t.getOutRiverAm().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outRiverMean(new BigDecimal(deciles.get(2).stream().filter(t->t.getOutRiverMean() != null).mapToDouble(t -> t.getOutRiverMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outConcealedAm(new BigDecimal(deciles.get(2).stream().filter(t->t.getOutConcealedAm() != null).mapToDouble(t -> t.getOutConcealedAm().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outConcealedMean(new BigDecimal(deciles.get(2).stream().filter(t->t.getOutConcealedMean() != null).mapToDouble(t -> t.getOutConcealedMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .lzzSc1Am(new BigDecimal(deciles.get(2).stream().filter(t->t.getLzzSc1Am() != null).mapToDouble(t -> t.getLzzSc1Am().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .lzzSc2Am(new BigDecimal(deciles.get(2).stream().filter(t->t.getLzzSc2Am() != null).mapToDouble(t -> t.getLzzSc2Am().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .lzzScMean(new BigDecimal(deciles.get(2).stream().filter(t->t.getLzzScMean() != null).mapToDouble(t -> t.getLzzScMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .build();

        SurfaceWaterWaterregimenDetail bu10 = SurfaceWaterWaterregimenDetail.builder()
                .id("月总数")
                .inReservoirAm(new BigDecimal(dayList.stream().filter(t->t.getInReservoirAm() != null).mapToDouble(t -> t.getInReservoirAm().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .inReservoirMean(new BigDecimal(dayList.stream().filter(t->t.getInReservoirMean() != null).mapToDouble(t -> t.getInReservoirMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outReservoirAm(new BigDecimal(dayList.stream().filter(t->t.getOutReservoirAm() != null).mapToDouble(t -> t.getOutReservoirAm().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outReservoirMean(new BigDecimal(dayList.stream().filter(t->t.getOutReservoirMean() != null).mapToDouble(t -> t.getOutReservoirMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outRiverAm(new BigDecimal(dayList.stream().filter(t->t.getOutRiverAm() != null).mapToDouble(t -> t.getOutRiverAm().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outRiverMean(new BigDecimal(dayList.stream().filter(t->t.getOutRiverMean() != null).mapToDouble(t -> t.getOutRiverMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outConcealedAm(new BigDecimal(dayList.stream().filter(t->t.getOutConcealedAm() != null).mapToDouble(t -> t.getOutConcealedAm().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .outConcealedMean(new BigDecimal(dayList.stream().filter(t->t.getOutConcealedMean() != null).mapToDouble(t -> t.getOutConcealedMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .lzzSc1Am(new BigDecimal(dayList.stream().filter(t->t.getLzzSc1Am() != null).mapToDouble(t -> t.getLzzSc1Am().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .lzzSc2Am(new BigDecimal(dayList.stream().filter(t->t.getLzzSc2Am() != null).mapToDouble(t -> t.getLzzSc2Am().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .lzzScMean(new BigDecimal(dayList.stream().filter(t->t.getLzzScMean() != null).mapToDouble(t -> t.getLzzScMean().doubleValue()).sum()).setScale(3, RoundingMode.DOWN))
                .build();
        SurfaceWaterWaterregimenDetail bu11 = SurfaceWaterWaterregimenDetail.builder()
                .id("月平均")
                .inReservoirAm(new BigDecimal(dayList.stream().filter(t->t.getInReservoirAm() != null).mapToDouble(t -> t.getInReservoirAm().doubleValue()).sum() / dayList.size()).setScale(3, RoundingMode.DOWN))
                .inReservoirMean(new BigDecimal(dayList.stream().filter(t->t.getInReservoirMean() != null).mapToDouble(t -> t.getInReservoirMean().doubleValue()).sum() / dayList.size()).setScale(3, RoundingMode.DOWN))
                .outReservoirAm(new BigDecimal(dayList.stream().filter(t->t.getOutReservoirAm() != null).mapToDouble(t -> t.getOutReservoirAm().doubleValue()).sum() / dayList.size()).setScale(3, RoundingMode.DOWN))
                .outReservoirMean(new BigDecimal(dayList.stream().filter(t->t.getOutReservoirMean() != null).mapToDouble(t -> t.getOutReservoirMean().doubleValue()).sum() / dayList.size()).setScale(3, RoundingMode.DOWN))
                .outRiverAm(new BigDecimal(dayList.stream().filter(t->t.getOutRiverAm() != null).mapToDouble(t -> t.getOutRiverAm().doubleValue()).sum() / dayList.size()).setScale(3, RoundingMode.DOWN))
                .outRiverMean(new BigDecimal(dayList.stream().filter(t->t.getOutRiverMean() != null).mapToDouble(t -> t.getOutRiverMean().doubleValue()).sum() / dayList.size()).setScale(3, RoundingMode.DOWN))
                .outConcealedAm(new BigDecimal(dayList.stream().filter(t->t.getOutConcealedAm() != null).mapToDouble(t -> t.getOutConcealedAm().doubleValue()).sum() / dayList.size()).setScale(3, RoundingMode.DOWN))
                .outConcealedMean(new BigDecimal(dayList.stream().filter(t->t.getOutConcealedMean() != null).mapToDouble(t -> t.getOutConcealedMean().doubleValue()).sum() / dayList.size()).setScale(3, RoundingMode.DOWN))
                .lzzSc1Am(new BigDecimal(dayList.stream().filter(t->t.getLzzSc1Am() != null).mapToDouble(t -> t.getLzzSc1Am().doubleValue()).sum() / dayList.size()).setScale(3, RoundingMode.DOWN))
                .lzzSc2Am(new BigDecimal(dayList.stream().filter(t->t.getLzzSc2Am() != null).mapToDouble(t -> t.getLzzSc2Am().doubleValue()).sum() / dayList.size()).setScale(3, RoundingMode.DOWN))
                .lzzScMean(new BigDecimal(dayList.stream().filter(t->t.getLzzScMean() != null).mapToDouble(t -> t.getLzzScMean().doubleValue()).sum() / dayList.size()).setScale(3, RoundingMode.DOWN))
                .build();
        SurfaceWaterWaterregimenDetail bu12 = SurfaceWaterWaterregimenDetail.builder()
                .id("月水量")
                .inReservoirAm(new BigDecimal(dayList.stream().filter(t->t.getInReservoirAm() != null).mapToDouble(t -> t.getInReservoirAm().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .inReservoirMean(new BigDecimal(dayList.stream().filter(t->t.getInReservoirMean() != null).mapToDouble(t -> t.getInReservoirMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outReservoirAm(new BigDecimal(dayList.stream().filter(t->t.getOutReservoirAm() != null).mapToDouble(t -> t.getOutReservoirAm().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outReservoirMean(new BigDecimal(dayList.stream().filter(t->t.getOutReservoirMean() != null).mapToDouble(t -> t.getOutReservoirMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outRiverAm(new BigDecimal(dayList.stream().filter(t->t.getOutRiverAm() != null).mapToDouble(t -> t.getOutRiverAm().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outRiverMean(new BigDecimal(dayList.stream().filter(t->t.getOutRiverMean() != null).mapToDouble(t -> t.getOutRiverMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outConcealedAm(new BigDecimal(dayList.stream().filter(t->t.getOutConcealedAm() != null).mapToDouble(t -> t.getOutConcealedAm().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .outConcealedMean(new BigDecimal(dayList.stream().filter(t->t.getOutConcealedMean() != null).mapToDouble(t -> t.getOutConcealedMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .lzzSc1Am(new BigDecimal(dayList.stream().filter(t->t.getLzzSc1Am() != null).mapToDouble(t -> t.getLzzSc1Am().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .lzzSc2Am(new BigDecimal(dayList.stream().filter(t->t.getLzzSc2Am() != null).mapToDouble(t -> t.getLzzSc2Am().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .lzzScMean(new BigDecimal(dayList.stream().filter(t->t.getLzzScMean() != null).mapToDouble(t -> t.getLzzScMean().doubleValue()).sum() * 86400).setScale(3, RoundingMode.DOWN))
                .build();
        
        
        List<SurfaceWaterWaterregimenDetail> map = new ArrayList<>();
        map.add(bu1);
        map.add(bu2);
        map.add(bu3);
        map.add(bu4);
        map.add(bu5);
        map.add(bu6);
        map.add(bu7);
        map.add(bu8);
        map.add(bu9);
        map.add(bu10);
        map.add(bu11);
        map.add(bu12);
        return map;
    }

}




