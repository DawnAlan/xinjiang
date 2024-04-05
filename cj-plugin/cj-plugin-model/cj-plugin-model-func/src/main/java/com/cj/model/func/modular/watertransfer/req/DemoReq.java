package com.cj.model.func.modular.watertransfer.req;

import com.cj.model.func.modular.watertransfer.entity.ExcelDemo;
import lombok.Data;

import java.util.List;

@Data
public class DemoReq {
    //方案名称
    private String name;
    //表格数据
    private List<ExcelDemo> excelDemoData;
}
