package com.cj.waterresources.func.modular.surfaceWater.generator.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.waterresources.func.modular.surfaceWater.generator.domain.SurfaceWaterHydrologyDetail;
import com.cj.waterresources.func.modular.surfaceWater.generator.mapper.SurfaceWaterHydrologyDetailMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【surface_water_hydrology_detail(洪水水文)】的数据库操作Service实现
 * @createDate 2023-12-25 10:17:44
 */
@Service
@RequiredArgsConstructor
public class SurfaceWaterHydrologyDetailService extends ServiceImpl<SurfaceWaterHydrologyDetailMapper, SurfaceWaterHydrologyDetail>
        implements IService<SurfaceWaterHydrologyDetail> {
    private final SurfaceWaterHydrologyDetailMapper surfaceWaterHydrologyDetailMapper;

    public List<SurfaceWaterHydrologyDetail> getFileList(MultipartFile file, String parentId, String siteCode, String siteName, Integer year) {
        List<SurfaceWaterHydrologyDetail> list = new ArrayList<>();
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
                    if (Integer.parseInt(currentRow.getCell(0).getStringCellValue()) > 1) {
                        // 定义日期格式化模式
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String[] hm = currentRow.getCell(2).getStringCellValue().split(":");
                        String mm = hm[0];
                        String ss = "0";
                        if (hm.length > 1) {
                            ss = hm[1];
                        }
                        // 字符串拼接成的日期时间
                        String dateString = String.format("%s-%s-%s %s:%s:00",
                                year,
                                currentRow.getCell(0).getStringCellValue(),
                                currentRow.getCell(1).getStringCellValue(),
                                mm,
                                ss
                        );
                        BigDecimal flow = new BigDecimal(currentRow.getCell(3).getStringCellValue().trim());
                        BigDecimal level = new BigDecimal(currentRow.getCell(4).getStringCellValue().trim());
                        // 将字符串转换为日期对象
                        Date date = formatter.parse(dateString);
                        SurfaceWaterHydrologyDetail surfaceWaterHydrologyDetail = SurfaceWaterHydrologyDetail.builder()
                                .id(UUID.randomUUID().toString())
                                .sampleTime(date)
                                .flow(flow)
                                .level(level)
                                .siteCode(siteCode)
                                .siteName(siteName)
                                .parentId(parentId)
                                .build();
                        list.add(surfaceWaterHydrologyDetail);
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

    public Boolean ins(List<SurfaceWaterHydrologyDetail> input) {
        input.forEach(surfaceWaterHydrologyDetailMapper::insert);
        return true;
    }

    public Boolean del(String id) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("parent_id", id);
        surfaceWaterHydrologyDetailMapper.deleteByMap(map);
        return true;
    }

    public List<SurfaceWaterHydrologyDetail> QueryList(String id) {
        /*序列化查询结构
         * */
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("parent_id", id);
        return surfaceWaterHydrologyDetailMapper.selectByMap(map).stream().sorted(Comparator.comparing(SurfaceWaterHydrologyDetail::getSampleTime)).collect(Collectors.toList());
    }

    private LambdaQueryWrapper<SurfaceWaterHydrologyDetail> wrapper(String id) {
        LambdaQueryWrapper<SurfaceWaterHydrologyDetail> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SurfaceWaterHydrologyDetail::getParentId, id)
                .orderBy(true, false, SurfaceWaterHydrologyDetail::getSampleTime);
        return wrapper;
    }


}




