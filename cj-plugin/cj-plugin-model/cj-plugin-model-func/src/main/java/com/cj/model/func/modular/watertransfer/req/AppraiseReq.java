package com.cj.model.func.modular.watertransfer.req;

import com.cj.model.func.modular.watertransfer.entity.Excel1;
import com.cj.model.func.modular.watertransfer.entity.Excel2;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class AppraiseReq {
    //方案名称
    private String name;
    //时段类型
    private String period;
    //配水类型
    private int id;
    //开始时间
    private Date startTime;
    //结束时间
    private Date endTime;
    //楼庄子开始水位
    private double levelBeginLzz;
    //楼庄子结束水位
    private double levelEndLzz;
    //头屯河开始水位
    private double levelBeginTth;
    //头屯河结束水位
    private double levelEndTth;
    //表1数据
    private List<Excel1> excel1Data;
    //表2数据
    private List<Excel2> excel2Data;
}
