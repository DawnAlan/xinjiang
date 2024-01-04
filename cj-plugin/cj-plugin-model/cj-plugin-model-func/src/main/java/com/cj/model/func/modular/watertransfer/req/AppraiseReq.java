package com.cj.model.func.modular.watertransfer.req;

import com.cj.model.func.modular.watertransfer.entity.Excel2;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class AppraiseReq {
    //时段类型
    private String period;
    //配水类型
    private int id;
    //开始时间
    private Date startTime;
    //结束时间
    private Date endTime;
    //表2数据
    private List<Excel2> excel2Data;
}
