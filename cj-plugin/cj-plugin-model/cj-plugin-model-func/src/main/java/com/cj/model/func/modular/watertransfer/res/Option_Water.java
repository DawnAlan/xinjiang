package com.cj.model.func.modular.watertransfer.res;
import lombok.Data;
import java.util.Date;

@Data

public class Option_Water {
    private String id;
    //    时间
    private Date Time;
//    站点类型
    private String StationType;
    //    站点名称
    private String StationName;
    //    类型
    private String TypeName;
    //    配水
    private Double Water;
}


