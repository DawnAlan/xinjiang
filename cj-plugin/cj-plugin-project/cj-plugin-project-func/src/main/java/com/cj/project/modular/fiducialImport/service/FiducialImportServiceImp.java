package com.cj.project.modular.fiducialImport.service;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.cj.project.modular.fiducialImport.Excel.NoModelDataListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class FiducialImportServiceImp implements FiducialImportService {

    @Override
    public List<Map<String, String>> ImportFiducial(String fileName) {
        // 引入监听器（此处需注意，监听器不可被Spring管理）
        NoModelDataListener readListener = new NoModelDataListener();
        // 开始处理excel
        EasyExcelFactory.read(fileName, readListener)
                .sheet(0)
                .doRead();
        // 获取表头（验空）
        Map<Integer, String> headList = readListener.getHeadList();
        if (CollectionUtils.isEmpty(headList)) {
            throw new RuntimeException("Excel表头不能为空");
        }
        // 获取表数据(验空)
        List<Map<Integer, String>> dataList = readListener.getDataList();
        if (CollectionUtils.isEmpty(dataList)) {
            throw new RuntimeException("Excel数据内容不能为空");
        }
        //封装数据体
        List<Map<String, String>> excelDataList = new ArrayList<Map<String, String>>();
        for (Map<Integer, String> dataRow : dataList) {
            HashMap<String, String> rowData = new HashMap<>();
            headList.entrySet().forEach(columnHead -> {
                rowData.put(columnHead.getValue(), dataRow.get(columnHead.getKey()));
            });
            excelDataList.add(rowData);
        }
        return excelDataList;
    }

    @Override
    public String GetFiducialFieldExcel(String projectCode, String instrumentType){
        return "";
    }

    @Override
    public String GetFiducialFieldExcel(Map<String, String> fieldMap,String fileName){
        //head
        // List<List<String>> headlist = ListUtils.newArrayList();
        // for (String fieldKey : fieldMap.keySet()
        //      ) {
        //     List<String> head = ListUtils.newArrayList();
        //     head.add(fieldKey);
        //     headlist.add(head);
        // }
        List<String> headKey = ListUtils.newArrayList();

        List<List<String>> dataList = ListUtils.newArrayList();
        List<String> headText = ListUtils.newArrayList();
        for (String fieldKey : fieldMap.keySet()
        ) {
            headKey.add(fieldKey);
            headText.add(fieldMap.get(fieldKey));
        }
        dataList.add(headKey);
        dataList.add(headText);
        if(ObjectUtil.isEmpty(fileName))
            fileName = System.getProperty("user.dir")  + "/noModelWrite" + System.currentTimeMillis() + ".xlsx";
        EasyExcel.write(fileName)
                //.head(headlist)
                .sheet("sheet1").doWrite(dataList);
        return fileName;
    }
}
