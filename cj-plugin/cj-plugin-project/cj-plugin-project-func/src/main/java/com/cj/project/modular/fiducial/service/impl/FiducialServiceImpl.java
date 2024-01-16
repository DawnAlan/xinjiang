package com.cj.project.modular.fiducial.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.common.enums.SystemTypeEnum;
import com.cj.common.pojo.CommonResult;
import com.cj.common.util.ExcelUtils;
import com.cj.common.util.FormatCheckUtil;
import com.cj.common.util.MapTransformUtil;
import com.cj.project.api.configfield.dto.ConfigFieldFiducialDto;
import com.cj.project.api.configfield.entity.ConfigFieldFiducial;
import com.cj.project.api.fiducial.entity.FiducialBase;
import com.cj.project.api.fiducial.entity.FiducialPara;
import com.cj.project.api.fiducial.param.*;
import com.cj.project.modular.configfield.service.ConfigFieldFiducialService;
import com.cj.project.modular.fiducial.result.FiducialResult;
import com.cj.project.modular.fiducial.service.FiducialBaseService;
import com.cj.project.modular.fiducial.service.FiducialParaService;
import com.cj.project.modular.fiducial.service.FiducialService;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 测点考证表Service接口实现类
 *
 * @author Lb
 * @date  2023/09/04 12:25
 **/
@Service
public class FiducialServiceImpl implements FiducialService {

    @Resource
    private FiducialBaseService fiducialBaseService;

    @Resource
    private FiducialParaService fiducialParaService;

    @Resource
    private ConfigFieldFiducialService configFieldFiducialService;

    @Autowired
    MapTransformUtil mapTransformUtil;

    @Autowired
    FormatCheckUtil formatCheckUtil;

