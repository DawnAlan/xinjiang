package com.cj.waterresources.func.modular.surfaceWater.generator.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.waterresources.func.modular.surfaceWater.generator.domain.SurfaceWaterFlowDetail;
import com.cj.waterresources.func.modular.surfaceWater.generator.mapper.SurfaceWaterFlowDetailMapper;
import com.cj.waterresources.func.modular.surfaceWater.vo.SurfaceWaterFlowDetailVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
                    .sorted(Comparator.comparing(SurfaceWaterFlowDetail::getMonth))
                    .collect(Collectors.toList());
            SurfaceWaterFlowDetailVo DetailVo = new SurfaceWaterFlowDetailVo();
            DetailVo.setDay(i);
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
}




