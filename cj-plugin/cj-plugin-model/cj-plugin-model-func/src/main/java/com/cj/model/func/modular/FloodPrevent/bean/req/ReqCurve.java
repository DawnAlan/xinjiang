package com.cj.model.func.modular.FloodPrevent.bean.req;

import com.cj.model.func.modular.FloodPrevent.entity.CurveParam;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReqCurve {

    private Map<String,List<CurveParam>> capacityCurves;
    private Map<String,Map<String, List<CurveParam>>> gateCurves;
}
