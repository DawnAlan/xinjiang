package com.cj.model.func.modular.FloodPredict.entity;

import lombok.Data;

@Data
public class TemporaryXlsx {

    private String path;//文件路径

    private String sheetName;//Sheet名称

    private String updateFilePath;
    private String updateParamPath;
    private String updateMaxPath;
}
