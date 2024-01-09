package com.cj.fourPredictions.func.modular.flood.video.bean.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetRegionsVo implements Serializable {

    /**
     * indexCode : 85551e49b2664a90a7bd2710fec9d5ae
     * name : 海康sdk
     * parentIndexCode : 36b1c09dfb1e484898cf56584bd81f67
     * externalIndexCode : 320100XXXXXXXX000XXXXXXXXX
     * treeCode : 0
     */

    private String indexCode;
    private String name;
    private String parentIndexCode;
    private String externalIndexCode;
    private String treeCode;
}
