package com.cj.dataSynchronization.func.modular.tth.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class OperateTimeDto implements Serializable {

    private Integer nanos;
    private Long time;
    private Integer minutes;
    private Integer seconds;
    private Integer hours;
    private Integer month;
    private Integer timezoneOffset;
    private Integer year;
    private Integer day;
    private Integer date;
}
