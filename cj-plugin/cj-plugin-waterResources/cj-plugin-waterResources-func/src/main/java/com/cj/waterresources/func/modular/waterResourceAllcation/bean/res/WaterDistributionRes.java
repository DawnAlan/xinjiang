package com.cj.waterresources.func.modular.waterResourceAllcation.bean.res;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class WaterDistributionRes implements Serializable {

    private Double ecologyProportion;
    private Double cityProportion;
    private Double industryProportion;
    private Double irrigateProportion;
    private Double greeningProportion;
    private String time;
}
