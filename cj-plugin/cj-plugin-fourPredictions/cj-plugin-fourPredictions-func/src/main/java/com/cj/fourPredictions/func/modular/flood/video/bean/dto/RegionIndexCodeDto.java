package com.cj.fourPredictions.func.modular.flood.video.bean.dto;

import com.cj.fourPredictions.func.modular.flood.video.bean.vo.RegionIndexCodeVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *根据区域编号获取下级监控点列表
 */
@Data
public class RegionIndexCodeDto implements Serializable {
    private Integer total;

    private Integer pageNo;

    private Integer pageSize;

    private List<RegionIndexCodeVo> list;
}
