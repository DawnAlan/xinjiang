package com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.bean.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RealFlowRes {

    private Integer month;

    private Integer name;

    private BigDecimal flow;
}