    @Override
    public Page<FiducialResult> page(Page<FiducialBase> fiducialBasePage) {
        Page<FiducialResult> result = new Page<>();
        BeanUtils.copyProperties(fiducialBasePage,result);
        /*result.setTotal(fiducialBasePage.getTotal());
        result.setSize(fiducialBasePage.getSize());
        result.setTotal(fiducialBasePage.getTotal());
        result.setCurrent(fiducialBasePage.getCurrent());
        result.setOrders(fiducialBasePage.getOrders());
        result.setMaxLimit(fiducialBasePage.getMaxLimit());*/

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(String projectCode, String instrumentID, Map<String,Object> fieldMaps) {
        //Base
        FiducialBase fiducialBase = BeanUtil.toBean(fieldMaps, FiducialBase.class);
        fiducialBase.setInstrumentType(instrumentID);
        fiducialBaseService.add(fiducialBase);
        String fiducialId = fiducialBase.getId();
        //分离Base、para字段
        Map<String,Object> paramsMap = fieldMaps;
        Field[] baseFields = ReflectUtil.getFields(FiducialBase.class);
        for (Field field : baseFields) {
            if(fieldMaps.containsKey(field.getName()))
                paramsMap.remove(field.getName());
        }
        //para
        List<FiducialParaAddParam> paraFieldList = new ArrayList<>();
        for (String mapkey :
                paramsMap.keySet()){
            FiducialParaAddParam para = new FiducialParaAddParam();
            para.setPointId(fiducialId);
            para.setFieldKey(mapkey);
            para.setFieldValue(paramsMap.get(mapkey).toString());
            paraFieldList.add(para);
        }
        fiducialParaService.adds(paraFieldList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void adds(List<FiducialAddParam> fiducialAddParamList) {
        for (FiducialAddParam fiducialAddParam : fiducialAddParamList
             ) {
            List<Map<String,Object>> fieldMaps = fiducialAddParam.getDetail();
            for (Map<String,Object> pointFieldMaps : fieldMaps
                 ) {
                //Base
                FiducialBase fiducialBase = BeanUtil.toBean(pointFieldMaps, FiducialBase.class);
                fiducialBase.setProjectCode(fiducialAddParam.getProjectCode());
                fiducialBase.setInstrumentType(fiducialAddParam.getInstrumentType());
                fiducialBaseService.add(fiducialBase);
                String fiducialId = fiducialBase.getId();
                //分离Base、para字段
                Map<String,Object> paramsMap = pointFieldMaps;
                Field[] baseFields = ReflectUtil.getFields(FiducialBase.class);
                for (Field field : baseFields) {
                    if(pointFieldMaps.containsKey(field.getName()))
                        paramsMap.remove(field.getName());
                }
                //para
                List<FiducialParaAddParam> paraFieldList = new ArrayList<>();
                for (String mapkey :
                        paramsMap.keySet()){
                    if(ObjectUtil.isEmpty(paramsMap.get(mapkey)))
                        continue;
                    FiducialParaAddParam para = new FiducialParaAddParam();
                    para.setPointId(fiducialId);
                    para.setFieldKey(mapkey);
                    para.setFieldValue(paramsMap.get(mapkey).toString());
                    paraFieldList.add(para);
                }
                fiducialParaService.adds(paraFieldList);
            }

        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addsByMap(List<Map<String, Object>>  fiducialAddList) {
        for (Map<String,Object> pointFieldMaps : fiducialAddList
        ) {
            //Base
            FiducialBase fiducialBase = BeanUtil.toBean(pointFieldMaps, FiducialBase.class);
            fiducialBaseService.add(fiducialBase);
            String fiducialId = fiducialBase.getId();
            //分离Base、para字段
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.putAll(pointFieldMaps);
            Field[] baseFields = ReflectUtil.getFields(FiducialBase.class);
            for (Field field : baseFields) {
                if(pointFieldMaps.containsKey(field.getName()))
                    paramsMap.remove(field.getName());
            }
            //para
            List<FiducialParaAddParam> paraFieldList = new ArrayList<>();
            for (String mapkey :
                    paramsMap.keySet()){
                if(ObjectUtil.isEmpty(paramsMap.get(mapkey)))
                    continue;
                FiducialParaAddParam para = new FiducialParaAddParam();
                para.setPointId(fiducialId);
                para.setFieldKey(mapkey);
                para.setFieldValue(paramsMap.get(mapkey).toString());
                paraFieldList.add(para);
            }
            fiducialParaService.adds(paraFieldList);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(FiducialBaseEditParam fiducialBaseEditParam) {
        /*FiducialBase fiducialBase = this.queryEntity(fiducialBaseEditParam.getId());
        BeanUtil.copyProperties(fiducialBaseEditParam, fiducialBase);
        this.updateById(fiducialBase);*/
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<String> fiducialIdList) {
        // 执行删除Base
        fiducialBaseService.delete(fiducialIdList);
        // 执行删除Para
        fiducialParaService.deleteByPoint(fiducialIdList);
    }

    @Override
    public FiducialResult ToFiducial(FiducialBase fiducialBase, List<FiducialPara> fiducialParas) {
        FiducialResult fiducialResult = new FiducialResult();
        fiducialResult.setId(fiducialBase.getId());
        fiducialResult.setProjectCode(fiducialBase.getProjectCode());
        fiducialResult.setInstrumentType(fiducialBase.getInstrumentType());
        fiducialResult.setPointName(fiducialBase.getPointName());
        fiducialResult.setPointAlias(fiducialBase.getPointAlias());
        Map<String, Object> detail = BeanUtil.beanToMap(fiducialBase);
        //Para
        for (FiducialPara para : fiducialParas
        ) {
            detail.put(para.getFieldKey(),para.getFieldValue());
        }
        fiducialResult.setDetail(detail);

        return fiducialResult;
    }

    @SneakyThrows
    @Override
    public void templateExport(ConfigFieldFiducialDto configFieldFiducialDto, HttpServletRequest request, HttpServletResponse response) {
        List<ConfigFieldFiducial> configFieldFiducials = configFieldFiducialService.list(Wrappers.<ConfigFieldFiducial>lambdaQuery()
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

    @Transactional(rollbackFor = Exception.class)
    @SneakyThrows
    @Override
    public CommonResult dataImport(ConfigFieldFiducialDto configFieldFiducialDto , MultipartFile file) {

        String projectCode = configFieldFiducialDto.getProjectCode();
        String instrumentType = configFieldFiducialDto.getInstrumentType();

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
            return CommonResult.error("无效后缀名称！");
        }

        if(wb != null){
            Sheet sheetAt = wb.getSheetAt(0);
            //获取公共字段的列序号
            Row row1 = sheetAt.getRow(1);
            Map<String , Object> fieldOrderMap = new HashMap();
            for(int i = 0 ; i < row1.getLastCellNum() ; i ++){
                fieldOrderMap.put(row1.getCell(i).toString() , i );
            }
            //分离Base、para字段
            Map<String , Object> baseMap = new HashMap();
            Map<String , Object> paraMap = new HashMap();
            paraMap.putAll(fieldOrderMap);

            Field[] baseFields = ReflectUtil.getFields(FiducialBase.class);
            for (Field field : baseFields) {
                if(fieldOrderMap.containsKey(field.getName())){
                    baseMap.put(field.getName() , fieldOrderMap.get(field.getName()));
                    paraMap.remove(field.getName());
                }
            }

            //查看拓展字段是否存在
            Map<String , Object> paraMap1 = new HashMap();
            paraMap1.putAll(paraMap);
            List<ConfigFieldFiducial> configFieldFiducials = configFieldFiducialService.list(Wrappers.<ConfigFieldFiducial>lambdaQuery()
                    .eq(ConfigFieldFiducial::getProjectCode, projectCode)
                    .eq(ConfigFieldFiducial::getInstrumentType, instrumentType)
            );
            for(ConfigFieldFiducial configFieldFiducial : configFieldFiducials){
                if(paraMap1.containsKey(configFieldFiducial.getFieldKey())){
                    paraMap1.remove(configFieldFiducial.getFieldKey());
                }
            }
            Set<String> fieldKeySet = paraMap1.keySet();
            if(fieldKeySet.size() > 0){
                String msg = String.join(",",fieldKeySet);
                return CommonResult.error(msg + "字段不存在！请删除Excel表中这两列！");
            }
            //反转map的key与value
            Map<Object, Object> newBaseMap = baseMap.entrySet().stream().collect(Collectors.toMap(entry -> entry.getValue(), entry -> entry.getKey()));
            Map<Object, Object> newParaMap = paraMap.entrySet().stream().collect(Collectors.toMap(entry -> entry.getValue(), entry -> entry.getKey()));
            //获取拓展字段格式Map
            Map<String , String> paraFormatMap = new HashMap<>();
            configFieldFiducials.forEach( e -> paraFormatMap.put(e.getFieldKey() , e.getSystemType()));

            DateFormat dateFormat = new SimpleDateFormat("YYYY/MM/DD HH:mm:ss");
            int lastRowNum = sheetAt.getLastRowNum();
            for (int i = 2 ; i <= lastRowNum ; i ++){
                Row row = sheetAt.getRow(i);
                Map<String , Object> baseValueMap = new HashMap();
                List<FiducialPara> fiducialParaList = new ArrayList<>();
                for (int j = 0 ; j < row.getLastCellNum() ; j ++){
                    if(newBaseMap.get(j) != null){
                        String field = newBaseMap.get(j).toString();
                        //String value = row.getCell(j).toString();
                        if(paraFormatMap.get(newBaseMap.get(j).toString()).equals(SystemTypeEnum.DATE.getValue())){
                            try {
                                baseValueMap.put(field , dateFormat.format(row.getCell(j).getDateCellValue()));
                            }catch (Exception e){
                                return CommonResult.error("第" + (i + 1) + "行第"+ (j + 1)+"列的" + newBaseMap.get(j) + "字段值格式不正确!为" + paraFormatMap.get(newBaseMap.get(j).toString()) + "类型！");
                            }

                        }else if (paraFormatMap.get(newBaseMap.get(j).toString()).equals(SystemTypeEnum.DOUBLE.getValue())){
                            try {
                                baseValueMap.put(field , String.valueOf(row.getCell(j).getNumericCellValue()));
                            }catch (Exception e){
                                return CommonResult.error("第" + (i + 1) + "行第"+ (j + 1)+"列的" + newBaseMap.get(j) + "字段值格式不正确!为" + paraFormatMap.get(newBaseMap.get(j).toString()) + "类型！");
                            }
                        }else {
                            baseValueMap.put(field , row.getCell(j).toString());
                        }

                    }else if(newParaMap.get(j) != null){
                        FiducialPara fiducialPara = new FiducialPara();
                        fiducialPara.setFieldKey(newParaMap.get(j).toString());

                        if(paraFormatMap.get(newParaMap.get(j).toString()).equals(SystemTypeEnum.DATE.getValue())){
                            try {
                                fiducialPara.setFieldValue(dateFormat.format(row.getCell(j).getDateCellValue()));
                            }catch (Exception e){
                                return CommonResult.error("第" + (i + 1) + "行第"+ (j + 1)+"列的" + newParaMap.get(j) + "字段值格式不正确!为" + paraFormatMap.get(newBaseMap.get(j).toString()) + "类型！");
                            }

                        }else if (paraFormatMap.get(newParaMap.get(j).toString()).equals(SystemTypeEnum.DOUBLE.getValue())){
                            try {
                                fiducialPara.setFieldValue(String.valueOf(row.getCell(j).getNumericCellValue()));
                            }catch (Exception e){
                                return CommonResult.error("第" + (i + 1) + "行第"+ (j + 1)+"列的" + newParaMap.get(j) + "字段值格式不正确!为" + paraFormatMap.get(newBaseMap.get(j).toString()) + "类型！");
                            }
                        }else {
                            fiducialPara.setFieldValue(row.getCell(j).toString());
                        }

                        fiducialParaList.add(fiducialPara);
                    }
                }
                //拓展字段值格式校验
                /*for (FiducialPara fiducialPara : fiducialParaList) {
                    String fieldKey = fiducialPara.getFieldKey();
                    String fieldValue = fiducialPara.getFieldValue();
                    String systemType = paraFormatMap.get(fieldKey);
                    Boolean msg = formatCheckUtil.checkFormat(fieldValue, systemType);
                    if(!msg)
                        return CommonResult.error("第" + (i + 1) + "行的" + fieldKey + "字段值格式不正确!为" + systemType + "类型！");

                }*/


                Map resultMap = mapTransformUtil.mapTransformClass(baseValueMap, FiducialBase.class);
                if(resultMap.get("Object") != null){
                    FiducialBase fiducialBase = (FiducialBase)resultMap.get("Object");
                    fiducialBase.setProjectCode(projectCode);
                    fiducialBase.setInstrumentType(instrumentType);
                    fiducialBaseService.save(fiducialBase);

                    fiducialParaList.stream().forEach(e -> e.setPointId(fiducialBase.getId()));
                    fiducialParaService.saveBatch(fiducialParaList);
                }else if(resultMap.get("String") != null){
                    String fieldKey = resultMap.get("String").toString();
                    List<ConfigFieldFiducial> configFieldFiducialList = configFieldFiducialService.list(Wrappers.<ConfigFieldFiducial>lambdaQuery()
                            .eq(ConfigFieldFiducial::getProjectCode, projectCode)
                            .eq(ConfigFieldFiducial::getInstrumentType, instrumentType)
                            .eq(ConfigFieldFiducial::getFieldKey, fieldKey)
                    );
                    if(CollectionUtils.isNotEmpty(configFieldFiducialList)){
                        String systemType = configFieldFiducialList.get(0).getSystemType();
                        return CommonResult.error("第" + (i + 1) + "行的" + fieldKey + "字段值格式不正确!为" + systemType + "类型！");
                    }



                }

            }
        }
        return CommonResult.ok("导入成功");
    }

}
