package com.cj.waterresources.func.modular.surfaceWater.generator.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.waterresources.func.modular.surfaceWater.generator.domain.SurfaceWaterFlowDetail;
import com.cj.waterresources.func.modular.surfaceWater.generator.mapper.SurfaceWaterFlowDetailMapper;
import com.cj.waterresources.func.modular.surfaceWater.vo.SurfaceWaterFlowDetailVo;
import com.cj.waterresources.func.modular.surfaceWater.vo.SurfaceWaterFlowVo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【surface_water_flow_detail(地表水水情数据子表)】的数据库操作Service实现
 * @createDate 2023-12-25 10:17:44
 */
@Service
@RequiredArgsConstructor
public class SurfaceWaterFlowDetailService extends ServiceImpl<SurfaceWaterFlowDetailMapper, SurfaceWaterFlowDetail>
        implements IService<SurfaceWaterFlowDetail> {
    private final SurfaceWaterFlowDetailMapper surfaceWaterFlowDetailMapper;

    public List<SurfaceWaterFlowDetail> getFileList(MultipartFile file, String parentId, String siteCode, String siteName, Integer year) {
        List<SurfaceWaterFlowDetail> list = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();
            Integer row = 1;
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();
                try {
                    currentRow.getCell(0).setCellType(CellType.STRING);
                    if (Integer.parseInt(currentRow.getCell(0).getStringCellValue()) == row) {
                        for (int i = 1; i <= 12; i++) {
                            try {
                                LocalDate localDate = LocalDate.of(year, i, row);
                            } catch (Exception e) {
                                continue;
                            }
                            currentRow.getCell(i).setCellType(CellType.STRING);
                            if (currentRow.getCell(i).getStringCellValue().trim().isEmpty()) {
                                continue;
                            }
                            // 定义日期格式化模式
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            // 字符串拼接成的日期时间
                            String dateString = String.format("%s-%s-%s", year, i, row);
                            // 将字符串转换为日期对象
                            Date date = formatter.parse(dateString);
                            SurfaceWaterFlowDetail surfaceWaterFlowDetail = SurfaceWaterFlowDetail.builder()
                                    .id(UUID.randomUUID().toString())
                                    .sampleTime(date)
                                    .year(year)
                                    .month(i)
                                    .day(row)
                                    .flow(new BigDecimal(currentRow.getCell(i).getStringCellValue()))
                                    .siteCode(siteCode)
                                    .siteName(siteName)
                                    .parentId(parentId)
                                    .build();
                            list.add(surfaceWaterFlowDetail);
                        }
                        row++;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return list;
    }

    public Boolean ins(List<SurfaceWaterFlowDetail> input) {
        input.forEach(surfaceWaterFlowDetailMapper::insert);
        return true;
    }

    public Boolean del(String id) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("parent_id", id);
        surfaceWaterFlowDetailMapper.deleteByMap(map);
        return true;
    }

    public List<SurfaceWaterFlowDetailVo> QueryList(String id) {
        List<SurfaceWaterFlowDetailVo> surfaceWaterFlowDetailVos = new ArrayList<>();
        /*序列化查询结构
         * */
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("parent_id", id);
        List<SurfaceWaterFlowDetail> list = surfaceWaterFlowDetailMapper.selectByMap(map);
        if (list.size() == 0) {
            return surfaceWaterFlowDetailVos;
        }
        for (int i = 1; i <= 31; i++) {
            int finalI = i;
            List<SurfaceWaterFlowDetail> dayList = list.stream()
                    .filter(f -> f.getDay() == finalI)
                    .sorted(Comparator.comparing(SurfaceWaterFlowDetail::getSampleTime))
                    .collect(Collectors.toList());
            SurfaceWaterFlowDetailVo DetailVo = new SurfaceWaterFlowDetailVo();
            DetailVo.setDay(String.valueOf(i));
            dayList.forEach(r -> {
                if (r.getMonth() == 1)
                    DetailVo.setJan(r.getFlow());
                if (r.getMonth() == 2)
                    DetailVo.setFeb(r.getFlow());
                if (r.getMonth() == 3)
                    DetailVo.setMar(r.getFlow());
                if (r.getMonth() == 4)
                    DetailVo.setAri(r.getFlow());
                if (r.getMonth() == 5)
                    DetailVo.setMay(r.getFlow());
                if (r.getMonth() == 6)
                    DetailVo.setJun(r.getFlow());
                if (r.getMonth() == 7)
                    DetailVo.setJul(r.getFlow());
                if (r.getMonth() == 8)
                    DetailVo.setAut(r.getFlow());
                if (r.getMonth() == 9)
                    DetailVo.setSep(r.getFlow());
                if (r.getMonth() == 10)
                    DetailVo.setOct(r.getFlow());
                if (r.getMonth() == 11)
                    DetailVo.setNov(r.getFlow());
                if (r.getMonth() == 12)
                    DetailVo.setDec(r.getFlow());
            });
            surfaceWaterFlowDetailVos.add(DetailVo);
        }
        return surfaceWaterFlowDetailVos;
    }

    private LambdaQueryWrapper<SurfaceWaterFlowDetail> wrapper(String id) {
        LambdaQueryWrapper<SurfaceWaterFlowDetail> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SurfaceWaterFlowDetail::getParentId, id)
                .orderBy(true, false, SurfaceWaterFlowDetail::getSampleTime);
        return wrapper;
    }

    /*
     *
     * */
    private List<SurfaceWaterFlowDetailVo> tenDay(String id) {
        List<SurfaceWaterFlowDetailVo> surfaceWaterFlowDetailVos = new ArrayList<>();
        SurfaceWaterFlowDetailVo sum1 = new SurfaceWaterFlowDetailVo();
        SurfaceWaterFlowDetailVo avg1 = new SurfaceWaterFlowDetailVo();
        SurfaceWaterFlowDetailVo sum2 = new SurfaceWaterFlowDetailVo();
        SurfaceWaterFlowDetailVo avg2 = new SurfaceWaterFlowDetailVo();
        SurfaceWaterFlowDetailVo sum3 = new SurfaceWaterFlowDetailVo();
        SurfaceWaterFlowDetailVo avg3 = new SurfaceWaterFlowDetailVo();
        SurfaceWaterFlowDetailVo sum4 = new SurfaceWaterFlowDetailVo();
        SurfaceWaterFlowDetailVo avg4 = new SurfaceWaterFlowDetailVo();
        SurfaceWaterFlowDetailVo max = new SurfaceWaterFlowDetailVo();
        SurfaceWaterFlowDetailVo maxday = new SurfaceWaterFlowDetailVo();
        SurfaceWaterFlowDetailVo min = new SurfaceWaterFlowDetailVo();
        SurfaceWaterFlowDetailVo minday = new SurfaceWaterFlowDetailVo();
        sum1.setDay("上旬总数");
        avg1.setDay("上旬平均");
        sum2.setDay("中旬总数");
        avg2.setDay("中旬平均");
        sum3.setDay("下旬总数");
        avg3.setDay("下旬平均");
        sum4.setDay("月总数");
        avg4.setDay("月平均");
        max.setDay("最大");
        maxday.setDay("最大日期");
        min.setDay("最小");
        minday.setDay("最小日期");
        /*序列化查询结构
         * */
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("parent_id", id);
        List<SurfaceWaterFlowDetail> list = surfaceWaterFlowDetailMapper.selectByMap(map);
        for (int i = 1; i <= 12; i++) {
            Integer finalI = i;
            List<SurfaceWaterFlowDetail> dayList = list.stream().filter(r -> r.getMonth().equals(finalI))
                    .sorted(Comparator.comparing(SurfaceWaterFlowDetail::getSampleTime))
                    .collect(Collectors.toList());
            // 将日期转换为旬并分组
            List<List<SurfaceWaterFlowDetail>> deciles = dayList.stream()
                    .collect(Collectors.groupingBy(it -> DateUtil.dayOfMonth(it.getSampleTime()) == 31 ? 2 :
                            (DateUtil.dayOfMonth(it.getSampleTime()) - 1) / 10)) // 按照旬分组
                    .values() // 获取所有的分组
                    .stream()
                    .map(group -> group.stream().map(date -> date).collect(Collectors.toList())) // 将每个分组转换为一个列表
                    .collect(Collectors.toList()); // 收集所有的列表到一个列表中
            if (i == 1) {
                sum1.setJan(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg1.setJan(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum2.setJan(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg2.setJan(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum3.setJan(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg3.setJan(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum4.setJan(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg4.setJan(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                max.setJan(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                maxday.setJan(BigDecimal.valueOf(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
                min.setJan(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                minday.setJan(BigDecimal.valueOf(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
            }
            if (i == 2) {
                sum1.setFeb(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg1.setFeb(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum2.setFeb(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg2.setFeb(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum3.setFeb(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg3.setFeb(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum4.setFeb(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg4.setFeb(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                max.setFeb(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                maxday.setFeb(BigDecimal.valueOf(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
                min.setFeb(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                minday.setFeb(BigDecimal.valueOf(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
            }
            if (i == 3) {
                sum1.setMar(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg1.setMar(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum2.setMar(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg2.setMar(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum3.setMar(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg3.setMar(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum4.setMar(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg4.setMar(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                max.setMar(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                maxday.setMar(BigDecimal.valueOf(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
                min.setMar(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                minday.setMar(BigDecimal.valueOf(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
            }
            if (i == 4) {
                sum1.setAri(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg1.setAri(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum2.setAri(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg2.setAri(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum3.setAri(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg3.setAri(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum4.setAri(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg4.setAri(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                max.setAri(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                maxday.setAri(BigDecimal.valueOf(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
                min.setAri(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                minday.setAri(BigDecimal.valueOf(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
            }
            if (i == 5) {
                sum1.setMay(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg1.setMay(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum2.setMay(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg2.setMay(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum3.setMay(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg3.setMay(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum4.setMay(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg4.setMay(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                max.setMay(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                maxday.setMay(BigDecimal.valueOf(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
                min.setMay(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                minday.setMay(BigDecimal.valueOf(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
            }
            if (i == 6) {
                sum1.setJun(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg1.setJun(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum2.setJun(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg2.setJun(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum3.setJun(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg3.setJun(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum4.setJun(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg4.setJun(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                max.setJun(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                maxday.setJun(BigDecimal.valueOf(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
                min.setJun(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                minday.setJun(BigDecimal.valueOf(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
            }
            if (i == 7) {
                sum1.setJul(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg1.setJul(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum2.setJul(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg2.setJul(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum3.setJul(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg3.setJul(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum4.setJul(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg4.setJul(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                max.setJul(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                maxday.setJul(BigDecimal.valueOf(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
                min.setJul(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                minday.setJul(BigDecimal.valueOf(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
            }
            if (i == 8) {
                sum1.setAut(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg1.setAut(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum2.setAut(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg2.setAut(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum3.setAut(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg3.setAut(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum4.setAut(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg4.setAut(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                max.setAut(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                maxday.setAut(BigDecimal.valueOf(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
                min.setAut(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                minday.setAut(BigDecimal.valueOf(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
            }
            if (i == 9) {
                sum1.setSep(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg1.setSep(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum2.setSep(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg2.setSep(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum3.setSep(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg3.setSep(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum4.setSep(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg4.setSep(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                max.setSep(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                maxday.setSep(BigDecimal.valueOf(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
                min.setSep(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                minday.setSep(BigDecimal.valueOf(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
            }
            if (i == 10) {
                sum1.setOct(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg1.setOct(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum2.setOct(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg2.setOct(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum3.setOct(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg3.setOct(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum4.setOct(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg4.setOct(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                max.setOct(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                maxday.setOct(BigDecimal.valueOf(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
                min.setOct(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                minday.setOct(BigDecimal.valueOf(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
            }
            if (i == 11) {
                sum1.setNov(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg1.setNov(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum2.setNov(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg2.setNov(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum3.setNov(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg3.setNov(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum4.setNov(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg4.setNov(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                max.setNov(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                maxday.setNov(BigDecimal.valueOf(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
                min.setNov(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                minday.setNov(BigDecimal.valueOf(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
            }
            if (i == 12) {
                sum1.setDec(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg1.setDec(BigDecimal.valueOf(deciles.get(0).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum2.setDec(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg2.setDec(BigDecimal.valueOf(deciles.get(1).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum3.setDec(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg3.setDec(BigDecimal.valueOf(deciles.get(2).stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                sum4.setDec(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).sum()));
                avg4.setDec(BigDecimal.valueOf(dayList.stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
                max.setDec(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                maxday.setDec(BigDecimal.valueOf(dayList.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
                min.setDec(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
                minday.setDec(BigDecimal.valueOf(dayList.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getDay()));
            }
        }
        surfaceWaterFlowDetailVos.add(sum1);
        surfaceWaterFlowDetailVos.add(avg1);
        surfaceWaterFlowDetailVos.add(sum2);
        surfaceWaterFlowDetailVos.add(avg2);
        surfaceWaterFlowDetailVos.add(sum3);
        surfaceWaterFlowDetailVos.add(avg3);
        surfaceWaterFlowDetailVos.add(sum4);
        surfaceWaterFlowDetailVos.add(avg4);
        surfaceWaterFlowDetailVos.add(max);
        surfaceWaterFlowDetailVos.add(maxday);
        surfaceWaterFlowDetailVos.add(min);
        surfaceWaterFlowDetailVos.add(minday);
        return surfaceWaterFlowDetailVos;
    }

    public SurfaceWaterFlowVo getsurf(String id) {
        SurfaceWaterFlowVo surfaceWaterFlowVo = new SurfaceWaterFlowVo();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("parent_id", id);
        List<SurfaceWaterFlowDetail> list = surfaceWaterFlowDetailMapper.selectByMap(map);
        if(list.size()<=0){
            return surfaceWaterFlowVo;
        }
        surfaceWaterFlowVo.setAnnual_average_flow(BigDecimal.valueOf(list.stream().mapToDouble(t -> t.getFlow().doubleValue()).average().getAsDouble()).setScale(2, RoundingMode.DOWN));
        BigDecimal sumflow = BigDecimal.valueOf(list.stream().mapToDouble(t -> t.getFlow().doubleValue()).sum());
        surfaceWaterFlowVo.setAnnual_runoff(BigDecimal.valueOf(sumflow.doubleValue() * 86400 / 10000 / 10000).setScale(2, RoundingMode.DOWN));
        surfaceWaterFlowVo.setAnnual_max(list.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
        surfaceWaterFlowVo.setAnnual_maxDay(list.stream().max(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getSampleTime());
        surfaceWaterFlowVo.setAnnual_min(list.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getFlow());
        surfaceWaterFlowVo.setAnnual_minDay(list.stream().min(Comparator.comparing(SurfaceWaterFlowDetail::getFlow)).get().getSampleTime());
        List<SurfaceWaterFlowDetailVo>  su1 =  QueryList(id);
        List<SurfaceWaterFlowDetailVo>  su2 =  tenDay(id);
        su1.addAll(su2);
        surfaceWaterFlowVo.setFlowDetailVos(su1);
        return surfaceWaterFlowVo;
    }

}




