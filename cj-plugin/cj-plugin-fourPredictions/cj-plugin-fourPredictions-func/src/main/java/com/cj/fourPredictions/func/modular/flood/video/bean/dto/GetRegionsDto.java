package com.cj.fourPredictions.func.modular.flood.video.bean.dto;

import com.cj.fourPredictions.func.modular.flood.video.bean.vo.GetRegionsVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * 分页获取区域列表
 */
@Data
public class GetRegionsDto implements Serializable {
    private Integer total;

    private Integer pageNo;

    private Integer pageSize;

    private List<GetRegionsVo> list;
}
