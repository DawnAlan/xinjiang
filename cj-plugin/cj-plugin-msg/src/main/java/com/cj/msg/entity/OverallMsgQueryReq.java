package com.cj.msg.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverallMsgQueryReq {
    private Date starTime;
    private Date endTime;
    private int pageNo;
    private int pageSize;
}
