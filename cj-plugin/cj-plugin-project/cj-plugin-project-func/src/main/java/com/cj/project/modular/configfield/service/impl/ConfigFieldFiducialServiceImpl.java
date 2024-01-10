package com.cj.project.modular.configfield.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.pojo.CommonResult;
import com.cj.common.util.ExcelUtils;
import com.cj.project.api.configfield.dto.ConfigFieldFiducialDto;
import com.cj.project.api.configfield.dto.ConfigFieldFiducialPageDto;
import com.cj.project.api.configfield.dto.ConfigFieldFiducialQueryDto;
import com.cj.project.api.configfield.entity.ConfigFieldFiducial;
import com.cj.project.modular.configfield.result.ConfigFieldFiducialResult;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cj.common.enums.CommonSortOrderEnum;
import com.cj.common.exception.CommonException;
import com.cj.common.page.CommonPageRequest;
import com.cj.project.modular.configfield.mapper.ConfigFieldFiducialMapper;
import com.cj.project.modular.configfield.service.ConfigFieldFiducialService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 考证字段配置Service接口实现类
 *
 * @author Lb
 * @date  2023/08/31 19:28
 **/
@Service
public class ConfigFieldFiducialServiceImpl extends ServiceImpl<ConfigFieldFiducialMapper, ConfigFieldFiducial> implements ConfigFieldFiducialService {

