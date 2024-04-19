package com.cj.model.func.modular.FloodPrevent.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class Option {
    /**
     * 水库名
     */
    @Excel(name = "name",width = 15,orderNum = "1")
    private String name;
    /**
     * 方案类型
     */
    @Excel(name = "type",width = 15,orderNum = "2")
    private String type;
    /**
     * 时间
     */
    @Excel(name = "time",width = 15,orderNum = "3",format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date time;
    /**
     * 入库流量
     */
    @Excel(name = "qIn",width = 15,orderNum = "4")
    private double qIn;
    /**
     * 时段初水位
     */
    @Excel(name = "h1",width = 15,orderNum = "5")
    private double h1;
    /**
     * 时段末水位
     */
    @Excel(name = "h2",width = 15,orderNum = "6")
    private double h2;
    /**
     * 出库流量
     */
    @Excel(name = "qOut",width = 15,orderNum = "7")
    private double qOut;
    /**
     * 1号闸门流量
     */
    @Excel(name = "q1",width = 15,orderNum = "8")
    private double q1;
    /**
     * 2号闸门流量
     */
    @Excel(name = "q2",width = 15,orderNum = "9")
    private double q2;
    /**
     * 3号闸门流量
     */
    @Excel(name = "q3",width = 15,orderNum = "10")
    private double q3;
    /**
     * 库容
     */
    @Excel(name = "v",width = 15,orderNum = "11")
    private double v;
    /**
     * 拦蓄洪量
     */
    @Excel(name = "retain",width = 15,orderNum = "12")
    private double retain;
    /**
     * 防洪库容
     */
    @Excel(name = "percentage1",width = 15,orderNum = "13")
    private double percentage1;
    /**
     * 调洪库容
     */
    @Excel(name = "percentage2",width = 15,orderNum = "14")
    private double percentage2;
    /**
     * 汛限水位
     */
    @Excel(name = "limits",width = 15,orderNum = "15")
    private String limitString;


    private double remainQ;
    private List<Double> limits;
    private double min;
    private double max;


}
