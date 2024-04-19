package com.cj.approval.func.modular.approval.approvalManagement.bean.res;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SendMsgRes implements Serializable {

    private String sendUnit;

    private String sendBy;

    private Date sendTime;

    private String sendContent;
}