    @Override
    public List<ConfigFieldFiducialResult> getList(ConfigFieldFiducialQueryDto configFieldFiducialQueryDto) {
        List<ConfigFieldFiducialResult> fieldFiducialResults = new ArrayList<>();
        //List
        QueryWrapper<ConfigFieldFiducial> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(configFieldFiducialQueryDto.getProjectCode())) {
            queryWrapper.lambda().eq(ConfigFieldFiducial::getProjectCode, configFieldFiducialQueryDto.getProjectCode());
        }
        if(ObjectUtil.isNotEmpty(configFieldFiducialQueryDto.getInstrumentType())) {
            queryWrapper.lambda().eq(ConfigFieldFiducial::getInstrumentType, configFieldFiducialQueryDto.getInstrumentType());
        }
        queryWrapper.lambda().orderByAsc(ConfigFieldFiducial::getSortCode);
        List<ConfigFieldFiducial> configFieldFiducials = this.list(queryWrapper);
        //Map
        Map<String,List<ConfigFieldFiducial>> fieldmap = configFieldFiducials.stream().collect(Collectors.groupingBy(ConfigFieldFiducial::getInstrumentType));
        for (String instrumenttype : fieldmap.keySet()
             ) {
            ConfigFieldFiducial defaultFieldFiducial = fieldmap.get(instrumenttype).stream().findFirst().get();
            ConfigFieldFiducialResult fiducialResult = new ConfigFieldFiducialResult();
            fiducialResult.setProjectCode(defaultFieldFiducial.getProjectCode());
            fiducialResult.setInstrumentType(instrumenttype);
            fiducialResult.setInstrumentMetaType(defaultFieldFiducial.getInstrumentMetaType());
            fiducialResult.setFieldConfigs(fieldmap.get(instrumenttype));
            fieldFiducialResults.add(fiducialResult);
        }
        return fieldFiducialResults;
    }

    @Override
    public Page<ConfigFieldFiducial> page(ConfigFieldFiducialPageDto configFieldFiducialPageDto) {
        QueryWrapper<ConfigFieldFiducial> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(configFieldFiducialPageDto.getProjectCode())) {
            queryWrapper.lambda().eq(ConfigFieldFiducial::getProjectCode, configFieldFiducialPageDto.getProjectCode());
        }
        if(ObjectUtil.isNotEmpty(configFieldFiducialPageDto.getInstrumentType())) {
            queryWrapper.lambda().eq(ConfigFieldFiducial::getInstrumentType, configFieldFiducialPageDto.getInstrumentType());
        }
        if(ObjectUtil.isAllNotEmpty(configFieldFiducialPageDto.getSortField(), configFieldFiducialPageDto.getSortOrder())) {
            CommonSortOrderEnum.validate(configFieldFiducialPageDto.getSortOrder());
            queryWrapper.orderBy(true, configFieldFiducialPageDto.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(configFieldFiducialPageDto.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(ConfigFieldFiducial::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ConfigFieldFiducial configFieldFiducial) {
        this.save(configFieldFiducial);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ConfigFieldFiducial configFieldFiducial) {
        this.queryEntity(configFieldFiducial.getId());
        this.updateById(configFieldFiducial);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<String> idList) {
        // 执行删除
        this.removeByIds(idList);
    }

    @Override
    public ConfigFieldFiducial detail(String id) {
        return this.queryEntity(id);
    }

    @Override
    public ConfigFieldFiducial queryEntity(String id) {
        ConfigFieldFiducial configFieldFiducial = this.getById(id);
        if(ObjectUtil.isEmpty(configFieldFiducial)) {
            throw new CommonException("考证字段配置不存在，id值为：{}", id);
        }
        return configFieldFiducial;
    }

    @SneakyThrows
    @Override
    public void templateExport(ConfigFieldFiducialDto configFieldFiducialDto, HttpServletRequest request, HttpServletResponse response) {
        List<ConfigFieldFiducial> configFieldFiducials = baseMapper.selectList(Wrappers.<ConfigFieldFiducial>lambdaQuery()
                .eq(ConfigFieldFiducial::getProjectCode, configFieldFiducialDto.getProjectCode())
                .eq(ConfigFieldFiducial::getInstrumentType, configFieldFiducialDto.getInstrumentType())
                .eq(ConfigFieldFiducial::getInstrumentMetaType, configFieldFiducialDto.getInstrumentMetaType())
                .eq(ConfigFieldFiducial::getIsDisplay, "1")
                .orderByAsc(ConfigFieldFiducial::getSortCode)
        );
        if (CollectionUtils.isNotEmpty(configFieldFiducials)){
            List textList = new ArrayList<>();
            for(int i = 0 ; i < configFieldFiducials.size() ; i ++){
                Map nameMap = new HashMap();
                nameMap.put("text" , configFieldFiducials.get(i).getFieldText());
                nameMap.put("row" , 1);
                nameMap.put("col" , i +1);
                nameMap.put("cell" , configFieldFiducials.get(i).getFieldText().length() * 50);
                nameMap.put("isRequired" , configFieldFiducials.get(i).getIsRequired());
                textList.add(nameMap);
            }
            List mapList = new ArrayList<>();
            mapList.add(textList);

            List keyList = new ArrayList<>();

            for (ConfigFieldFiducial configFieldFiducial : configFieldFiducials) {
                Map nameMap = new HashMap();
                nameMap.put("text" , configFieldFiducial.getFieldKey());
                keyList.add(nameMap);
            }

            mapList.add(keyList);
            String name = configFieldFiducialDto.getInstrumentType();
            XSSFWorkbook workbook = ExcelUtils.createExcel(name , mapList);
            if (org.springframework.util.StringUtils.isEmpty(name)) {
                name = java.net.URLEncoder.encode("nameless", "UTF-8");
            } else {
                name = java.net.URLEncoder.encode(name, "UTF-8");
            }
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("content-disposition", "attachment;filename=" + name + ".xlsx");
            ServletOutputStream outPut = response.getOutputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(outPut);
            byte[] content = os.toByteArray();
            InputStream inPut = new ByteArrayInputStream(content);
            IOUtils.copy(inPut, outPut);
        }

    }

    @SneakyThrows
    @Override
    public CommonResult dataImport(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        //获取文件后缀名
        String suffixName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        //判断是否为excel格式
        InputStream inputStream = file.getInputStream();

        Workbook wb = null;
        if ("xls".equals(suffixName)) {
            wb = new HSSFWorkbook(inputStream);
        } else if ("xlsx".equals(suffixName)) {
            wb = new XSSFWorkbook(inputStream);
        } else {
            // 无效后缀名称，这里之能保证excel的后缀名称，不能保证文件类型正确，不过没关系，在创建Workbook的时候会校验文件格式
            return CommonResult.error("Invalid excel version");
        }

        if(wb != null){
            Sheet sheetAt = wb.getSheetAt(0);
            //获取公共字段的列序号
            Row row1 = sheetAt.getRow(1);
            HashMap<String , Integer> commonFieldOrderMap = new HashMap();
            for(int i = 0 ; i < row1.getLastCellNum() ; i ++){
                commonFieldOrderMap.put(row1.getCell(i).toString() , i );
            }


            int lastRowNum = sheetAt.getLastRowNum();
            for (int i = 0 ; i < lastRowNum ; i ++){
                Row row = sheetAt.getRow(i);
                for (int j = 0 ; j < row.getLastCellNum() ; j ++){
                    System.out.println("第" + i + "行  " + "第" + j + "列的值  " + row.getCell(j) );
                }

            }
        }
        return CommonResult.ok("导入成功");
    }
}
